package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsByMinute {
    private final ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond;

    public StatisticsByMinute(ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond) {
        this.statisticsBySecond = statisticsBySecond;
    }

    public void storeTransaction(BigDecimal amount, Instant timestamp) {
        int instantSecond = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.get(instantSecond).storeTransaction(amount, timestamp);
    }

    public Statistics statistics() {
        List<StatisticsBySecond> lastMinuteStatistics = new ArrayList<>(statisticsBySecond.values());

        int lastMinuteCount = lastMinuteCount(lastMinuteStatistics);

        return new Statistics(
                lastMinuteSum(lastMinuteStatistics),
                lastMinuteAverage(lastMinuteStatistics),
                lastMinuteMax(lastMinuteStatistics),
                lastMinuteMin(lastMinuteStatistics),
                lastMinuteCount
        );
    }

    private BigDecimal lastMinuteAverage(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::statistics)
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .avg();
    }

    private BigDecimal lastMinuteSum(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::statistics)
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .sum();
    }

    private BigDecimal lastMinuteMax(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::statistics)
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .max();
    }

    private BigDecimal lastMinuteMin(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::statistics)
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .min();
    }


    private int lastMinuteCount(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::statistics)
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .count();
    }
}
