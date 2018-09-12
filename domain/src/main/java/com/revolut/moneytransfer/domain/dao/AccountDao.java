package com.revolut.moneytransfer.domain.dao;

import javax.money.MonetaryAmount;

import com.revolut.moneytransfer.domain.entity.Account;

public interface AccountDao extends Dao<Account> {

  public boolean increaseBalance(Long accountId, MonetaryAmount value);

  public boolean decreaseBalance(Long accountId, MonetaryAmount currentBalance, MonetaryAmount value);
}
