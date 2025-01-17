package com.ditucci.matteo.infrastructure.controllers;

import application.LastMinuteStatistics;
import domain.Statistics;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller
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
