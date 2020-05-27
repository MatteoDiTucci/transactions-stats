package com.ditucci.matteo.infrastructure;

import application.StoreTransaction;
import com.ditucci.matteo.infrastructure.validation.TransactionValidator;
import com.fasterxml.jackson.core.JsonParseException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Post;

import java.math.BigDecimal;
import java.time.Instant;

@io.micronaut.http.annotation.Controller
public class StoreTransactionController {
    private final TransactionValidator validator;
    private final StoreTransaction storeTransaction;

    public StoreTransactionController(TransactionValidator validator, StoreTransaction storeTransaction) {
        this.validator = validator;
        this.storeTransaction = storeTransaction;
    }

    @Post(value = "/transactions", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<String> storeTransaction(@Body Transaction transaction) {
        HttpStatus httpStatusFromValidation = validator.validate(transaction);

        if (HttpStatus.OK.equals(httpStatusFromValidation)) {
            storeTransaction.store(amountFrom(transaction), instantFrom(transaction));
            return HttpResponse.created("");
        }
        return HttpResponse.status(httpStatusFromValidation);
    }

    private BigDecimal amountFrom(@Body Transaction transaction) {
        return new BigDecimal(transaction.getAmount());
    }

    private Instant instantFrom(@Body Transaction transaction) {
        return Instant.parse(transaction.getTimestamp());
    }

    @Error
    public HttpResponse<Void> jsonParsingError(HttpRequest request, JsonParseException exception) {
        return HttpResponse.badRequest();
    }
}
