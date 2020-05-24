package com.ditucci.matteo.infrastructure.acceptance;

import domain.Statistics;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.Instant;

@Singleton
public class HttpClient {
    private final RxHttpClient client;

    public HttpClient(@Client("/") RxHttpClient rxHttpClient) {
        this.client = rxHttpClient;
    }

    public HttpResponse<Void> storeTransaction(BigDecimal amount, Instant timesStamp) {
        HttpRequest<StoreTransactionBody> request =
                HttpRequest.POST("/transactions", new StoreTransactionBody(amount, timesStamp))
                        .contentType(MediaType.APPLICATION_JSON);

        return client.toBlocking().exchange(request);
    }

    public HttpResponse<Statistics> last60SecondsStatistics() {
        HttpRequest<Void> request = HttpRequest.GET("/statistics");

        return client.toBlocking().exchange(request);
    }


    private static class StoreTransactionBody {
        public BigDecimal amount;
        public Instant timestamp;

        public StoreTransactionBody(BigDecimal amount, Instant timestamp) {
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }
}