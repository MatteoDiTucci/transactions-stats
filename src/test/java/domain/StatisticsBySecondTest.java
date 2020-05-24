package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsBySecondTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private Clock clock;
    private StatisticsBySecond statisticsBySecond;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome"));
        statisticsBySecond = new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS);
    }

    @Test
    void refreshStatisticsWhenCreationInstantIsOneMinuteOlderThanTransactionReceived() {
        StatisticsBySecond result =
                statisticsBySecond.update(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30))
                        .update(BigDecimal.TEN, BASE_INSTANT.plusSeconds(60));

        assertEquals(1, result.statistics(BASE_INSTANT).getCount());
        assertEquals(new BigDecimal("10.00"), result.statistics(BASE_INSTANT).getSum());
    }

    @Test
    void refreshStatisticsWhenCreationInstantIsOverOneMinuteOlderThanTransactionReceived() {
        StatisticsBySecond result =
                statisticsBySecond.update(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30))
                        .update(BigDecimal.TEN, BASE_INSTANT.plusSeconds(61));

        assertEquals(1, result.statistics(BASE_INSTANT).getCount());
        assertEquals(new BigDecimal("10.00"), result.statistics(BASE_INSTANT).getSum());
    }

    @Test
    void doNotRefreshStatisticsWhenCreationInstantIs59SecondsOlderThanTransactionReceived() {
        StatisticsBySecond result =
                statisticsBySecond.update(BigDecimal.ONE, BASE_INSTANT.minusSeconds(30))
                        .update(BigDecimal.TEN, BASE_INSTANT.plusSeconds(59));

        assertEquals(2, result.statistics(BASE_INSTANT).getCount());
        assertEquals(new BigDecimal("11.00"), result.statistics(BASE_INSTANT).getSum());
    }

    @Test
    void returnEmptyStatisticsIfTheyAreOlderThanOneMinute() {
        statisticsBySecond.update(BigDecimal.ONE, BASE_INSTANT);

        Statistics statistics = statisticsBySecond.statistics(BASE_INSTANT.plusSeconds(90));

        assertEquals(Statistics.EMPTY_STATISTICS, statistics);
    }
}