package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsBySecondTest {
    private final Instant instant = Instant.parse("2020-05-23T18:00:24.000Z");
    private StatisticsBySecond statisticsBySecond;

    @BeforeEach
    void setUp() {
        statisticsBySecond = new StatisticsBySecond(instant);
    }

    @Test
    void keepTrackOfTransactionCount() {
        statisticsBySecond.logTransaction(new BigDecimal("123.456"), instant);

        assertEquals(1, statisticsBySecond.count());
    }

    @Test
    void keepTrackOfTransactionAverage() {
        BigDecimal expected = BigDecimal.valueOf(2.50).setScale(2, RoundingMode.HALF_UP);
        statisticsBySecond.logTransaction(new BigDecimal("2"), instant);
        statisticsBySecond.logTransaction(new BigDecimal("3"), instant);

        assertEquals(expected, statisticsBySecond.average());
    }

    @Test
    void roundsAverageAt2Decimals() {
        statisticsBySecond.logTransaction(new BigDecimal("5"), instant);
        statisticsBySecond.logTransaction(new BigDecimal("2"), instant);
        statisticsBySecond.logTransaction(new BigDecimal("3"), instant);

        assertEquals("3.33", statisticsBySecond.average().toString());
    }
}