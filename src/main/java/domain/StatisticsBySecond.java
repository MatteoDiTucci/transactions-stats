package domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

public class StatisticsBySecond {
    private final Clock clock;
    private final Instant creationInstant;
    private final Statistics statistics;

    public StatisticsBySecond(Clock clock, Statistics statistics) {
        this.clock = clock;
        this.creationInstant = clock.instant();
        this.statistics = statistics;
    }

    public StatisticsBySecond update(BigDecimal amount, Instant timestamp) {
        if (timestamp.isAfter(oneMinuteFromCreation())) {
            Statistics statistics = new Statistics(amount, amount, amount, amount, 1);
            return new StatisticsBySecond(this.clock, statistics);
        }
        return new StatisticsBySecond(this.clock, statistics.update(amount));
    }

    public Statistics statistics(Instant now) {
        if (now.isAfter(oneMinuteFromCreation())) {
            return Statistics.EMPTY_STATISTICS;
        }
        return this.statistics;
    }

    private Instant oneMinuteFromCreation() {
        return creationInstant.plusSeconds(59);
    }
}