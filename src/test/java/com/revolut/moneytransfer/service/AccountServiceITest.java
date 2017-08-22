package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.UserDao;
import com.revolut.moneytransfer.domain.Account;
import com.revolut.moneytransfer.domain.User;
import org.javamoney.moneta.FastMoney;
import org.junit.Before;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class AccountServiceITest {

    private AccountDao accountDao;
    private UserDao userDao;
    private AccountService accountService;

    @Before
    public void setUp(){

        accountDao = new AccountDao();
        userDao = new UserDao();
        accountService = new AccountService(accountDao, userDao);

        User user1 = new User(1L, "firstName1", "lastName1");
        Account account1 = new Account(1L, user1, FastMoney.of(1000, "EUR"));
        accountDao.beginTransaction();
        accountDao.create(account1);
        accountDao.commit();


        User user2 = new User(2L, "firstName2", "lastName2");
        Account account2 = new Account(2L, user2, FastMoney.of(1000, "EUR"));
        accountDao.beginTransaction();
        accountDao.create(account2);
        accountDao.commit();
    }

    @Test
    public void transferMoneyTest(){


        accountService.transferMoney(1L, 2L, FastMoney.of(50, "EUR"));

        accountDao.beginTransaction();
        MonetaryAmount account1Balance = accountDao.findById(1L).getBalance();
        MonetaryAmount account2Balance = accountDao.findById(2L).getBalance();
        accountDao.commit();


        assertEquals(FastMoney.of(950, "EUR"), account1Balance);
        assertEquals(FastMoney.of(1050, "EUR"), account2Balance);
    }

    @Test
    public void transferMoneyThreadsTest() throws Exception {

        ExecutorService executorService1 = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1000; i++) {
            executorService1.execute(() ->{
                accountService.transferMoney(1L, 2L, FastMoney.of(1, "EUR"));
            });

        }
        ExecutorService executorService2 = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1000; i++) {
            executorService2.execute(() ->{
                accountService.transferMoney(2L, 1L, FastMoney.of(1, "EUR"));
            });

        }

        executorService1.shutdown();
        executorService1.awaitTermination(10, TimeUnit.SECONDS);

        executorService2.shutdown();
        executorService2.awaitTermination(10, TimeUnit.SECONDS);


        accountDao.beginTransaction();
        MonetaryAmount account1Balance = accountDao.findById(1L).getBalance();
        MonetaryAmount account2Balance = accountDao.findById(2L).getBalance();
        accountDao.commit();


        assertEquals(FastMoney.of(1000, "EUR"), account1Balance);
        assertEquals(FastMoney.of(1000, "EUR"), account2Balance);
    }
}
