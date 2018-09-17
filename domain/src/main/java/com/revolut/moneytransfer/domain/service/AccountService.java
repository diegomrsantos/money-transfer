package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.dao.AccountDao;
import com.revolut.moneytransfer.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AccountService {

    private AccountDao accountDao;
    private TransactionHandler transactionHandler;

    public AccountService(TransactionHandler transactionHandler, AccountDao accountDao) {
        this.transactionHandler = transactionHandler;
        this.accountDao = accountDao;
    }

    public Account create(Long userId) {
        return transactionHandler.runInTransation(() -> accountDao.create(new Account(userId)));
    }

    public Optional<Account> findById(Long id) {
        return transactionHandler.runInTransation(() -> accountDao.findById(id));
    }

    public void delete(Long id) {
        transactionHandler.runInTransation(() -> accountDao.delete(id));
    }

    public void deposit(Long id, BigDecimal value){
        transactionHandler.runInTransation(() -> accountDao.increaseBalance(id, value));
    }

    public List<Account> getAll() {
        return transactionHandler.runInTransation(() -> accountDao.getAll());
    }


}
