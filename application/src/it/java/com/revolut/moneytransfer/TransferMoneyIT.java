package com.revolut.moneytransfer;

import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.repository.AccountRepository;
import com.revolut.moneytransfer.domain.repository.Repository;
import com.revolut.moneytransfer.domain.service.AccountServiceImpl;
import com.revolut.moneytransfer.domain.service.TransferServiceImpl;
import com.revolut.moneytransfer.infrastructure.repository.AccountRepositoryImpl;
import com.revolut.moneytransfer.infrastructure.repository.TransferRepositoryImpl;
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
    private final Repository<Transfer> transferRepository = new TransferRepositoryImpl(transactionHandler);

    private TransferServiceImpl transferServiceImpl;
    private AccountServiceImpl accountServiceImpl;

    private Account account1;
    private Account account2;

    @Before
    public void setUp() {
        final AccountRepository accountRepository = new AccountRepositoryImpl(transactionHandler);

        accountServiceImpl = new AccountServiceImpl(transactionHandler, accountRepository);
        transferServiceImpl = new TransferServiceImpl(transferRepository, accountRepository, transactionHandler);

        account1 = accountServiceImpl.create(1L);
        account2 = accountServiceImpl.create(2L);

        accountServiceImpl.deposit(account1.getId(), ONE_THOUSAND);
        accountServiceImpl.deposit(account2.getId(), ONE_THOUSAND);
    }

    @Test
    public void transferMoneyTest() {

        final Transfer transfer =  transferServiceImpl
            .transferMoney(new Transfer(account1.getId(), account2.getId(), TEN));

        BigDecimal account1Balance = accountServiceImpl.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountServiceImpl.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND.subtract(TEN), account1Balance);
        assertEquals(ONE_THOUSAND.add(TEN), account2Balance);

        assertEquals(account1.getId(), transfer.getFromAccountId());
        assertEquals(account2.getId(), transfer.getToAccountId());
        assertEquals(TEN, transfer.getAmount());
    }

    @Test
    public void transferMoneyRollbackTest() {

        final AccountRepository accountDaoSpy = Mockito.spy(new AccountRepositoryImpl(transactionHandler));
        Mockito.doThrow(new RuntimeException()).when(accountDaoSpy).increaseBalance(account2.getId(), TEN);

        final Repository<Transfer> transferDao = new TransferRepositoryImpl(transactionHandler);

        accountServiceImpl = new AccountServiceImpl(transactionHandler, accountDaoSpy);
        transferServiceImpl = new TransferServiceImpl(transferDao, accountDaoSpy, transactionHandler);

        try {
            transferServiceImpl.transferMoney(new Transfer(account1.getId(), account2.getId(), TEN));
        } catch (Exception e) {
            // catching exception which caused rollback
        }

        BigDecimal account1Balance = accountServiceImpl.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountServiceImpl.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND, account1Balance);
        assertEquals(ONE_THOUSAND, account2Balance);
    }

    @Test
    public void transferMoneyConcurrencyTest() throws Exception {

        ExecutorService executorService1 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService1.execute(() ->{
                transferServiceImpl
                    .transferMoney(new Transfer(account1.getId(), account2.getId(), BigDecimal.ONE));
            });

        }
        ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 1000; i++) {
            executorService2.execute(() ->{
                transferServiceImpl
                    .transferMoney(new Transfer(account2.getId(), account1.getId(), BigDecimal.ONE));
            });
        }

        executorService1.shutdown();
        executorService1.awaitTermination(10, TimeUnit.SECONDS);

        executorService2.shutdown();
        executorService2.awaitTermination(10, TimeUnit.SECONDS);


        BigDecimal account1Balance = accountServiceImpl.findById(account1.getId()).get().getBalance();
        BigDecimal account2Balance = accountServiceImpl.findById(account2.getId()).get().getBalance();

        assertEquals(ONE_THOUSAND, account1Balance);
        assertEquals(ONE_THOUSAND, account2Balance);
    }
}
