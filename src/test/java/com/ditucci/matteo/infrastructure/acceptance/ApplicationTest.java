package com.ditucci.matteo.infrastructure.acceptance;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void returnLastMinuteStatistics() {
        String expected = "{\"sum\":883.37,\"avg\":441.69,\"max\":759.91,\"min\":123.46,\"count\":2}";

        client.storeTransaction(BigDecimal.valueOf(123.4567), BASE_INSTANT.minusSeconds(30));
        client.storeTransaction(BigDecimal.valueOf(759.91), BASE_INSTANT.minusSeconds(45));

        HttpResponse<String> response = client.last60SecondsStatistics();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected, response.getBody().get());
    }

    @Test
    public void deleteAllTransactions() {
        String expected = "{\"sum\":0.00,\"avg\":0.00,\"max\":-2147483648.00,\"min\":2147483647.00,\"count\":0}";

        client.storeTransaction(BigDecimal.valueOf(123.4567), BASE_INSTANT.minusSeconds(30));
        client.storeTransaction(BigDecimal.valueOf(759.91), BASE_INSTANT.minusSeconds(45));

        client.deleteTransactions();

        HttpResponse<String> response = client.last60SecondsStatistics();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expected, response.getBody().get());
    }
}
