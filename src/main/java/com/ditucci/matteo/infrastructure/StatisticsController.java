package com.ditucci.matteo.infrastructure;

import application.LastMinuteStatistics;
import domain.Statistics;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;

public class StatisticsController {
    private final LastMinuteStatistics lastMinuteStatistics;

    public StatisticsController(LastMinuteStatistics lastMinuteStatistics) {
        this.lastMinuteStatistics = lastMinuteStatistics;
    }

    @Get(value = "/statistics", produces = MediaType.APPLICATION_JSON)
    public HttpResponse<Statistics> statistics() {
        return HttpResponse.ok(lastMinuteStatistics.statistics());
    }
}
