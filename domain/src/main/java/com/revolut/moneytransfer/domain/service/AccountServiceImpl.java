package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountDao;
    private TransactionHandler transactionHandler;

    public AccountServiceImpl(TransactionHandler transactionHandler, AccountRepository accountDao) {
        this.transactionHandler = transactionHandler;
        this.accountDao = accountDao;
    }

    public Account create(Long userId) {
        return transactionHandler.runInTransation(() -> accountDao.create(new Account(userId)));
    }

    public Optional<Account> findById(Long id) {
        return transactionHandler.runInTransation(() -> accountDao.findById(id));
    }

    public boolean delete(Long id) {
        return transactionHandler.runInTransation(() -> accountDao.delete(id));
    }

    public void deposit(Long id, BigDecimal value){
        transactionHandler.runInTransation(() -> accountDao.increaseBalance(id, value));
    }
}
