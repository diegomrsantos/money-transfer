package com.revolut.moneytransfer.domain.entity;

import java.math.BigDecimal;

public class Account {

    private Long id;
    private Long userId;
    private BigDecimal balance;

    public Account(Long userid) {
        this.userId = userid;
        this.balance = BigDecimal.ZERO;
    }

    private Account(Long id, Long userid, BigDecimal balance) {
        this.id = id;
        this.userId = userid;
        this.balance = balance;
    }

    public static Account of(Long accountId, Long userid, BigDecimal balance){
        return new Account(accountId, userid, balance);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance.toString() +
                '}';
    }
}
