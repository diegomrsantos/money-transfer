package com.revolut.moneytransfer.domain.dao;

import com.revolut.moneytransfer.domain.entity.Account;

import java.math.BigDecimal;

public interface AccountDao extends Dao<Account> {

  boolean increaseBalance(Long accountId, BigDecimal value);

  boolean decreaseBalance(Long accountId, BigDecimal currentBalance, BigDecimal value);
}
