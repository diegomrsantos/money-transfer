package com.revolut.moneytransfer.domain.entity;

import java.math.BigDecimal;

public class Transfer {

    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;

    public Transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    private Transfer(Long id, Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public static Transfer of(Long id, Long fromAccountId, Long toAccountId, BigDecimal amount) {
        return new Transfer(id, fromAccountId, toAccountId, amount);
    }

    public Long getId() {
        return id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
