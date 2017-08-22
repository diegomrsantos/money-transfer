package com.revolut.moneytransfer.dao.interfaces;

public interface TransactionalDAO<T> extends DAO<T>, Transactional {
}
