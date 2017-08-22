package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.dao.AccountDao;
import com.revolut.moneytransfer.dao.UserDao;
import com.revolut.moneytransfer.domain.Account;
import com.revolut.moneytransfer.domain.User;
import org.javamoney.moneta.FastMoney;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountDao accountDaoMock;
    private AccountService accountService;
    private Account account1;
    private Account account2;

    @Before
    public void setUp(){

        accountDaoMock = mock(AccountDao.class);
        accountService = new AccountService(accountDaoMock, mock(UserDao.class));

        User user1 = new User(1L, "firstName1", "lastName1");
        account1 = new Account(1L, user1, FastMoney.of(100, "EUR"));

        User user2 = new User(2L, "firstName2", "lastName2");
        account2 = new Account(2L, user2, FastMoney.of(100, "EUR"));

        Map<Long, Account> result = new HashMap<>();
        result.put(1L, account1);
        result.put(2L, account2);
        when(accountDaoMock.findByIdsAndLock(1L, 2L)).thenReturn(result);
    }

    @Test
    public void transferMoneyCommitTest(){

        FastMoney value = FastMoney.of(50, "EUR");
        accountService.transferMoney(1L, 2L, value);

        verify(accountDaoMock, times(1)).update(
                ArgumentMatchers.refEq(new Account(account1.getId(), account1.getUser(), account1.getBalance().subtract(value))));

        verify(accountDaoMock, times(1)).update(
                ArgumentMatchers.refEq(new Account(account2.getId(), account2.getUser(), account2.getBalance().add(value))));

        verify(accountDaoMock, times(1)).commit();

        verify(accountDaoMock, never()).rollback();

    }


    @Test(expected = RuntimeException.class)
    public void transferMoneyRollbackTest(){

        FastMoney value = FastMoney.of(50, "EUR");

        when(accountDaoMock.update(
                ArgumentMatchers.refEq(new Account(account1.getId(), account1.getUser(), account1.getBalance().subtract(value)))))
                .thenThrow(new RuntimeException());

        accountService.transferMoney(1L, 2L, value);

        verify(accountDaoMock, never()).update(
                ArgumentMatchers.refEq(new Account(account2.getId(), account2.getUser(), account2.getBalance().add(value))));

        verify(accountDaoMock, times(1)).rollback();
        verify(accountDaoMock, never()).commit();

    }
}
