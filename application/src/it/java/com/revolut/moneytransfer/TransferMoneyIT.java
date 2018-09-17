package com.revolut.moneytransfer;

import com.revolut.moneytransfer.domain.dao.AccountDao;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.service.AccountService;
import com.revolut.moneytransfer.domain.service.TransferService;
import com.revolut.moneytransfer.infrastructure.dao.AccountDaoImpl;
import com.revolut.moneytransfer.infrastructure.dao.TransferDaoImpl;
import com.revolut.moneytransfer.infrastructure.transaction.TransactionHandlerImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TransferMoneyIT {

    private static final BigDecimal TEN = new BigDecimal("10.00");
    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000.00");

    private final TransactionHandlerImpl transactionHandler = new TransactionHandlerImpl();
    private final TransferDaoImpl transferDao = new TransferDaoImpl(transactionHandler);

    private TransferService transferService;
    private AccountService accountService;

    private Account account1;
    private Account account2;

    @Before
    public void setUp() {
        final AccountDaoImpl accountDao = new AccountDaoImpl(transactionHandler);

        accountService = new AccountService(transactionHandler, accountDao);
        transferService = new TransferService(transferDao, accountDao, transactionHandler);

        account1 = accountService.create(1L);
        account2 = accountService.create(2L);

        accountService.deposit(account1.getId(), ONE_THOUSAND);
        accountService.deposit(account2.getId(), ONE_THOUSAND);
    }

    @Test
    public void transferMoneyTest() {

        final Transfer transfer =  transferService.transferMoney(new Transfer(account1.getId(), account2.getId(), TEN));

        BigDecimal account1Balance = accountService.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountService.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND.subtract(TEN), account1Balance);
        assertEquals(ONE_THOUSAND.add(TEN), account2Balance);

        assertEquals(account1.getId(), transfer.getFromAccountId());
        assertEquals(account2.getId(), transfer.getToAccountId());
        assertEquals(TEN, transfer.getAmount());
    }

    @Test
    public void transferMoneyRollbackTest() {

        final AccountDao accountDaoSpy = Mockito.spy(new AccountDaoImpl(transactionHandler));
        Mockito.doThrow(new RuntimeException()).when(accountDaoSpy).increaseBalance(account2.getId(), TEN);

        final TransferDaoImpl transferDao = new TransferDaoImpl(transactionHandler);

        accountService = new AccountService(transactionHandler, accountDaoSpy);
        transferService = new TransferService(transferDao, accountDaoSpy, transactionHandler);

        try {
            transferService.transferMoney(new Transfer(account1.getId(), account2.getId(), TEN));
        } catch (Exception e) {
            // catching exception which caused rollback
        }

        BigDecimal account1Balance = accountService.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountService.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND, account1Balance);
        assertEquals(ONE_THOUSAND, account2Balance);
    }

    @Test
    public void transferMoneyConcurrencyTest() throws Exception {

        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService1.execute(() ->{
                transferService.transferMoney(new Transfer(account1.getId(), account2.getId(), BigDecimal.ONE));
            });

        }
        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService2.execute(() ->{
                transferService.transferMoney(new Transfer(account2.getId(), account1.getId(), BigDecimal.ONE));
            });
        }

        executorService1.shutdown();
        executorService1.awaitTermination(10, TimeUnit.SECONDS);

        executorService2.shutdown();
        executorService2.awaitTermination(10, TimeUnit.SECONDS);


        BigDecimal account1Balance = accountService.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountService.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND, account1Balance);
        assertEquals(ONE_THOUSAND, account2Balance);
    }
}
