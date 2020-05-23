package domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Statistics {
    public BigDecimal sum;
    public BigDecimal avg;
    public BigDecimal max;
    public BigDecimal min;
    public int count;

    public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, int count) {
        this.sum = sum;
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statistics that = (Statistics) o;
        return count == that.count &&
                Objects.equals(sum, that.sum) &&
                Objects.equals(avg, that.avg) &&
                Objects.equals(max, that.max) &&
                Objects.equals(min, that.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum, avg, max, min, count);
    }
}
