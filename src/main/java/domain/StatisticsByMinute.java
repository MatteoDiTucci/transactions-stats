package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class StatisticsByMinute {
    private Map<Integer, StatisticsBySecond> statisticsBySecond;
    public int count;

    public StatisticsByMinute(Map<Integer, StatisticsBySecond> statisticsBySecond) {
        this.statisticsBySecond = statisticsBySecond;
    }

    public void logTransaction(BigDecimal amount, Instant instant) {
        int instantSecond = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Rome")).getSecond();
        statisticsBySecond.get(instantSecond).logTransaction(amount, instant);
    }
}
