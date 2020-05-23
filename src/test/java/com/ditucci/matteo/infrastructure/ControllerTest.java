package com.ditucci.matteo.infrastructure;

import domain.Transaction;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerTest {

    private Clock clock;
    private Controller controller;

    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");

    @BeforeEach
    void setup() {
        clock = Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome"));
        controller = new Controller(clock);
    }

    @Test
    public void replyCreatedWhenRequestIsValid() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void returnNoContentWhenTransactionIsOlderThan60Seconds() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.minus(Duration.ofSeconds(90)).toString());

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionIsInTheFuture() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.plus(Duration.ofSeconds(90)).toString());

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionTimestampIsNull() {
        Transaction transaction = new Transaction("123.456", null);

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionTimestampIsNotInIso8601Format() {
        Transaction transaction = new Transaction("123.456", "2020/05/2318:00:00.000Z");

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionAmountIsNull() {
        Transaction transaction = new Transaction(null, BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionAmountIsMalformed() {
        Transaction transaction = new Transaction("abc", BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.logTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }
}