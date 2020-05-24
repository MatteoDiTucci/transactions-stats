package com.ditucci.matteo.infrastructure.acceptance;

import domain.Statistics;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {

    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private EmbeddedServer server;
    private HttpClient client;

    @BeforeEach
    void setUp() {
        EmbeddedServer embeddedServer = ApplicationContext.build().build()
                .registerSingleton(Clock.class, Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome")))
                .start()
                .getBean(EmbeddedServer.class)
                .start();
        server = embeddedServer;
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

        client.storeTransaction(BigDecimal.valueOf(123.4567), BASE_INSTANT.minusSeconds(30));
        client.storeTransaction(BigDecimal.valueOf(759.91), BASE_INSTANT.minusSeconds(45));

        Statistics statistics = client.last60SecondsStatistics().getBody().get();

        assertEquals(expected, statistics);
    }
}
