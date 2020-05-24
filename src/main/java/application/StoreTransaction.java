package application;

import domain.StatisticsByMinute;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;

@Singleton
public class StoreTransaction {
    private final StatisticsByMinute statisticByMinute;

    public StoreTransaction(StatisticsByMinute statisticByMinute) {
        this.statisticByMinute = statisticByMinute;
    }

    public void store(BigDecimal amount, Instant timestamp) {
        statisticByMinute.storeTransaction(amount, timestamp);
    }
}
