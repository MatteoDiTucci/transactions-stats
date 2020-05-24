package application;

import domain.Statistics;
import domain.StatisticsByMinute;

public class LastMinuteStatistics {
    StatisticsByMinute statisticsByMinute;

    public LastMinuteStatistics(StatisticsByMinute statisticsByMinute) {
        this.statisticsByMinute = statisticsByMinute;
    }

    public Statistics statistics() {
        return statisticsByMinute.statistics();
    }
}
