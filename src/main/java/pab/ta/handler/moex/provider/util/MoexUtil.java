package pab.ta.handler.moex.provider.util;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import pab.ta.handler.base.lib.asset.CandleInterval;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class MoexUtil {

    public interface Engine {
        String stock = "stock";
        String currency = "currency";
        String futures = "futures";
    }

    public interface Market {
        String shares = "shares";
        String index = "index";
        String forts = "forts";
    }

    public interface Board {
        String tqbr = "TQBR";
        String sndx = "SNDX";
        String rfud = "RFUD";
        String fixi = "FIXI";
    }


    /**
     * map raw data from moex to Bar object
     *
     * @param columns        - list of property names
     * @param candleInterval interval
     * @return mapper function
     */
    public static Function<List<Object>, Bar> getBarMapper(List<String> columns, CandleInterval candleInterval) {
        var barBuilder = BaseBar.builder();
        var converter = new IntervalConverter(candleInterval);
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return data -> {
            for (int i = 0; i < columns.size(); i++) {
                String property = data.get(i).toString();

                switch (columns.get(i)) {
                    case "open":
                        barBuilder.openPrice(DecimalNum.valueOf(Double.parseDouble(property)));
                        break;
                    case "close":
                        barBuilder.closePrice(DecimalNum.valueOf(Double.parseDouble(property)));
                        break;
                    case "high":
                        barBuilder.highPrice(DecimalNum.valueOf(Double.parseDouble(property)));
                        break;
                    case "low":
                        barBuilder.lowPrice(DecimalNum.valueOf(Double.parseDouble(property)));
                        break;
                    case "volume":
                        barBuilder.volume(DecimalNum.valueOf(Long.parseLong(property)));
                        break;
                    case "begin":
                        //'begin' replaced with time period and end of period
                        barBuilder.timePeriod(converter.duration());
                        break;
                    case "end":
                        LocalDateTime ldt = LocalDateTime.parse(property, formatter);
                        barBuilder.endTime(ldt.atZone(ZoneId.systemDefault()));
                        break;
                }
            }

            return barBuilder.build();
        };
    }

    /**
     * merge bars into one bar
     *
     * @param candleInterval interval
     * @return merged bar
     */
    public static Function<List<Bar>, Bar> getBarMerger(CandleInterval candleInterval) {

        var barBuilder = BaseBar.builder();
        var converter = new IntervalConverter(candleInterval);

        return bars -> {
            Bar lastBar = bars.getLast();
            Bar firstBar = bars.getFirst();

            ZonedDateTime endTime = lastBar.getEndTime();
            Double lowPrice = firstBar.getLowPrice().doubleValue();
            Double highPrice = firstBar.getHighPrice().doubleValue();

            Num openPrice = firstBar.getOpenPrice();
            Num closePrice = lastBar.getClosePrice();

            Optional<Double> lowPriceOptional = bars.stream()
                    .map(bar -> bar.getLowPrice().doubleValue())
                    .min((v1, v2) -> (int) (v1 - v2));
            if (lowPriceOptional.isPresent()) {
                lowPrice = lowPriceOptional.get();
            }

            Optional<Double> highPriceOptional = bars.stream()
                    .map(bar -> bar.getHighPrice().doubleValue())
                    .min((v1, v2) -> (int) (v1 - v2));

            if (highPriceOptional.isPresent()) {
                highPrice = highPriceOptional.get();
            }

            Double volume = bars.stream().mapToDouble(bar -> bar.getVolume().doubleValue()).sum();

            return barBuilder
                    .lowPrice(DecimalNum.valueOf(lowPrice))
                    .highPrice(DecimalNum.valueOf(highPrice))
                    .openPrice(openPrice)
                    .closePrice(closePrice)
                    .volume(DecimalNum.valueOf(volume))
                    .endTime(endTime)
                    .timePeriod(converter.duration())
                    .build();
        };
    }
}