package application;

import domain.StatisticsByMinute;

import javax.inject.Singleton;

@Singleton
public class DeleteTransactions {
    StatisticsByMinute statisticsByMinute;

    public void delete() {
        statisticsByMinute.delete();
    }
}
