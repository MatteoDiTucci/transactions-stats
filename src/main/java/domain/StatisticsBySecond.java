package domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class StatisticsBySecond {

    private final Instant creationInstant;
    private final AtomicReference<Statistics> statistics;

    public StatisticsBySecond(Instant creationInstant, Statistics statistics) {
        this.creationInstant = creationInstant;
        this.statistics = new AtomicReference<>(statistics);
    }

    public void storeTransaction(BigDecimal amount, Instant timestamp) {
        if(timestamp.isAfter(creationInstant.plusSeconds(59))) {
            statistics.set(Statistics.EMPTY_STATISTICS);
        }
        statistics.set(statistics.get().update(amount));
    }

    public Statistics statistics(Instant now) {
        if (now.isAfter(creationInstant.plusSeconds(59))) {
            return Statistics.EMPTY_STATISTICS;
        }
        return this.statistics.get();
    }
}