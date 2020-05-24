package com.ditucci.matteo.infrastructure.acceptance;

import domain.Statistics;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpResponse;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

    private static final long MILLISECONDS_FOR_2020_05_20_12_00_00 = 1590256586172L;
    private EmbeddedServer server;
    private HttpClient client;

    @BeforeEach
    void setUp() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().getBean(HttpClient.class);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    @Disabled
    public void returnLast60SecondsStatistics() {
        Statistics expected = new Statistics(BigDecimal.valueOf(883.37), BigDecimal.valueOf(441.69),
                BigDecimal.valueOf(759.91), BigDecimal.valueOf(123.46), 2);

        client.storeTransaction(BigDecimal.valueOf(123.4567), Instant.ofEpochMilli(MILLISECONDS_FOR_2020_05_20_12_00_00));
        client.storeTransaction(BigDecimal.valueOf(759.91), Instant.ofEpochMilli(MILLISECONDS_FOR_2020_05_20_12_00_00));

        HttpResponse<Statistics> response = client.last60SecondsStatistics();

        assertEquals(expected, response.getBody().get());
    }

    @Test
    void returnBadRequestForMalformedTransactionJson() {

    }
}
