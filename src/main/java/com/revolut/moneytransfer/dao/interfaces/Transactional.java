package com.revolut.moneytransfer.dao.interfaces;

public interface Transactional {

    void beginTransaction();

    void commit();

    void rollback();
}
