package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsByMinuteTest {
    private final Instant instantWithSecond24 = Instant.parse("2020-05-23T18:00:24.000Z");
    private final Instant instantWithSecond45 = Instant.parse("2020-05-23T18:00:45.000Z");
    private StatisticsBySecond statisticsForSecond24 = new StatisticsBySecond(instantWithSecond24);
    private StatisticsBySecond statisticsForSecond45 = new StatisticsBySecond(instantWithSecond45);
    private StatisticsByMinute statisticsByMinute;

    @BeforeEach
    void setUp() {
        int instantSecond24 = LocalDateTime.ofInstant(instantWithSecond24, ZoneId.of("Europe/Rome")).getSecond();
        int instantSecond45 = LocalDateTime.ofInstant(instantWithSecond45, ZoneId.of("Europe/Rome")).getSecond();
        statisticsByMinute = new StatisticsByMinute(Map.of(instantSecond24, statisticsForSecond24, instantSecond45, statisticsForSecond45));
    }

    @Test
    void storeTransactionBySecond() {
        BigDecimal amount = new BigDecimal("123.456");

        statisticsByMinute.logTransaction(amount, instantWithSecond24);

        assertEquals(1, statisticsForSecond24.count());
    }

    @Test
    void returnLastMinuteStatistics() {
        Statistics expected = new Statistics(BigDecimal.valueOf(883.37), BigDecimal.valueOf(441.69),
                BigDecimal.valueOf(759.91), BigDecimal.valueOf(123.46), 2);

        statisticsByMinute.logTransaction(new BigDecimal("123.456"), instantWithSecond24);
        statisticsByMinute.logTransaction(new BigDecimal("759.91"), instantWithSecond45);

        Statistics result = statisticsByMinute.statistics();

        assertEquals(expected, result);
    }

    @Test
    void returnZeroWhenNoDataAreAvailable() {
        Statistics expected = new Statistics(BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, 0);

        Statistics result = statisticsByMinute.statistics();

        assertEquals(expected, result);
    }
}