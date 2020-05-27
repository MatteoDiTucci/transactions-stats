package com.ditucci.matteo.infrastructure.controller;

import application.StoreTransaction;
import com.ditucci.matteo.infrastructure.StoreTransactionController;
import com.ditucci.matteo.infrastructure.Transaction;
import com.ditucci.matteo.infrastructure.validation.TransactionValidator;
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
import static org.mockito.Mockito.*;

class StoreTransactionControllerTest {

    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private StoreTransaction storeTransaction;
    private TransactionValidator validator;
    private StoreTransactionController controller;

    @BeforeEach
    void setup() {
        validator = mock(TransactionValidator.class);
        storeTransaction = mock(StoreTransaction.class);
        controller = new StoreTransactionController(validator, storeTransaction);
    }

    @Test
    public void returnCreatedWhenValidationSucceeds() {
        Transaction transaction = new Transaction("123.456", BASE_INSTANT.toString());
        when(validator.validate(transaction)).thenReturn(HttpStatus.OK);

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    public void returnUnprocessableEntityWhenValidationReturnsUnprocessableEntity() {
        Transaction transaction = new Transaction("", BASE_INSTANT.toString());
        when(validator.validate(transaction)).thenReturn(HttpStatus.NO_CONTENT);

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    public void returnNoContentWhenValidationReturnsNoContent() {
        Transaction transaction = new Transaction("", BASE_INSTANT.toString());
        when(validator.validate(transaction)).thenReturn(HttpStatus.NO_CONTENT);

        HttpResponse<String> response = controller.storeTransaction(transaction);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void callStoreTransactionUseCase() {
        BigDecimal amount = new BigDecimal("123.456");
        Instant timestamp = BASE_INSTANT.minus(Duration.ofSeconds(30));
        Transaction transaction = new Transaction(amount.toString(), timestamp.toString());
        when(validator.validate(transaction)).thenReturn(HttpStatus.OK);

        controller.storeTransaction(transaction);

        verify(storeTransaction).store(amount, timestamp);
    }
}