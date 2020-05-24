package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class StatisticsBySecond {

    private final Instant creationInstant;
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicReference<BigDecimal> average = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> sum = new AtomicReference<>(BigDecimal.ZERO);
    private final AtomicReference<BigDecimal> max = new AtomicReference<>(BigDecimal.valueOf(Long.MIN_VALUE));
    private final AtomicReference<BigDecimal> min = new AtomicReference<>(BigDecimal.valueOf(Long.MAX_VALUE));

    public StatisticsBySecond(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public int count() {
        return count.get();
    }

    public void logTransaction(BigDecimal amount, Instant instant) {
        count.incrementAndGet();
        average.set(computeNewAverage(amount));
        sum.set(computeNewSum(amount));
        max.set(computeNewMax(amount));
        min.set(computeNewMin(amount));
    }

    public BigDecimal average() {
        return average.get();
    }

    public BigDecimal sum() {
        return sum.get();
    }

    public BigDecimal max() {
        return max.get();
    }

    public BigDecimal min() {
        return min.get();
    }

    private BigDecimal computeNewAverage(BigDecimal amount) {
        int count = this.count.get();
        return ((this.average.get().multiply(BigDecimal.valueOf(count - 1)))
                .add(amount))
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal computeNewSum(BigDecimal amount) {
        return round(sum.get().add(amount));
    }

    private BigDecimal computeNewMax(BigDecimal amount) {
        BigDecimal max = this.max.get();
        if (amount.compareTo(max) > 0) {
            return round(amount);
        }
        return max;
    }

    private BigDecimal computeNewMin(BigDecimal amount) {
        if (amount.compareTo(max.get()) < 0) {
            return round(amount);
        }
        return min.get();
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
