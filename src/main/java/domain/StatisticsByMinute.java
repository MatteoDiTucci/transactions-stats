package domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StatisticsByMinute {
    private final ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond;
    private final Clock clock;

    public StatisticsByMinute(ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond, Clock clock) {
        this.statisticsBySecond = statisticsBySecond;
        this.clock = clock;
    }

    public void storeTransaction(BigDecimal amount, Instant timestamp) {
        int seconds = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.replace(seconds, statisticsBySecond.get(seconds).update(amount, timestamp));
    }

    public void delete() {
        statisticsBySecond.replaceAll((key, value) -> new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS));
    }

    public Statistics statistics() {
        Instant now = clock.instant();
        List<Statistics> lastMinuteStatistics =
                statisticsBySecond.values().stream()
                        .map(statisticsBySecond -> statisticsBySecond.statistics(now)).collect(Collectors.toList());

        int lastMinuteCount = lastMinuteCount(lastMinuteStatistics);

        return new Statistics(
                lastMinuteSum(lastMinuteStatistics),
                lastMinuteAverage(lastMinuteStatistics),
                lastMinuteMax(lastMinuteStatistics),
                lastMinuteMin(lastMinuteStatistics),
                lastMinuteCount
        );
    }

    private BigDecimal lastMinuteAverage(List<Statistics> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .getAvg();
    }

    private BigDecimal lastMinuteSum(List<Statistics> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .getSum();
    }

    private BigDecimal lastMinuteMax(List<Statistics> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .getMax();
    }

    private BigDecimal lastMinuteMin(List<Statistics> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .getMin();
    }


    private int lastMinuteCount(List<Statistics> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .reduce(Statistics.EMPTY_STATISTICS, Statistics::aggregate)
                .getCount();
    }
}
