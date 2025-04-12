package pab.ta.handler.moex.provider;

import pab.ta.handler.base.lib.asset.AssetInfo;
import pab.ta.handler.base.lib.asset.AssetType;
import pab.ta.handler.base.lib.asset.BaseAssetInfo;
import ru.exdata.moex.IssClient;
import ru.exdata.moex.IssClientBuilder;
import ru.exdata.moex.response.Block;
import ru.exdata.moex.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AssetInfoMoexProviderBase {

    protected final IssClient client;

    public AssetInfoMoexProviderBase() {
        client = IssClientBuilder.builder().build();
    }

    protected List<AssetInfo> handleRequest(Response response, AssetType type) {
        if (response.findBlock("securities").isEmpty()) {
            return List.of();
        }
        Block block = response.findBlock("securities").get();
        List<String> columns = block.getColumns();

        return block.getData().stream()
                .map(data -> {
                    String ticker = "";
                    String description = "";
                    Map<String, Object> properties = new HashMap<>();

                    for (int i = 0; i < columns.size(); i++) {
                        String property = columns.get(i);

                        switch (property.toLowerCase()) {
                            case "secid":
                                ticker = data.get(i).toString();
                                break;
                            case "shortname":
                                description = data.get(i).toString();
                                break;
                            //to filter share by listing level
                            case "listlevel":
                            //additional info for futures
                            case "lasttradedate":
                            case "assetcode":
                                properties.put(property.toLowerCase(), data.get(i).toString());
                                break;
                        }
                    }

                    return new BaseAssetInfo(ticker, ticker, type, description, properties);
                })
                .map(baseAssetInfo -> (AssetInfo) baseAssetInfo)
                .toList();
    }
}
