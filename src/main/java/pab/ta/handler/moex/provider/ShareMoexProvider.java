package pab.ta.handler.moex.provider;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pab.ta.handler.base.lib.asset.AssetInfo;
import pab.ta.handler.base.lib.asset.AssetType;
import pab.ta.handler.base.lib.asset.provider.AssetInfoProvider;
import pab.ta.handler.moex.provider.util.MoexUtil;
import ru.exdata.moex.Request;
import ru.exdata.moex.response.Response;

import java.util.List;
import java.util.Map;

@Component
public class ShareMoexProvider extends AssetInfoMoexProviderBase implements AssetInfoProvider {

    @Override
    @Cacheable("share")
    public List<AssetInfo> info() {
        Request<Response> request = client.iss()
                .engines().engine(MoexUtil.Engine.stock)
                .markets().market(MoexUtil.Market.shares)
                .boards().board(MoexUtil.Board.tqbr)
                .securities()
                .format().json();

        return handleRequest(
                request.get(Map.of(
                        "iss.only", "securities",
                        "iss.meta", "off",
                        "securities.columns", "SECID,NAME,LISTLEVEL")),
                AssetType.SHARE)
                .stream()
                .filter(info -> !String.valueOf(info.getProperties().get("listlevel")).equals("3"))
                .toList();
    }
}