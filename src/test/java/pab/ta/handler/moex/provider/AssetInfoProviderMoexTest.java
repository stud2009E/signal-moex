package pab.ta.handler.moex.provider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pab.ta.handler.base.provider.AssetInfoProvider;

public class AssetInfoProviderMoexTest {

    private AssetInfoProvider provider;

    @BeforeEach
    public void setup() {
        provider = new CurrencyMoexProvider();
    }

    @AfterEach
    public void cleanup() {
        provider = null;
    }

    @Test
    public void search() {
//        AssetInfoProvider mockProvider = Mockito.mock(AssetInfoProviderMoex.class);
//
//        Mockito.when(mockProvider.search("IMOEX")).thenReturn(List.of(
//                new BaseAssetInfo("", "IMOEX1", "imoex1 descr"),
//                new BaseAssetInfo("", "IMOEX2", "imoex2 descr"),
//                new BaseAssetInfo("", "IMOEX3", "imoex3 descr")
//        ));
//
//        List<AssetInfo> searchList = mockProvider.search("IMOEX");
//        Mockito.verify(mockProvider).search("IMOEX");
//        Assertions.assertEquals(3, searchList.size());
//
//
//        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.search(null));
//        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.search(""));
//        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.search("         "));
//        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.search("ad  "));
//        Assertions.assertThrows(IllegalArgumentException.class, () -> provider.search("  ad "));
    }

    @Test
    public void getShares() {
        //todo List<AssetInfo> assets = provider.getShares();

        Assertions.assertTrue(true);
    }

    @Test
    public void getIndexes() {
        //todo List<AssetInfo> assets = provider.getIndexes();

        Assertions.assertTrue(true);
    }

    @Test
    public void getCurrencies() {
        //todo List<AssetInfo> assets = provider.getCurrencies();

        Assertions.assertTrue(true);
    }

    @Test
    public void getFutures() {
        //todo List<AssetInfo> assets = provider.getFutures();

        Assertions.assertTrue(true);
    }
}
