package application;

import domain.StatisticsByMinute;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;

@Singleton
public class StoreTransaction {
    private final StatisticsByMinute statisticsByMinute;

    public StoreTransaction(StatisticsByMinute statisticsByMinute) {
        this.statisticsByMinute = statisticsByMinute;
    }

    public void store(BigDecimal amount, Instant timestamp) {
        statisticsByMinute.storeTransaction(amount, timestamp);
    }
}
