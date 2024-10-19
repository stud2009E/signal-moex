package pab.ta.handler.moex.service;

import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pab.ta.handler.base.asset.Direction;
import pab.ta.handler.base.asset.TimeFrame;
import pab.ta.handler.base.component.task.BaseTaskHandler;
import pab.ta.handler.base.task.SignalSelector;
import pab.ta.handler.base.task.SignalStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static pab.ta.handler.base.asset.CandleInterval.*;
import static pab.ta.handler.base.component.rule.RuleGroup.*;

@Component
@RequiredArgsConstructor
public class MoexSignalSelector implements SignalSelector {

    private final SignalStore store;
    private final BaseTaskHandler handler;

    @PostConstruct
    public void initialize() {
        handler.register(this);
    }

    @Subscribe
    @Override
    public void selectOnEvent(TimeFrame tf) {
        getFilters()
                .stream()
                .filter(filter -> filter.rules().containsKey(tf.interval()))
                .forEach(filter -> {
                    Set<String> result = store.findTicker(filter);
                    logResult(result, filter);
                });
    }

    @Override
    public List<SignalFilter> getFilters() {
        return List.of(
                new SignalFilter(
                        Map.of(
                                HOUR_4, List.of(RSI, CCI),
                                DAY, List.of(RSI, CCI, MFI),
                                WEEK, List.of(RSI, CCI, MFI)),
                        Direction.BUY
                ),
                new SignalFilter(
                        Map.of(
                                HOUR_4, List.of(RSI, CCI),
                                DAY, List.of(RSI, CCI, MFI),
                                WEEK, List.of(RSI, CCI, MFI)),
                        Direction.SELL
                ),
                new SignalFilter(
                        Map.of(
                                HOUR_4, List.of(RSI, CCI_EXTREMUM),
                                DAY, List.of(RSI, CCI_EXTREMUM)),
                        Direction.BUY
                ),
                new SignalFilter(
                        Map.of(
                                HOUR_4, List.of(RSI, CCI_EXTREMUM),
                                DAY, List.of(RSI, CCI_EXTREMUM)),
                        Direction.SELL
                )
        );
    }
}
