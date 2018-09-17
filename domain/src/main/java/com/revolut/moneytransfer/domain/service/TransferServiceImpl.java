package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.exception.MoneyTransferException;
import com.revolut.moneytransfer.domain.repository.AccountRepository;
import com.revolut.moneytransfer.domain.repository.Repository;

import java.math.BigDecimal;
import java.util.Optional;

public class TransferServiceImpl implements TransferService{

    private Repository<Transfer> transferRepository;
    private AccountRepository accountDao;
    private TransactionHandler transactionHandler;

    public TransferServiceImpl(Repository<Transfer> transferRepository, AccountRepository accountRepository,
                               TransactionHandler transactionHandler) {
        this.transferRepository = transferRepository;
        this.accountDao = accountRepository;
        this.transactionHandler = transactionHandler;
    }

    public Optional<Transfer> findById(Long id) {
        return transactionHandler.runInTransation(() -> transferRepository.findById(id));
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
            return transferRepository.create(transfer);
         });
    }
}
