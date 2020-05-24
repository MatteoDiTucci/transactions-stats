package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LastMinuteStatisticsBySecondTest {
    private final Instant instant = Instant.parse("2020-05-23T18:00:24.000Z");
    private StatisticsBySecond statisticsBySecond;

    @BeforeEach
    void setUp() {
        statisticsBySecond = new StatisticsBySecond(instant);
    }

    @Test
    void keepTrackOfTransactionCount() {
        statisticsBySecond.storeTransaction(new BigDecimal("123.456"), instant);

        assertEquals(1, statisticsBySecond.count());
    }

    @Test
    void keepTrackOfTransactionAverage() {
        BigDecimal expected = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);
        statisticsBySecond.storeTransaction(new BigDecimal("2"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("3"), instant);

        assertEquals(expected, statisticsBySecond.average());
    }

    @Test
    void roundsAverageAt2Decimals() {
        statisticsBySecond.storeTransaction(new BigDecimal("5"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("2"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("3"), instant);

        assertEquals("3.33", statisticsBySecond.average().toString());
    }

    @Test
    void keepTrackOfAmountSum() {
        BigDecimal expected = BigDecimal.valueOf(21).setScale(2, RoundingMode.HALF_UP);
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("10.24"), instant);

        assertEquals(expected, statisticsBySecond.sum());
    }

    @Test
    void roundSumAt2Decimals() {
        BigDecimal expected = BigDecimal.valueOf(21).setScale(2, RoundingMode.HALF_UP);
        statisticsBySecond.storeTransaction(new BigDecimal("10.761"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("10.242"), instant);

        assertEquals(expected, statisticsBySecond.sum());
    }

    @Test
    void keepTracksOfMaxAmount() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("10.77"), instant);

        assertEquals(BigDecimal.valueOf(10.77), statisticsBySecond.max());
    }

    @Test
    void keepTracksOfMaxAmountWithNegativeAmount() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("-75.27"), instant);

        assertEquals(BigDecimal.valueOf(10.76), statisticsBySecond.max());
    }

    @Test
    void roundMaxAt2Decimals() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("10.773"), instant);

        assertEquals(BigDecimal.valueOf(10.77), statisticsBySecond.max());
    }

    @Test
    void keepTracksOfMinAmount() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("5.32"), instant);

        assertEquals(BigDecimal.valueOf(5.32), statisticsBySecond.min());
    }

    @Test
    void keepTracksOfMinAmountWithNegativeAmounts() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("-32.52"), instant);

        assertEquals(BigDecimal.valueOf(-32.52), statisticsBySecond.min());
    }

    @Test
    void roundMinAt2Decimals() {
        statisticsBySecond.storeTransaction(new BigDecimal("10.76"), instant);
        statisticsBySecond.storeTransaction(new BigDecimal("5.3234567"), instant);

        assertEquals(BigDecimal.valueOf(5.32), statisticsBySecond.min());
    }
}