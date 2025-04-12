package pab.ta.handler.moex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pab.ta.handler.base.lib.asset.provider.DataProvider;
import pab.ta.handler.moex.provider.ProviderMOEX;

@Configuration
public class Config {

    @Bean
    public DataProvider moexDataProvider() {
        return new ProviderMOEX();
    }
}