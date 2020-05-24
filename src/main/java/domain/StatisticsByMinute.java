package domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsByMinute {
    private final ConcurrentHashMap<Integer, Statistics> statisticsBySecond;
    private final Clock clock;

    public StatisticsByMinute(ConcurrentHashMap<Integer, Statistics> statisticsBySecond, Clock clock) {
        this.statisticsBySecond = statisticsBySecond;
        this.clock = clock;
    }

    public void storeTransaction(BigDecimal amount, Instant timestamp) {
        int timestampSecond = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.computeIfPresent(timestampSecond, (key, value) -> value.update(amount, timestamp));
    }

    public Statistics statistics() {
        List<Statistics> lastMinuteStatistics = new ArrayList<>(statisticsBySecond.values());

        Instant instant = clock.instant();
        int lastMinuteCount = lastMinuteCount(lastMinuteStatistics, instant);

        return new Statistics(
                lastMinuteSum(lastMinuteStatistics, instant),
                lastMinuteAverage(lastMinuteStatistics, instant),
                lastMinuteMax(lastMinuteStatistics, instant),
                lastMinuteMin(lastMinuteStatistics, instant),
                lastMinuteCount
        );
    }

    private BigDecimal lastMinuteAverage(List<Statistics> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .avg();
    }

    private BigDecimal lastMinuteSum(List<Statistics> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .sum();
    }

    private BigDecimal lastMinuteMax(List<Statistics> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .max();
    }

    private BigDecimal lastMinuteMin(List<Statistics> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .min();
    }


    private int lastMinuteCount(List<Statistics> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .count();
    }
}
