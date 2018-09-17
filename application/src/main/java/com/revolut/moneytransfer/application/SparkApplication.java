package com.revolut.moneytransfer.application;


import com.revolut.moneytransfer.application.controller.AccountController;
import com.revolut.moneytransfer.application.controller.TransferController;
import com.revolut.moneytransfer.domain.service.AccountService;
import com.revolut.moneytransfer.domain.service.TransferService;
import com.revolut.moneytransfer.infrastructure.dao.AccountDaoImpl;
import com.revolut.moneytransfer.infrastructure.dao.TransferDaoImpl;
import com.revolut.moneytransfer.infrastructure.transaction.TransactionHandlerImpl;

public class SparkApplication {

    public static void main(String[] args) {
        final TransactionHandlerImpl transactionHandler = new TransactionHandlerImpl();
        final AccountDaoImpl accountDao = new AccountDaoImpl(transactionHandler);
        new AccountController(new AccountService(transactionHandler, accountDao));
        new TransferController(new TransferService(new TransferDaoImpl(transactionHandler), accountDao, transactionHandler));
    }
}
