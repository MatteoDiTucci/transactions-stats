package com.ditucci.matteo.infrastructure;

import application.StoreTransaction;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StoreTransactionControllerTest {

    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private Clock clock;
    private StoreTransaction storeTransaction;
    private StoreTransactionController controller;

    @BeforeEach
    void setup() {
        clock = Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome"));
        storeTransaction = mock(StoreTransaction.class);
        controller = new StoreTransactionController(clock, storeTransaction);
    }

    @Test
    public void replyCreatedWhenRequestIsValid() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void returnNoContentWhenTransactionIsOlderThan60Seconds() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.minus(Duration.ofSeconds(90)).toString());

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionIsInTheFuture() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.plus(Duration.ofSeconds(90)).toString());

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionTimestampIsNull() {
        Transaction transaction = new Transaction("123.456", null);

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionTimestampIsNotInIso8601Format() {
        Transaction transaction = new Transaction("123.456", "2020/05/2318:00:00.000Z");

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionAmountIsNull() {
        Transaction transaction = new Transaction(null, BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void returnUnprocessableEntityWhenTransactionAmountIsMalformed() {
        Transaction transaction = new Transaction("abc", BASE_INSTANT.minus(Duration.ofSeconds(30)).toString());

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatus());
    }

    @Test
    void callStoreTransactionUseCase() {
        BigDecimal amount = new BigDecimal("123.456");
        Instant timestamp = BASE_INSTANT.minus(Duration.ofSeconds(30));
        Transaction transaction = new Transaction(amount.toString(), timestamp.toString());

        controller.storeTransaction(transaction);

        verify(storeTransaction).store(amount, timestamp);
    }
}