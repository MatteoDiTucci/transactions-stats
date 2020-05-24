package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsBySecondTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:24.000Z");

    private StatisticsBySecond statisticsBySecond;

    @BeforeEach
    void setUp() {
        statisticsBySecond = new StatisticsBySecond(BASE_INSTANT, Statistics.EMPTY_STATISTICS);
    }
}