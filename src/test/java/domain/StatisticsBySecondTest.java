package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsBySecondTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");

    private StatisticsBySecond statisticsBySecond;

    @BeforeEach
    void setUp() {
        statisticsBySecond = new StatisticsBySecond(BASE_INSTANT, Statistics.EMPTY_STATISTICS);
    }

    @Test
    void refreshStatisticsWhenCreationInstantIsOneMinuteOlderThanTransactionReceived() {
        statisticsBySecond.storeTransaction(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30));

        statisticsBySecond.storeTransaction(BigDecimal.TEN, BASE_INSTANT.plusSeconds(60));

        assertEquals(1, statisticsBySecond.statistics(BASE_INSTANT).count());
        assertEquals(new BigDecimal("10.00"), statisticsBySecond.statistics(BASE_INSTANT).sum());
    }

    @Test
    void refreshStatisticsWhenCreationInstantIsOverOneMinuteOlderThanTransactionReceived() {
        statisticsBySecond.storeTransaction(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30));

        statisticsBySecond.storeTransaction(BigDecimal.TEN, BASE_INSTANT.plusSeconds(61));

        assertEquals(1, statisticsBySecond.statistics(BASE_INSTANT).count());
        assertEquals(new BigDecimal("10.00"), statisticsBySecond.statistics(BASE_INSTANT).sum());
    }

    @Test
    void doNotRefreshStatisticsWhenCreationInstantIs59SecondsOlderThanTransactionReceived() {
        statisticsBySecond.storeTransaction(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30));

        statisticsBySecond.storeTransaction(BigDecimal.TEN, BASE_INSTANT.plusSeconds(59));

        assertEquals(2, statisticsBySecond.statistics(BASE_INSTANT).count());
        assertEquals(new BigDecimal("11.00"), statisticsBySecond.statistics(BASE_INSTANT).sum());
    }

    @Test
    void returnEmptyStatisticsIfTheyAreOlderThanOneMinute() {
        statisticsBySecond.storeTransaction(BigDecimal.ONE, BASE_INSTANT);

        Statistics statistics = statisticsBySecond.statistics(BASE_INSTANT.plusSeconds(90));

        assertEquals(Statistics.EMPTY_STATISTICS, statistics);
    }
}