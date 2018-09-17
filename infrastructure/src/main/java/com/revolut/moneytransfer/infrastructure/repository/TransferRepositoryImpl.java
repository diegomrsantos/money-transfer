package com.revolut.moneytransfer.infrastructure.repository;

import com.revolut.moneytransfer.domain.entity.Transfer;
import com.revolut.moneytransfer.domain.repository.Repository;
import com.revolut.moneytransfer.domain.service.TransactionHandler;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class TransferRepositoryImpl implements Repository<Transfer> {

    private static final String INSERT_INTO_TRANSFER = "INSERT INTO transfer (from_account_id, to_account_id, amount) VALUES(?, ?, ?)";
    private static final String SELECT_FROM_TRANSFER_BY_ID = "SELECT * FROM transfer WHERE id = ?";
    private static final String UNEXPECTED_ERROR_MSG = "An unexpected error occurred";
    private TransactionHandler transactionHandler;

    public TransferRepositoryImpl(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @Override
    public Transfer create(Transfer entity) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.insert(currentConnection,
                INSERT_INTO_TRANSFER,
                    resultSet -> {
                        resultSet.next();
                        return Transfer.of(resultSet.getLong(1), entity.getFromAccountId(),
                                entity.getToAccountId(), entity.getAmount());
                    },
                    entity.getFromAccountId(), entity.getToAccountId(), entity.getAmount());
        } catch (SQLException e) {
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    @Override
    public Optional<Transfer> findById(Long id) {
        final Connection currentConnection = transactionHandler.getCurrentConnection();
        QueryRunner run = new QueryRunner();
        try {
            return run.query(currentConnection,
                SELECT_FROM_TRANSFER_BY_ID,
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
            throw new RuntimeException(UNEXPECTED_ERROR_MSG, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        throw new UnsupportedOperationException();
    }
}
