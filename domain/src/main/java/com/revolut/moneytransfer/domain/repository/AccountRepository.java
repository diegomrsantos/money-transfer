package com.revolut.moneytransfer.domain.repository;

import com.revolut.moneytransfer.domain.entity.Account;

import java.math.BigDecimal;

public interface AccountRepository extends Repository<Account> {

  boolean increaseBalance(Long accountId, BigDecimal value);

  boolean decreaseBalance(Long accountId, BigDecimal currentBalance, BigDecimal value);

}
