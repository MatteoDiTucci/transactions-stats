package com.ditucci.matteo.infrastructure.validation;

import com.ditucci.matteo.infrastructure.Transaction;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {
    private final Instant BASE_INSTANT = Instant.parse("2020-05-23T18:00:00.000Z");
    private Clock clock;
    private TransactionValidator validator;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(BASE_INSTANT, ZoneId.of("Europe/Rome"));
        validator = new TransactionValidator(clock);
    }

    @Test
    void returnUnprocessableEntityForNullAmount() {
        HttpStatus result = validator.validate(new Transaction(null, BASE_INSTANT.toString()));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result);
    }

    @Test
    void returnUnprocessableEntityForInvalidAmount() {
        HttpStatus result = validator.validate(new Transaction("", BASE_INSTANT.toString()));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result);
    }

    @Test
    void returnUnprocessableEntityForNullTimestamp() {
        HttpStatus result = validator.validate(new Transaction("1.23", null));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result);
    }

    @Test
    void returnUnprocessableEntityForInvalidTimestamp() {
        HttpStatus result = validator.validate(new Transaction("1.23", "2018/07/17T 09-59-51-312-"));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result);
    }

    @Test
    void returnUnprocessableEntityForTimestampInFuture() {
        HttpStatus result = validator.validate(new Transaction("1.23", BASE_INSTANT.plusSeconds(1).toString()));

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result);
    }

    @Test
    void returnNoContentForTimestampOlderThanOneMinute() {
        HttpStatus result = validator.validate(new Transaction("1.23", BASE_INSTANT.minusSeconds(60).toString()));

        assertEquals(HttpStatus.NO_CONTENT, result);
    }

    @Test
    void returnOkForValidTransaction() {
        HttpStatus result = validator.validate(new Transaction("1.23", BASE_INSTANT.toString()));

        assertEquals(HttpStatus.OK, result);
    }
}