package com.ditucci.matteo.infrastructure.controller;

import application.LastMinuteStatistics;
import com.ditucci.matteo.infrastructure.controllers.StatisticsController;
import domain.Statistics;
import io.micronaut.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LastMinuteStatisticsControllerTest {
    LastMinuteStatistics lastMinuteStatistics;
    StatisticsController controller;

    @BeforeEach
    void setUp() {
        lastMinuteStatistics = mock(LastMinuteStatistics.class);
        controller = new StatisticsController(lastMinuteStatistics);
    }

    @Test
    void returnTransactionsStatistics() {
        Statistics statistics = new Statistics(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(1), 1);
        when(lastMinuteStatistics.statistics()).thenReturn(statistics);

        HttpResponse<Statistics> response = controller.statistics();

        assertEquals(statistics, response.getBody().get());
    }
}