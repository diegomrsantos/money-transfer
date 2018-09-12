package com.revolut.moneytransfer.domain.dao;

import java.util.List;
import java.util.Map;

public interface Dao<T> {

    T create(T entity);

    T findById(Long id);

    T update(T entity);

    void delete(Long id);

    List<T> getAll();
}
