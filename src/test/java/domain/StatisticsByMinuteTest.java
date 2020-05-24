package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsByMinuteTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private final Instant instantWithSecond24 = BASE_INSTANT.plusSeconds(24);
    private final Instant instantWithSecond45 = BASE_INSTANT.plusSeconds(45);
    private final Instant instantOlderThanOneMinute = BASE_INSTANT.minusSeconds(90);

    private Clock clock;
    private StatisticsByMinute statisticsByMinute;
    private ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond;

    @BeforeEach
    void setUp() {
        int instantSecond24 = LocalDateTime.ofInstant(instantWithSecond24, ZoneId.of("Europe/Rome")).getSecond();
        int instantSecond45 = LocalDateTime.ofInstant(instantWithSecond45, ZoneId.of("Europe/Rome")).getSecond();
        int instantSecondOlderThanOneMinute = LocalDateTime.ofInstant(instantOlderThanOneMinute, ZoneId.of("Europe/Rome")).getSecond();

        clock = Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome"));
        statisticsBySecond = new ConcurrentHashMap<>();
        statisticsBySecond.put(instantSecond24, new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS));
        statisticsBySecond.put(instantSecond45, new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS));
        statisticsBySecond.put(instantSecondOlderThanOneMinute, new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS));

        statisticsByMinute = new StatisticsByMinute(statisticsBySecond, clock);
    }

    @Test
    void storeTransactionBySecond() {
        statisticsByMinute.storeTransaction(new BigDecimal("123.456"), instantWithSecond24);

        assertEquals(1, statisticsBySecond.get(24).statistics(BASE_INSTANT).getCount());
        assertEquals(0, statisticsBySecond.get(45).statistics(BASE_INSTANT).getCount());
    }

    @Test
    void returnLastMinuteStatistics() {
        Statistics expected = new Statistics(BigDecimal.valueOf(883.37), BigDecimal.valueOf(441.69),
                BigDecimal.valueOf(759.91), BigDecimal.valueOf(123.46), 2);

        statisticsByMinute.storeTransaction(new BigDecimal("123.456"), instantWithSecond24);
        statisticsByMinute.storeTransaction(new BigDecimal("759.91"), instantWithSecond45);

        Statistics result = statisticsByMinute.statistics();

        assertEquals(expected, result);
    }

    @Test
    void returnBaseLineValuesWhenNoDataAreAvailable() {
        Statistics expected = new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(Integer.MIN_VALUE),
                BigDecimal.valueOf(Integer.MAX_VALUE), 0);

        Statistics result = statisticsByMinute.statistics();

        assertEquals(expected, result);
    }

    @Test
    void wipeAllStatisticsBySecondWhenDeleting() {
        statisticsByMinute.storeTransaction(new BigDecimal("123.456"), instantWithSecond24);

        statisticsByMinute.delete();

        assertEquals(0, statisticsByMinute.statistics().getCount());
    }
}