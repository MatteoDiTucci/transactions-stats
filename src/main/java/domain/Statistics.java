package domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

public class Statistics {
    private final BigDecimal sum;
    private final BigDecimal avg;
    private final BigDecimal max;
    private final BigDecimal min;
    private final int count;

    public static Statistics EMPTY_STATISTICS = new Statistics(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0);

    public Statistics(BigDecimal sum, BigDecimal avg, BigDecimal max, BigDecimal min, int count) {
        this.sum = round(sum);
        this.avg = round(avg);
        this.max = round(max);
        this.min = round(min);
        this.count = count;
    }

    public Statistics update(BigDecimal amount, Instant timestamp) {
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

    public BigDecimal sum() {
        return sum;
    }

    public BigDecimal avg() {
        return avg;
    }

    public BigDecimal max() {
        return max;
    }

    public BigDecimal min() {
        return min;
    }

    public int count() {
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
        return thisStatistics.sum().add(thatStatistics.sum());
    }

    private BigDecimal aggregatedAverage(Statistics statistics, int aggregatedCount) {
        if (aggregatedCount == 0) {
            return BigDecimal.ZERO;
        }
        return statistics.avg().multiply(BigDecimal.valueOf(statistics.count()))
                .divide(BigDecimal.valueOf(aggregatedCount), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal aggregatedMax(Statistics thisStatistics, Statistics thatStatistics) {
        if (thisStatistics.max().compareTo(thatStatistics.max()) > 0) {
            return thisStatistics.max();
        }
        return thatStatistics.max();
    }

    private BigDecimal aggregatedMin(Statistics thisStatistics, Statistics thatStatistics) {
        if (thisStatistics == EMPTY_STATISTICS) {
            return thatStatistics.min();
        }
        if (thatStatistics == Statistics.EMPTY_STATISTICS) {
            return thisStatistics.min();
        }
        if (thisStatistics.min().compareTo(thatStatistics.min()) < 0) {
            return thisStatistics.min();
        }
        return thatStatistics.min();
    }

    private int aggregatedCount(Statistics thisStatistics, Statistics thatStatistics) {
        return thisStatistics.count() + thatStatistics.count();
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
