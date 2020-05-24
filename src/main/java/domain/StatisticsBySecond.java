package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class StatisticsBySecond {

    private final Instant creationInstant;
    private final AtomicReference<Statistics> statistics;

    public StatisticsBySecond(Instant creationInstant, Statistics statistics) {
        this.creationInstant = creationInstant;
        this.statistics = new AtomicReference<>(statistics);
    }

    public void storeTransaction(BigDecimal amount, Instant instant) {
        statistics.set(statistics.get().update(amount));
    }

    public Statistics statistics() {
        return this.statistics.get();
    }
}