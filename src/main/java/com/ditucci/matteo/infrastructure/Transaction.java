package com.ditucci.matteo.infrastructure;

public class Transaction {
    private final String amount;
    private final String timestamp;

    public Transaction(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAmount() {
        return amount;
    }
}
