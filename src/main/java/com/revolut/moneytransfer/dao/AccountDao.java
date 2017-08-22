package com.revolut.moneytransfer.dao;


import com.revolut.moneytransfer.domain.Account;

import java.util.concurrent.atomic.AtomicLong;

public class AccountDao extends AbstractDao<Account> {

    private final AtomicLong idGenerator;

    public AccountDao() {
        super();
        idGenerator = new AtomicLong(1);
    }

    public Account create(Account account) {

        return super.create(new Account(idGenerator.getAndIncrement(), account.getUser(), account.getBalance()));
    }
}
