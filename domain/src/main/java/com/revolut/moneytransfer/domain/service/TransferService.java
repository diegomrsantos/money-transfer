package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.dao.AccountDao;
import com.revolut.moneytransfer.domain.dao.Dao;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.exception.MoneyTransferException;

import java.math.BigDecimal;

public class TransferService {

    private Dao<Transfer> transferDao;
    private AccountDao accountDao;
    private TransactionHandler transactionHandler;

    public TransferService(Dao<Transfer> transferDao, AccountDao accountDao, TransactionHandler transactionHandler) {
        this.transferDao = transferDao;
        this.accountDao = accountDao;
        this.transactionHandler = transactionHandler;
    }

    public Transfer transferMoney(Transfer transfer) {

        final Long fromAccountId = transfer.getFromAccountId();
        final Long toAccountId = transfer.getToAccountId();
        final BigDecimal amount = transfer.getAmount();

        if(fromAccountId == toAccountId){
            throw new MoneyTransferException("Accounts cannot be the same");
        }

         return transactionHandler.runInTransation( () -> {
            boolean balanceSuccefullyDecreased = false;
            while (!balanceSuccefullyDecreased) {
                Account fromAccount = accountDao.findById(fromAccountId)
                        .orElseThrow(() -> new MoneyTransferException(String.format("Account %s could not be found", fromAccountId)));

                Account toAccount = accountDao.findById(toAccountId)
                        .orElseThrow(() -> new MoneyTransferException(String.format("Account %s could not be found", toAccountId)));

                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    throw new MoneyTransferException(
                            String.format("Insufficient funds in account %s", fromAccountId));
                }
                balanceSuccefullyDecreased = accountDao.decreaseBalance(fromAccountId, fromAccount.getBalance(), amount);
            }

            accountDao.increaseBalance(toAccountId, amount);
            return transferDao.create(new Transfer(fromAccountId, toAccountId, amount));
         });
    }
}
