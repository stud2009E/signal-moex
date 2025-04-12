package pab.ta.handler.moex.provider;

import com.google.common.collect.Lists;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;
import pab.ta.handler.base.lib.asset.AssetInfo;
import pab.ta.handler.base.lib.asset.CandleInterval;
import pab.ta.handler.base.lib.asset.TimeFrame;
import pab.ta.handler.base.lib.asset.provider.DataProvider;
import pab.ta.handler.moex.provider.util.IntervalConverter;
import pab.ta.handler.moex.provider.util.MoexUtil;
import ru.exdata.moex.IssClient;
import ru.exdata.moex.IssClientBuilder;
import ru.exdata.moex.Request;
import ru.exdata.moex.response.Block;
import ru.exdata.moex.response.Response;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Data provider for MOEX ISS.
 * MOEX ISS request schema:
 * <pre>
 * https://iss.moex.com/iss
 * /engines/(trade_engine_name)
 * /markets/(market_name)
 * /boards/(boardid)
 * /boardgroups/(board_group_id)
 * /securities/(secid)
 * </pre>
 * <p>
 *
 * <a href="https://iss.moex.com/iss/index.html">Info</a>
 * information about trade_engine_name, market_name, boardid, board_group_id
 */
public class ProviderMOEX implements DataProvider {

    private final IssClient client;

    public ProviderMOEX() {
        client = IssClientBuilder.builder().build();
    }

    @Override
    public BarSeries getSeries(AssetInfo info, TimeFrame tf) {

        return switch (info.getType()) {
            case SHARE -> getShareSeries(info, tf);
            case INDEX -> getIndexSeries(info, tf);
            case FUTURE -> getFutureSeries(info, tf);
            case CURRENCY -> getCurrencySeries(info, tf);
        };
    }

    /**
     * Get share series
     * Example:
     * <a href="https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/SBER/candles.xml">SBER</a>
     *
     * @param data asset
     * @param tf   time frame
     * @return series of requested data
     */
    protected BarSeries getShareSeries(AssetInfo data, TimeFrame tf) {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.stock)
                .markets().market(MoexUtil.Market.shares)
                .boards().board(MoexUtil.Board.tqbr)
                .securities().security(data.getTicker())
                .candles().format().json();

        return handleRequest(request, data, tf);
    }


    /**
     * Get future series
     * Example:
     * <a href="https://iss.moex.com/iss/engines/futures/markets/forts/boards/RFUD/securities/NAZ4/candles.xml">NASD-12.24</a>
     *
     * @param data asset
     * @param tf   time frame
     * @return series of requested data
     */
    protected BarSeries getFutureSeries(AssetInfo data, TimeFrame tf) {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.futures)
                .markets().market(MoexUtil.Market.forts)
                .boards().board(MoexUtil.Board.rfud)
                .securities().security(data.getTicker())
                .candles().format().json();

        return handleRequest(request, data, tf);
    }

    /**
     * Currency fixing
     *
     * @param data asset
     * @param tf   time frame
     * @return bar series
     */
    protected BarSeries getCurrencySeries(AssetInfo data, TimeFrame tf) {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.currency)
                .markets().market(MoexUtil.Market.index)
                .boards().board(MoexUtil.Board.fixi)
                .securities().security(data.getTicker())
                .candles().format().json();

        return handleRequest(request, data, tf);
    }

    /**
     * Example:
     * <a href="https://iss.moex.com/iss/engines/stock/markets/index/boards/SNDX/securities/IMOEX/candles.xml">...</a>
     *
     * @param data asset
     * @param tf   time frame
     * @return bar series
     */
    protected BarSeries getIndexSeries(AssetInfo data, TimeFrame tf) {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.stock)
                .markets().market(MoexUtil.Market.index)
                .boards().board(MoexUtil.Board.sndx)
                .securities().security(data.getTicker())
                .candles().format().json();

        return handleRequest(request, data, tf);
    }


    private BarSeries handleRequest(Request<Response> request, AssetInfo data, TimeFrame tf) {
        CandleInterval interval = tf.getInterval();
        IntervalConverter converter = new IntervalConverter(interval);

        Response response = request.get(Map.of("from", tf.getFrom().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "till", tf.getTo().format(DateTimeFormatter.ISO_LOCAL_DATE),
                "interval", converter.moexInterval()));

        BarSeries series = new BaseBarSeriesBuilder()
                .withName(data.getTicker() + " " + interval.name())
                .withNumTypeOf(DecimalNum.class)
                .build();

        if (response.findBlock("candles").isEmpty()) {
            return series;
        }
        Block block = response.findBlock("candles").get();
        List<Bar> barList = block.getData().stream()
                .map(MoexUtil.getBarMapper(block.getColumns(), interval))
                .toList();

        if (interval == CandleInterval.HOUR_2) {
            barList = transform1hBar(barList, 2, interval);
        } else if (interval == CandleInterval.HOUR_4) {
            barList = transform1hBar(barList, 4, interval);
        }

        //minimal size of bar list
        if (barList.size() < 30) {
            return series;
        }

        barList.forEach(series::addBar);

        return series;
    }

    /**
     * transform 1h bar list into 2h or 4h bar list
     *
     * @param barList        1h bar list
     * @param groupCount     bar count for interval
     * @param candleInterval candle interval
     * @return transformed bar list
     */
    private List<Bar> transform1hBar(List<Bar> barList, int groupCount, CandleInterval candleInterval) {
        List<List<Bar>> listListBar = groupBarsDayBorder(barList);

        return listListBar.stream()
                .map(listBar -> Lists.partition(listBar, groupCount)
                        .stream()
                        .map(MoexUtil.getBarMerger(candleInterval))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * group bars with date time in the same day
     * in separate lists
     *
     * @param barList bar series
     * @return collection of list of bars
     */
    private List<List<Bar>> groupBarsDayBorder(List<Bar> barList) {
        return barList.stream().collect(
                        Collectors.groupingBy(
                                bar -> bar.getBeginTime().toLocalDate(),
                                Collectors.mapping(b -> b, Collectors.toList())
                        )
                )
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .peek(entry -> entry.getValue().sort(Comparator.comparing(Bar::getBeginTime)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
