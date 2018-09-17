package com.revolut.moneytransfer.domain.service;

import com.revolut.moneytransfer.domain.exception.BusinessException;

public interface Transaction<T> {
    T execute() throws BusinessException;
}
