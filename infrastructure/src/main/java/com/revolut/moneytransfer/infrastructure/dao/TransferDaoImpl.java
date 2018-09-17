package com.revolut.moneytransfer.infrastructure.dao;

import com.revolut.moneytransfer.domain.dao.Dao;
import com.revolut.moneytransfer.domain.entity.Account;
import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransferDaoImpl implements Dao<Transfer> {

    private TransactionHandler transactionHandler;

    public TransferDaoImpl(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @Override
    public Transfer create(Transfer entity) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.insert(currentConnection,
                    "INSERT INTO transfer (from_account_id, to_account_id, amount) VALUES(?, ?, ?)",
                    resultSet -> {
                        resultSet.next();
                        return Transfer.of(resultSet.getLong(1), entity.getToAccountId(),
                                entity.getToAccountId(), entity.getAmount());
                    },
                    entity.getFromAccountId(), entity.getToAccountId(), entity.getAmount());
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.query(currentConnection,
                    "SELECT * FROM transfer WHERE id = ?",
                    resultSet -> {
                        if (resultSet.next()) {
                            return Optional.of(Transfer.of(resultSet.getLong(1), resultSet.getLong(2),
                                    resultSet.getLong(3), resultSet.getBigDecimal(4)));
                        } else {
                            return Optional.empty();
                        }
                    },
                    id);
        } catch (SQLException e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
    }

    @Override
    public Void delete(Long id) {
        return null;
    }

    @Override
    public List<Transfer> getAll() {
        return null;
    }
}
