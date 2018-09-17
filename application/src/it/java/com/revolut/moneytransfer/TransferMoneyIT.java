package com.revolut.moneytransfer;

import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.service.AccountService;
import com.revolut.moneytransfer.domain.service.TransferService;
import com.revolut.moneytransfer.infrastructure.dao.AccountDaoImpl;
import com.revolut.moneytransfer.infrastructure.dao.TransferDaoImpl;
import com.revolut.moneytransfer.infrastructure.transaction.TransactionHandlerImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TransferMoneyIT {

    private TransferService transferService;
    private AccountService accountService;

    @Before
    public void setUp() {
        final TransactionHandlerImpl transactionHandler = new TransactionHandlerImpl();
        final TransferDaoImpl transferDao = new TransferDaoImpl(transactionHandler);

        final AccountDaoImpl accountDao = new AccountDaoImpl(transactionHandler);

        accountService = new AccountService(transactionHandler, accountDao);

        transferService = new TransferService(transferDao, accountDao, transactionHandler);

        final Account account1 = accountService.create(1L);
        final Account account2 = accountService.create(2L);

        accountService.deposit(account1.getId(), new BigDecimal("1000.00"));
        accountService.deposit(account2.getId(), new BigDecimal("1000.00"));
    }

    @Test
    public void transferMoneyThreadsTest() throws Exception {

        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService1.execute(() ->{
                transferService.transferMoney(new Transfer(1L, 2L, BigDecimal.ONE));
            });

        }
        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService2.execute(() ->{
                transferService.transferMoney(new Transfer(2L, 1L, BigDecimal.ONE));
            });
        }

        executorService1.shutdown();
        executorService1.awaitTermination(10, TimeUnit.SECONDS);

        executorService2.shutdown();
        executorService2.awaitTermination(10, TimeUnit.SECONDS);


        BigDecimal account1Balance = accountService.findById(1L).get().getBalance();
        BigDecimal account2Balance = accountService.findById(2L).get().getBalance();

        assertEquals(new BigDecimal("1000.00"), account1Balance);
        assertEquals(new BigDecimal("1000.00"), account2Balance);
    }
}
