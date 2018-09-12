package com.revolut.moneytransfer.infrastructure.dao;

import com.revolut.moneytransfer.domain.dao.Dao;
import com.revolut.moneytransfer.domain.entity.Entity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class AbstractDao<T extends Entity> implements Dao<T> {

    protected HikariDataSource ds;

    public AbstractDao() {
        this.ds = new HikariDataSource(new HikariConfig("src/main/resources/hikari.properties"));
    }
}