package com.ditucci.matteo.infrastructure;

import com.fasterxml.jackson.core.JsonParseException;
import domain.Transaction;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@io.micronaut.http.annotation.Controller
public class Controller {
    private Clock clock;

    public Controller(Clock clock) {
        this.clock = clock;
    }

    @Post(value = "/transactions", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<String> logTransaction(@Body Transaction transaction) {
        if(instantFrom(transaction).isEmpty()) {
            return HttpResponse.unprocessableEntity();
        }
        if (amountFrom(transaction).isEmpty()) {
            return HttpResponse.unprocessableEntity();
        }

        Instant timestamp  = instantFrom(transaction).get();
        if (timestamp.isBefore(clock.instant().minus(Duration.ofSeconds(60)))) {
            return HttpResponse.noContent();
        }
        if (timestamp.isAfter(clock.instant())) {
            return HttpResponse.unprocessableEntity();
        }

        return HttpResponse.created("");
    }

    private Optional<Instant> instantFrom(@Body Transaction transaction) {
        if (transaction.getTimestamp() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Instant.parse(transaction.getTimestamp()));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    private Optional<BigDecimal> amountFrom(@Body Transaction transaction) {
        if (transaction.getAmount() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(new BigDecimal(transaction.getAmount()));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    @Error
    public HttpResponse<Void> jsonParsingError(HttpRequest request, JsonParseException exception) {
        return HttpResponse.badRequest();
    }
}
