package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = Statistics.EMPTY_STATISTICS;
    }

    @Test
    void returnUpdatedTransactionCount() {
        Statistics result = statistics.update(new BigDecimal("123.456"), BASE_INSTANT);

        assertEquals(1, result.count());
    }

    @Test
    void returnUpdatedTransactionAverage() {
        BigDecimal expected = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);
        Statistics result = statistics.update(new BigDecimal("2"), BASE_INSTANT)
                .update(new BigDecimal("3"), BASE_INSTANT);

        assertEquals(expected, result.avg());
    }

    @Test
    void roundsAverageAt2Decimals() {
        Statistics result = statistics.update(new BigDecimal("5"), BASE_INSTANT)
                .update(new BigDecimal("2"), BASE_INSTANT)
                .update(new BigDecimal("3"), BASE_INSTANT);

        assertEquals(new BigDecimal("3.33"), result.avg());
    }

    @Test
    void calculatesAverageForSingleAmount() {
        Statistics result = statistics.update(new BigDecimal("5"), BASE_INSTANT);

        assertEquals(new BigDecimal("5.00"), result.avg());
    }

    @Test
    void returnUpdatedAmountSum() {
        BigDecimal expected = BigDecimal.valueOf(21).setScale(2, RoundingMode.HALF_UP);
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("10.24"), BASE_INSTANT);

        assertEquals(expected, result.sum());
    }

    @Test
    void roundSumAt2Decimals() {
        BigDecimal expected = BigDecimal.valueOf(21).setScale(2, RoundingMode.HALF_UP);
        Statistics result =
                statistics.update(new BigDecimal("10.761"), BASE_INSTANT)
                        .update(new BigDecimal("10.242"), BASE_INSTANT);

        assertEquals(expected, result.sum());
    }

    @Test
    void returnUpdatedMaxAmount() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("10.77"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(10.77), result.max());
    }

    @Test
    void returnUpdatedMaxAmountWithNegativeAmount() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("-75.27"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(10.76), result.max());
    }

    @Test
    void roundMaxAt2Decimals() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("10.773"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(10.77), result.max());
    }

    @Test
    void ignoreMaxInitialisationValue() {
        Statistics result = statistics.update(new BigDecimal("-10.76"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(-10.76), result.max());
    }

    @Test
    void returnUpdatedMinAmount() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("-5.32"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(-5.32), result.min());
    }

    @Test
    void returnUpdatedMinAmountWithNegativeAmounts() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("-32.52"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(-32.52), result.min());
    }

    @Test
    void roundMinAt2Decimals() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT)
                .update(new BigDecimal("-5.3234567"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(-5.32), result.min());
    }

    @Test
    void ignoreMinInitialisationValue() {
        Statistics result = statistics.update(new BigDecimal("10.76"), BASE_INSTANT);

        assertEquals(BigDecimal.valueOf(10.76), result.min());
    }

    @Test
    void aggregatesByAverage() {
        Statistics statisticsToAggregate = new Statistics(BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456),
                BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456), 1);
        statistics = statistics.update(new BigDecimal("17.17"), BASE_INSTANT)
                .update(new BigDecimal("-74.22"), BASE_INSTANT);

        Statistics result = statistics.aggregate(statisticsToAggregate);

        assertEquals(BigDecimal.valueOf(22.13), result.avg());
    }

    @Test
    void aggregatesBySum() {
        Statistics statisticsToAggregate = new Statistics(BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456),
                BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456), 1);
        statistics = statistics.update(new BigDecimal("17.17"), BASE_INSTANT)
                .update(new BigDecimal("-74.22"), BASE_INSTANT);

        Statistics result = statistics.aggregate(statisticsToAggregate);

        assertEquals(BigDecimal.valueOf(66.41), result.sum());
    }

    @Test
    void aggregatesByMax() {
        Statistics statisticsToAggregate = new Statistics(BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456),
                BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456), 1);
        statistics = statistics.update(new BigDecimal("17.17"), BASE_INSTANT)
                .update(new BigDecimal("-74.22"), BASE_INSTANT);

        Statistics result = statistics.aggregate(statisticsToAggregate);

        assertEquals(BigDecimal.valueOf(123.46), result.max());
    }

    @Test
    void aggregatesByMin() {
        Statistics statisticsToAggregate = new Statistics(BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456),
                BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456), 1);
        statistics = statistics.update(new BigDecimal("17.17"), BASE_INSTANT)
                .update(new BigDecimal("-74.22"), BASE_INSTANT);

        Statistics result = statistics.aggregate(statisticsToAggregate);

        assertEquals(BigDecimal.valueOf(-74.22), result.min());
    }

    @Test
    void aggregatesByCount() {
        Statistics statisticsToAggregate = new Statistics(BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456),
                BigDecimal.valueOf(123.456), BigDecimal.valueOf(123.456), 1);
        statistics = statistics.update(new BigDecimal("17.17"), BASE_INSTANT)
                .update(new BigDecimal("-74.22"), BASE_INSTANT);

        Statistics result = statistics.aggregate(statisticsToAggregate);

        assertEquals(3, result.count());
    }

    @Test
    void roundConstructorInputTo2Decimals() {
        Statistics statistics = new Statistics(BigDecimal.valueOf(12.345), BigDecimal.valueOf(12.345),
                BigDecimal.valueOf(12.345), BigDecimal.valueOf(12.345), 1);

        assertEquals(BigDecimal.valueOf(12.35), statistics.avg());
        assertEquals(BigDecimal.valueOf(12.35), statistics.max());
        assertEquals(BigDecimal.valueOf(12.35), statistics.min());
        assertEquals(BigDecimal.valueOf(12.35), statistics.sum());
    }
}