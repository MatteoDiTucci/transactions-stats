package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Statistics {
    public static Statistics EMPTY_STATISTICS =
            new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(Integer.MIN_VALUE),
                    BigDecimal.valueOf(Integer.MAX_VALUE), 0);

    private final BigDecimal sum;
    private final BigDecimal avg;
    private final BigDecimal max;
    private final BigDecimal min;
    private final int count;

    public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, int count) {
        this.sum = round(sum);
        this.avg = round(avg);
        this.max = round(max);
        this.min = round(min);
        this.count = count;
    }

    public Statistics update(BigDecimal amount) {
        return new Statistics(
                updatedSum(amount),
                updatedAverage(amount),
                updatedMax(amount),
                updatedMin(amount),
                this.count + 1);
    }

    public Statistics aggregate(Statistics thatStatistics) {
        int aggregatedCount = aggregatedCount(this, thatStatistics);

        return new Statistics(
                aggregatedSum(this, thatStatistics),
                aggregatedAverage(this, aggregatedCount).add(aggregatedAverage(thatStatistics, aggregatedCount)),
                aggregatedMax(this, thatStatistics),
                aggregatedMin(this, thatStatistics),
                aggregatedCount);
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public int getCount() {
        return count;
    }

    private BigDecimal updatedAverage(BigDecimal amount) {
        if (count == 0) {
            return amount;
        }

        return ((this.avg.multiply(BigDecimal.valueOf(count)))
                .add(amount))
                .divide(BigDecimal.valueOf(count + 1), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal updatedSum(BigDecimal amount) {
        return round(sum.add(amount));
    }

    private BigDecimal updatedMax(BigDecimal amount) {
        if (this == EMPTY_STATISTICS) {
            return round(amount);
        }
        if (amount.compareTo(this.max) > 0) {
            return round(amount);
        }
        return this.max;
    }

    private BigDecimal updatedMin(BigDecimal amount) {
        if (this == EMPTY_STATISTICS) {
            return round(amount);
        }
        if (amount.compareTo(min) < 0) {
            return round(amount);
        }
        return min;
    }

    private BigDecimal aggregatedSum(Statistics thisStatistics, Statistics thatStatistics) {
        return thisStatistics.getSum().add(thatStatistics.getSum());
    }

    private BigDecimal aggregatedAverage(Statistics statistics, int aggregatedCount) {
        if (aggregatedCount == 0) {
            return BigDecimal.ZERO;
        }
        return statistics.getAvg().multiply(BigDecimal.valueOf(statistics.getCount()))
                .divide(BigDecimal.valueOf(aggregatedCount), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal aggregatedMax(Statistics thisStatistics, Statistics thatStatistics) {
        if (thisStatistics.getMax().compareTo(thatStatistics.getMax()) > 0) {
            return thisStatistics.getMax();
        }
        return thatStatistics.getMax();
    }

    private BigDecimal aggregatedMin(Statistics thisStatistics, Statistics thatStatistics) {
        if (thisStatistics.getMin().compareTo(thatStatistics.getMin()) < 0) {
            return thisStatistics.getMin();
        }
        return thatStatistics.getMin();
    }

    private int aggregatedCount(Statistics thisStatistics, Statistics thatStatistics) {
        return thisStatistics.getCount() + thatStatistics.getCount();
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
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
