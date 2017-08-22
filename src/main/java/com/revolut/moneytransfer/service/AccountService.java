package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.interfaces.TransactionalDAO;
import com.revolut.moneytransfer.domain.Account;
import com.revolut.moneytransfer.domain.User;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AccountService {

    private TransactionalDAO<Account> accountDAO;
    private TransactionalDAO<User> userDAO;

    public AccountService(TransactionalDAO<Account> accountDAO, TransactionalDAO<User> userDAO) {
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
    }

    public Account create(String userId) {

        Account account = null;
        try {

            userDAO.beginTransaction();
            User user = userDAO.findById(Long.valueOf(userId));
            userDAO.commit();
            accountDAO.beginTransaction();
            account = accountDAO.create(new Account(user, Money.of(0, "EUR")));
            accountDAO.commit();
        } catch (Exception e) {
            userDAO.rollback();
            accountDAO.rollback();
        }
        return account;
    }

    public Account findById(Long id) {
        Account account = null;
        try {
            accountDAO.beginTransaction();
            account = accountDAO.findById(id);
            accountDAO.commit();
        } catch (Exception e) {
            accountDAO.rollback();
            throw e;
        }
        return account;
    }

    public void delete(Long id) {
        try {
            accountDAO.beginTransaction();
            accountDAO.delete(id);
            accountDAO.commit();
        } catch (Exception e) {
            accountDAO.rollback();
        }
    }

    public Account deposit(Long id, MonetaryAmount value){

        try {
            accountDAO.beginTransaction();
            Account existingAccount = accountDAO.findByIdsAndLock(id).get(id);
            Account account = accountDAO.update(new Account(id, existingAccount.getUser(), existingAccount.getBalance().add(value)));
            accountDAO.commit();
            return account;

        } catch (Exception e) {
            userDAO.rollback();
            throw e;
        }
    }

    public List<Account> getAll() {
        List<Account> accountList = Collections.EMPTY_LIST;
        try {
            accountDAO.beginTransaction();
            accountList = accountDAO.getAll();
            accountDAO.commit();
        } catch (Exception e) {
            accountDAO.rollback();
            throw e;
        }
        return accountList;
    }

    public void transferMoney(Long fromAccountId, Long toAccountId, MonetaryAmount value){

        if(fromAccountId == toAccountId){
            throw new RuntimeException("Accounts cannot be the same");
        }

        accountDAO.beginTransaction();

        try {
            Map<Long, Account> accountMap = accountDAO.findByIdsAndLock(fromAccountId, toAccountId);
            Account fromAccount = accountMap.get(fromAccountId);
            Account toAccount = accountMap.get(toAccountId);

            if(fromAccount.getBalance().isLessThan(value)){
                throw new RuntimeException(String.format("Insufficient funds in account %s", fromAccountId));
            }
            MonetaryAmount fromAccountNewBalance = fromAccount.getBalance().subtract(value);
            MonetaryAmount toAccountNewBalance = toAccount.getBalance().add(value);

            accountDAO.update(new Account(fromAccountId, fromAccount.getUser(), fromAccountNewBalance));
            accountDAO.update(new Account(toAccountId, toAccount.getUser(), toAccountNewBalance));
            accountDAO.commit();

        } catch (RuntimeException e) {
            accountDAO.rollback();
            throw e;
        }
    }
}
