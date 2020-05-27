package com.ditucci.matteo.infrastructure.validation;

import com.ditucci.matteo.infrastructure.Transaction;
import io.micronaut.http.HttpStatus;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@Singleton
public class TransactionValidator {
    private final Clock clock;

    public TransactionValidator(Clock clock) {
        this.clock = clock;
    }

    public HttpStatus validate(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getTimestamp() == null) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        try {
            new BigDecimal(transaction.getAmount());
            Instant timestamp = Instant.parse(transaction.getTimestamp());

            if (timestamp.isBefore(clock.instant().minus(Duration.ofSeconds(59)))) {
                return HttpStatus.NO_CONTENT;
            }
            if (timestamp.isAfter(clock.instant())) {
                return HttpStatus.UNPROCESSABLE_ENTITY;
            }

            return HttpStatus.OK;

        } catch (NumberFormatException | DateTimeParseException exception) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
    }
}
