package pab.ta.handler.moex.provider.util;

import pab.ta.handler.base.asset.CandleInterval;

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
            case WEEK -> Duration.ofDays(7);
            case DAY -> Duration.ofDays(1);
            case HOUR_4 -> Duration.ofHours(4);
            case HOUR_2 -> Duration.ofHours(2);
        };
    }


    public String moexInterval() {
        return switch (interval) {
            case WEEK -> "7";
            case DAY -> "24";
            // moex api does not have 4 hour interval
            // use 1 hour interval data and transform to 4h
            case HOUR_4 -> "60";
            case HOUR_2 -> "60";
        };
    }

    @Override
    public String toString() {
        return switch (interval) {
            case WEEK -> "1w";
            case DAY -> "1d";
            case HOUR_4 -> "4h";
            case HOUR_2 -> "2h";
        };
    }
}
