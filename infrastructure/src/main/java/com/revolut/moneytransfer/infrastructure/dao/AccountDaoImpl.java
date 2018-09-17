package com.revolut.moneytransfer.infrastructure.dao;

import com.revolut.moneytransfer.domain.dao.AccountDao;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import org.apache.commons.dbutils.QueryRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class AccountDaoImpl implements AccountDao {

    private TransactionHandler transactionHandler;

    public AccountDaoImpl(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @Override
    public Account create(Account account) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.insert(currentConnection,
                    "INSERT INTO account (user_id, balance) VALUES(?, ?)",
                    resultSet -> {
                        resultSet.next();
                        return Account.of(resultSet.getLong(1), account.getUserId(), account.getBalance());
                    },
                    account.getUserId(), account.getBalance());
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Optional<Account> findById(Long id) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.query(currentConnection,
                    "SELECT * FROM account WHERE id = ?",
                    resultSet -> {
                        if (resultSet.next()) {
                            return Optional.of(Account.of(resultSet.getLong(1), resultSet.getLong(2), resultSet.getBigDecimal(3)));
                        } else {
                            return Optional.empty();
                        }
                    },
                    id);
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public boolean increaseBalance(Long accountId, BigDecimal value) {

        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.update(currentConnection,
                    "UPDATE account SET balance = balance + ? WHERE id = ?",
                    value, accountId) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    public boolean decreaseBalance(Long accountId, BigDecimal currentBalance, BigDecimal value) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.update(currentConnection,
                    "UPDATE account SET balance = balance - ? WHERE id = ? and balance = ?",
                    value, accountId, currentBalance) == 1;
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Void delete(Long id) {
        return null;
    }

    @Override
    public List<Account> getAll() {
        return null;
    }
}
