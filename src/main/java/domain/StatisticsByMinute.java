package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatisticsByMinute {
    private final ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond;

    public StatisticsByMinute(ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond) {
        this.statisticsBySecond = statisticsBySecond;
    }

    public void logTransaction(BigDecimal amount, Instant timestamp) {
        int instantSecond = LocalDateTime.ofInstant(timestamp, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.get(instantSecond).logTransaction(amount, timestamp);
    }

    public Statistics statistics() {
        List<StatisticsBySecond> lastMinuteStatistics = new ArrayList<>(statisticsBySecond.values());

        int lastMinuteCount = lastMinuteCount(lastMinuteStatistics);

        return new Statistics(
                lastMinuteSum(lastMinuteStatistics),
                lastMinuteAverage(lastMinuteStatistics, lastMinuteCount),
                lastMinuteMax(lastMinuteStatistics),
                lastMinuteMin(lastMinuteStatistics),
                lastMinuteCount
        );
    }

    private BigDecimal lastMinuteAverage(List<StatisticsBySecond> lastMinuteStatistics, int lastMinuteCount) {
        List<Integer> countsForAverages = lastMinuteStatistics.stream()
                .map(StatisticsBySecond::count).collect(Collectors.toList());

        return IntStream.range(0, lastMinuteStatistics.size()).boxed()
                .map(index -> {
                    BigDecimal average = lastMinuteStatistics.get(index).average();
                    Integer amountOfNumbersUsedInTheAverage = countsForAverages.get(index);
                    return weightedAverage(average, amountOfNumbersUsedInTheAverage, lastMinuteCount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal weightedAverage(BigDecimal average, int amountOfNumbersUsedInTheAverage, int lastMinuteCount) {
        if (lastMinuteCount == 0) {
            return BigDecimal.ZERO;
        }
        return average.multiply(BigDecimal.valueOf(amountOfNumbersUsedInTheAverage))
                .divide(BigDecimal.valueOf(lastMinuteCount), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal lastMinuteSum(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::sum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal lastMinuteMax(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::sum)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal lastMinuteMin(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::sum)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }


    private int lastMinuteCount(List<StatisticsBySecond> lastMinuteStatistics) {
        return lastMinuteStatistics.stream()
                .map(StatisticsBySecond::count)
                .reduce(0, Integer::sum);
    }
}
