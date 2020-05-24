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

    public HttpResponse<Void> logTransaction(BigDecimal amount, Instant timesStamp) {
        HttpRequest<LogTransactionBody> request =
                HttpRequest.POST("/transactions", new LogTransactionBody(amount, timesStamp))
                        .contentType(MediaType.APPLICATION_JSON);

        return client.toBlocking().exchange(request);
    }

    public HttpResponse<Statistics> last60SecondsStatistics() {
        HttpRequest<Void> request = HttpRequest.GET("/statistics");

        return client.toBlocking().exchange(request);
    }


    private static class LogTransactionBody {
        public BigDecimal amount;
        public Instant timestamp;

        public LogTransactionBody(BigDecimal amount, Instant timestamp) {
            this.amount = amount;
            this.timestamp = timestamp;
        }
    }
}