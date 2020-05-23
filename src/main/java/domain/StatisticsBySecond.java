package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class StatisticsBySecond {

    private Instant creationInstant;
    private int count;
    private BigDecimal average = BigDecimal.ZERO;

    public StatisticsBySecond(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public int count() {
        return count;
    }

    public void logTransaction(BigDecimal amount, Instant instant) {

        count = count + 1;
        average = rollingAverage(amount);
    }

    private BigDecimal rollingAverage(BigDecimal amount) {
        return ((this.average.multiply(BigDecimal.valueOf(count - 1)))
                .add(amount))
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal average() {
        return average;
    }
}
