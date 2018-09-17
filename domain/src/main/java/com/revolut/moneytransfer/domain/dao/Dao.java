package com.revolut.moneytransfer.domain.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Dao<T> {

    T create(T entity);

    Optional<T> findById(Long id);

    Void delete(Long id);

    List<T> getAll();
}
