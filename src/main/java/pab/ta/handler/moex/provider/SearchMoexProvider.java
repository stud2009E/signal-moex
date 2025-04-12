package pab.ta.handler.moex.provider;

import pab.ta.handler.base.lib.asset.AssetInfo;
import pab.ta.handler.base.lib.asset.provider.AssetInfoSearchProvider;
import ru.exdata.moex.Request;
import ru.exdata.moex.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SearchMoexProvider extends AssetInfoMoexProviderBase implements AssetInfoSearchProvider {
    @Override
    public List<AssetInfo> search(String query) {
        if (Objects.isNull(query) || query.isBlank() || query.trim().length() < 3) {
            throw new IllegalArgumentException();
        }

        Request<Response> request = client.iss().securities().format().json();

        return handleRequest(request.get(Map.of("q", query)), null);
    }
}
