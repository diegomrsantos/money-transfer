package com.revolut.moneytransfer.domain.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.revolut.moneytransfer.domain.entity.Account;

public interface AccountService {

    Account create(Long userId);

    Optional<Account> findById(Long id);

    boolean delete(Long id);

    void deposit(Long id, BigDecimal value);
}
