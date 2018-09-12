package com.revolut.moneytransfer.infrastructure.dao;

import com.revolut.moneytransfer.domain.entity.Account;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;
import java.util.List;

import javax.money.MonetaryAmount;

public class AccountDao extends AbstractDao<Account> {

    @Override
    public Account create(Account entity) {
        return null;
    }

    @Override
    public Account findById(Long id) {
        QueryRunner run = new QueryRunner(this.ds);
        try {
            return run.query("", resultSet -> new Account(null, null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Account update(Account entity) {
        QueryRunner run = new QueryRunner(this.ds);
        try {
            run.update("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public boolean increaseBalance(Long accountId, MonetaryAmount value) {
        return false;
    }

    public boolean decreaseBalance(Long accountId, MonetaryAmount currentBalance, MonetaryAmount value) {
        return false;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Account> getAll() {
        return null;
    }
}
