package com.ditucci.matteo.infrastructure;

public class Transaction {
    private String amount;
    private String timestamp;

    public Transaction(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction() {
        // needed by Jackson to deserialise requests
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAmount() {
        return amount;
    }
}
