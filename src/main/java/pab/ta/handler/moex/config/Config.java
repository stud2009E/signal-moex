package pab.ta.handler.moex.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import pab.ta.handler.base.component.task.BaseSignalSelector;
import pab.ta.handler.base.component.task.BaseSignalStore;
import pab.ta.handler.base.provider.DataProvider;
import pab.ta.handler.moex.provider.ProviderMOEX;

@Configuration
@ComponentScan(basePackages = "pab.ta.handler.base.component",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                BaseSignalStore.class,
                                BaseSignalSelector.class
                        }
                )
        }
)
@EnableCaching
public class Config {

    @Bean
    public DataProvider moexDataProvider() {
        return new ProviderMOEX();
    }
}