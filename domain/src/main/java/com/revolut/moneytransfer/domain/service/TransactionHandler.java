package com.revolut.moneytransfer.domain.service;

import java.sql.Connection;

public interface TransactionHandler {
    <T> T runInTransation(Transaction<T> transaction);
    Connection getCurrentConnection();

}
