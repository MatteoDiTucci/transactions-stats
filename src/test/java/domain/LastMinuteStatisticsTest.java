package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LastMinuteStatisticsTest {
    private final Instant instantWithSecond24 = Instant.parse("2020-05-23T18:00:24.000Z");
    private StatisticsBySecond statisticsForSecond24 = new StatisticsBySecond(instantWithSecond24);
    private StatisticsByMinute statisticsByMinute;

    @BeforeEach
    void setUp() {
        int instantSecond = LocalDateTime.ofInstant(instantWithSecond24, ZoneId.of("Europe/Rome")).getSecond();
        statisticsByMinute = new StatisticsByMinute(Map.of(instantSecond, statisticsForSecond24));
    }

    @Test
    void storeTransactionBySecond() {
        BigDecimal amount = new BigDecimal("123.456");

        statisticsByMinute.logTransaction(amount, instantWithSecond24);

        assertEquals(1, statisticsForSecond24.count());
    }
}