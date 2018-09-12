package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.Account;
import com.revolut.moneytransfer.domain.User;
import com.revolut.moneytransfer.domain.dao.AccountDao;
import com.revolut.moneytransfer.domain.dao.Dao;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.User;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;

import java.util.Collections;
import java.util.List;

public class AccountService {

    private AccountDao accountDao;
    private Dao<User> userDao;

    public AccountService(AccountDao accountDao, Dao<User> userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    public Account create(String userId) {

        Account account = null;
        try {

            userDao.beginTransaction();
            User user = userDao.findById(Long.valueOf(userId));
            userDao.commit();
            accountDao.beginTransaction();
            account = accountDao.create(new Account(user, Money.of(0, "EUR")));
            accountDao.commit();
        } catch (Exception e) {
            userDao.rollback();
            accountDao.rollback();
        }
        return account;
    }

    public Account findById(Long id) {
        Account account = null;
        try {
            accountDao.beginTransaction();
            account = accountDao.findById(id);
            accountDao.commit();
        } catch (Exception e) {
            accountDao.rollback();
            throw e;
        }
        return account;
    }

    public void delete(Long id) {
        try {
            accountDao.beginTransaction();
            accountDao.delete(id);
            accountDao.commit();
        } catch (Exception e) {
            accountDao.rollback();
        }
    }

    public Account deposit(Long id, MonetaryAmount value){

        try {
            accountDao.beginTransaction();
            Account existingAccount = accountDao.findByIdsAndLock(id).get(id);
            Account account = accountDao
                .update(new Account(id, existingAccount.getUser(), existingAccount.getBalance().add(value)));
            accountDao.commit();
            return account;

        } catch (Exception e) {
            userDao.rollback();
            throw e;
        }
    }

    public List<Account> getAll() {
        List<Account> accountList = Collections.EMPTY_LIST;
        try {
            accountDao.beginTransaction();
            accountList = accountDao.getAll();
            accountDao.commit();
        } catch (Exception e) {
            accountDao.rollback();
            throw e;
        }
        return accountList;
    }


    public void transferMoney(Long fromAccountId, Long toAccountId, MonetaryAmount value){

        if(fromAccountId == toAccountId){
            throw new RuntimeException("Accounts cannot be the same");
        }

        try {
            boolean flag = false;

            accountDao.beginTransaction();
            while (flag != true) {
              Account fromAccount = accountDao.findById(fromAccountId);
              if (fromAccount.getBalance().isLessThan(value)) {
                throw new RuntimeException(
                    String.format("Insufficient funds in account %s", fromAccountId));
              }
              flag = accountDao.decreaseBalance(fromAccountId, fromAccount.getBalance(), value);
            }

            Account toAccount = accountDao.findById(toAccountId);
            accountDao.increaseBalance(toAccountId, value);

            accountDao.commit();

        } catch (RuntimeException e) {
            accountDao.rollback();
            throw e;
        }
    }
}
