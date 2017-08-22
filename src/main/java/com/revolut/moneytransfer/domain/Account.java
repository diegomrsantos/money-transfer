package com.revolut.moneytransfer.domain;

import com.fasterxml.jackson.annotation.JsonGetter;

import javax.money.MonetaryAmount;

/**
 * Created by SG0226594 on 8/16/2017.
 */
public class Account implements Entity, Cloneable {

    private Long accountId;
    private User user;
    private MonetaryAmount balance;

    public Account(Long accountId, User user, MonetaryAmount balance) {
        this.accountId = accountId;
        this.user = user;
        this.balance = balance;
    }

    public Account(User user, MonetaryAmount balance) {
        this.user = user;
        this.balance = balance;
    }

    @Override
    public Long getId() {
        return accountId;
    }

    public User getUser() {
        return user;
    }

    public MonetaryAmount getBalance() {
        return balance;
    }

    @JsonGetter("balance")
    public String getBalanceFormatted() {
        return balance.toString();
    }



    @Override
     public Account clone() throws CloneNotSupportedException {
        return (Account) super.clone();
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", user=" + user +
                ", balance=" + balance.toString() +
                '}';
    }
}
