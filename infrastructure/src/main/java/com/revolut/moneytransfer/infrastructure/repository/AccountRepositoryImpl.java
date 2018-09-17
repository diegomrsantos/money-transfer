package com.revolut.moneytransfer.infrastructure.repository;

import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.repository.AccountRepository;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import org.apache.commons.dbutils.QueryRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;


public class AccountRepositoryImpl implements AccountRepository {

    private static final String INSERT_INTO_ACCOUNT = "INSERT INTO account (user_id, balance) VALUES(?, ?)";
    private static final String SELECT_FROM_ACCOUNT_BY_ID = "SELECT * FROM account WHERE id = ?";
    private static final String INCREASE_ACCOUNT_BALANCE_BY_ID = "UPDATE account SET balance = balance + ? WHERE id = ?";
    private static final String DECREASE_ACCOUNT_BALANCE_BY_ID = "UPDATE account SET balance = balance - ? WHERE id = ? and balance = ?";
    private static final String DELETE_FROM_ACCOUNT_BY_ID = "DELETE from account WHERE id = ?";

    private static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred";

    private TransactionHandler transactionHandler;

    public AccountRepositoryImpl(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @Override
    public Account create(Account account) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.insert(currentConnection,
                INSERT_INTO_ACCOUNT,
                    resultSet -> {
                        resultSet.next();
                        return Account.of(resultSet.getLong(1), account.getUserId(), account.getBalance());
                    },
                    account.getUserId(), account.getBalance());
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    @Override
    public Optional<Account> findById(Long id) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.query(currentConnection,
                SELECT_FROM_ACCOUNT_BY_ID,
                    resultSet -> {
                        if (resultSet.next()) {
                            return Optional.of(Account.of(resultSet.getLong(1),
                                resultSet.getLong(2), resultSet.getBigDecimal(3)));
                        } else {
                            return Optional.empty();
                        }
                    },
                    id);
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    public boolean increaseBalance(Long accountId, BigDecimal value) {

        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.update(currentConnection, INCREASE_ACCOUNT_BALANCE_BY_ID, value, accountId) == 1;
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    public boolean decreaseBalance(Long accountId, BigDecimal currentBalance, BigDecimal value) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.update(currentConnection, DECREASE_ACCOUNT_BALANCE_BY_ID, value, accountId, currentBalance) == 1;
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.update(currentConnection, DELETE_FROM_ACCOUNT_BY_ID, id) == 1;
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }
}
