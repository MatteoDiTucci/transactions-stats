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
    private final ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond;
    private final Clock clock;

    public StatisticsByMinute(ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond, Clock clock) {
        this.statisticsBySecond = statisticsBySecond;
        this.clock = clock;
    }

    public void storeTransaction(BigDecimal amount, Instant timestamp) {
        int instantSecond = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.get(instantSecond).storeTransaction(amount, timestamp);
    }

    public Statistics statistics() {
        List<StatisticsBySecond> lastMinuteStatistics = new ArrayList<>(statisticsBySecond.values());

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

    private BigDecimal lastMinuteAverage(List<StatisticsBySecond> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .map(statisticsBySecond1 -> statisticsBySecond1.statistics(instant))
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .avg();
    }

    private BigDecimal lastMinuteSum(List<StatisticsBySecond> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .map(statisticsBySecond1 -> statisticsBySecond1.statistics(instant))
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .sum();
    }

    private BigDecimal lastMinuteMax(List<StatisticsBySecond> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .map(statisticsBySecond1 -> statisticsBySecond1.statistics(instant))
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .max();
    }

    private BigDecimal lastMinuteMin(List<StatisticsBySecond> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .map(statisticsBySecond1 -> statisticsBySecond1.statistics(instant))
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .min();
    }


    private int lastMinuteCount(List<StatisticsBySecond> lastMinuteStatistics, Instant instant) {
        return lastMinuteStatistics.stream()
                .map(statisticsBySecond1 -> statisticsBySecond1.statistics(instant))
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .count();
    }
}
