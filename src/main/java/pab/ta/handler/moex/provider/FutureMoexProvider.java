package pab.ta.handler.moex.provider;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pab.ta.handler.base.asset.AssetInfo;
import pab.ta.handler.base.asset.AssetType;
import pab.ta.handler.base.provider.AssetInfoProvider;
import pab.ta.handler.moex.provider.util.MoexUtil;
import ru.exdata.moex.Request;
import ru.exdata.moex.response.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Component
public class FutureMoexProvider extends AssetInfoMoexProviderBase implements AssetInfoProvider {

    @Override
    @Cacheable("future")
    public List<AssetInfo> info() {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.futures)
                .markets().market(MoexUtil.Market.forts)
                .boards().board(MoexUtil.Board.rfud)
                .securities()
                .format().json();

        return handleRequest(request.get(Map.of(
                        "iss.only", "securities",
                        "iss.meta", "off")),
                AssetType.FUTURE)
                .stream()
                .filter(assetInfo -> {
                    LocalDate lastTradeDate;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    try {
                        String rawDate = String.valueOf(assetInfo.properties().get("lasttradedate"));
                        lastTradeDate = LocalDate.parse(rawDate, formatter);
                    } catch (DateTimeParseException ignored) {
                        return true;
                    }

                    LocalDate eternalFutureDate = LocalDate.parse("2100-01-01", formatter);

                    return LocalDate.now().plusMonths(3).isAfter(lastTradeDate) ||
                            lastTradeDate.equals(eternalFutureDate) ;
                })
                .toList();
    }
}
