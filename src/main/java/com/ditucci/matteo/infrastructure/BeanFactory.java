package com.ditucci.matteo.infrastructure;

import domain.Statistics;
import domain.StatisticsByMinute;
import domain.StatisticsBySecond;
import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;
import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Factory
public class BeanFactory {

    @Singleton
    public StatisticsByMinute createStatisticsByMinute(Clock clock) {
        ConcurrentHashMap<Integer, StatisticsBySecond> statisticsBySecond = new ConcurrentHashMap<>();

        IntStream.range(0, 59).boxed()
                .forEach(second -> statisticsBySecond.put(second, new StatisticsBySecond(clock, Statistics.EMPTY_STATISTICS)));

        return new StatisticsByMinute(statisticsBySecond, clock);
    }

    @Singleton
    public Clock createClock() {
        return Clock.systemUTC();
    }
}
