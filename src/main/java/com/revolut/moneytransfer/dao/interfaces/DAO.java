package com.revolut.moneytransfer.dao.interfaces;

import java.util.List;
import java.util.Map;

public interface DAO<T> {

    T create(T entity);

    T findById(Long id);

    Map<Long, T> findByIdsAndLock(Long... ids);

    T update(T entity);

    void delete(Long id);

    List<T> getAll();
}
