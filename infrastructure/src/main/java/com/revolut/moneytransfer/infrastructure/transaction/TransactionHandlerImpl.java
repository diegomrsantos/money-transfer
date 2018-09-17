package com.revolut.moneytransfer.infrastructure.transaction;

import com.revolut.moneytransfer.domain.exception.BusinessException;
import com.revolut.moneytransfer.domain.service.Transaction;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;

public class TransactionHandlerImpl implements TransactionHandler {

    private static final ThreadLocal<Connection> contextHolder = new ThreadLocal<>();
    private static final HikariDataSource dataSource = new HikariDataSource(new HikariConfig("/hikari.properties"));

    @Override
    public <T> T runInTransation(Transaction<T> transaction) {
        T result = null;
        try (Connection connection = dataSource.getConnection()) {

            contextHolder.set(connection);
            connection.setAutoCommit(false);

            try {
                result = transaction.execute();
                connection.commit();

            } catch (Exception e){
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error ocurred.", e);
        } finally {
            contextHolder.remove();
        }
        return result;
    }

    public Connection getCurrentConnection() {
        final Connection connection = contextHolder.get();
        if(connection != null) {
            return connection;
        } else {
            throw new IllegalStateException("There is no open connection.");
        }
    }
}
