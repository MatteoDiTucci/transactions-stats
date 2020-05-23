package domain;

import java.math.BigDecimal;

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
}
