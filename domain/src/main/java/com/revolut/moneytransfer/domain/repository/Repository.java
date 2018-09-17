package com.revolut.moneytransfer.domain.repository;

import java.util.Optional;

public interface Repository<T> {

    T create(T entity);

    Optional<T> findById(Long id);

    boolean delete(Long id);
}
