package me.igromov.exchanger.controller.pojo;

public class AccountCreateRequest {
    private Long id;
    private Long balance = 0L;

    public AccountCreateRequest() {
    }

    public AccountCreateRequest(Long id, Long balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
