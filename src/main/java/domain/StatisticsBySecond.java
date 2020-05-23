package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class StatisticsBySecond {

    private Instant creationInstant;
    private int count;
    private BigDecimal average = BigDecimal.ZERO;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal max = BigDecimal.valueOf(Long.MIN_VALUE);
    private BigDecimal min = BigDecimal.valueOf(Long.MAX_VALUE);

    public StatisticsBySecond(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public int count() {
        return count;
    }

    public void logTransaction(BigDecimal amount, Instant instant) {
        count = count + 1;
        average = rollingAverage(amount);
        sum = round(sum.add(amount));
        max = findNewMax(amount);
        min = findNewMin(amount);
    }

    public BigDecimal average() {
        return average;
    }

    public BigDecimal sum() {
        return sum;
    }

    public BigDecimal max() {
        return max;
    }

    public BigDecimal min() {
        return min;
    }

    private BigDecimal findNewMax(BigDecimal amount) {
        if (amount.compareTo(max) > 0) {
            return round(amount);
        }
        return max;
    }

    private BigDecimal findNewMin(BigDecimal amount) {
        if (amount.compareTo(max) < 0) {
            return round(amount);
        }
        return min;
    }

    private BigDecimal rollingAverage(BigDecimal amount) {
        return ((this.average.multiply(BigDecimal.valueOf(count - 1)))
                .add(amount))
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
