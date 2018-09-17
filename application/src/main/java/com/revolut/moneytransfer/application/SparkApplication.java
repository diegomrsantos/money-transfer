package com.revolut.moneytransfer.application;


import com.revolut.moneytransfer.application.resource.AccountResource;
import com.revolut.moneytransfer.application.resource.TransferResources;
import com.revolut.moneytransfer.domain.repository.AccountRepository;
import com.revolut.moneytransfer.domain.service.AccountServiceImpl;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import com.revolut.moneytransfer.domain.service.TransferServiceImpl;
import com.revolut.moneytransfer.infrastructure.repository.AccountRepositoryImpl;
import com.revolut.moneytransfer.infrastructure.repository.TransferRepositoryImpl;
import com.revolut.moneytransfer.infrastructure.transaction.TransactionHandlerImpl;

public class SparkApplication {

    public static void main(String[] args) {
        final TransactionHandler transactionHandler = new TransactionHandlerImpl();
        final AccountRepository accountRepository = new AccountRepositoryImpl(transactionHandler);
        new AccountResource(new AccountServiceImpl(transactionHandler, accountRepository));
        new TransferResources(new TransferServiceImpl(new TransferRepositoryImpl(transactionHandler), accountRepository, transactionHandler));
    }
}
