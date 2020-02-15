package me.igromov.bank.controller;

public class TransferRequest {
    private long from;
    private long to;
    private long amount;

    public TransferRequest() {
    }

    public TransferRequest(long from, long to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
