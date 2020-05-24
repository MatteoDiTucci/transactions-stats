package domain;

import io.micronaut.context.annotation.Factory;

import javax.inject.Singleton;
import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Factory
public class BeanFactory {

    @Singleton
    public StatisticsByMinute createStatisticsByMinute(Clock clock) {
        Instant instant = clock.instant();
        ConcurrentHashMap<Integer, Statistics> statisticsBySecond = new ConcurrentHashMap<>();

        IntStream.range(0, 59).boxed()
                .forEach(second -> statisticsBySecond.put(second, Statistics.EMPTY_STATISTICS));

        return new StatisticsByMinute(statisticsBySecond, clock);
    }

    @Singleton
    public Clock createClock() {
        return Clock.systemUTC();
    }
}
