package pab.ta.handler.moex.provider.util;

import pab.ta.handler.base.lib.asset.CandleInterval;

import java.time.Duration;


/**
 * Convert {@link CandleInterval} to different representation
 */
public class IntervalConverter {

    private final CandleInterval interval;

    public IntervalConverter(CandleInterval candleInterval) {
        this.interval = candleInterval;
    }

    public Duration duration() {
        return switch (interval) {
            case HOUR_1 -> Duration.ofHours(1);
            case HOUR_2 -> Duration.ofHours(2);
            case HOUR_4 -> Duration.ofHours(4);
            case WEEK -> Duration.ofDays(7);
            case DAY -> Duration.ofDays(1);
            case MONTH -> null;
        };
    }


    public String moexInterval() {
        return switch (interval) {
            case HOUR_1 -> "60";
            case HOUR_2 -> "60";
            // moex api does not have 4 hour interval
            // use 1 hour interval data and transform to 4h
            case HOUR_4 -> "60";
            case DAY -> "24";
            case WEEK -> "7";
            case MONTH -> "31";
        };
    }

    @Override
    public String toString() {
        return interval.toString();
    }
}
