package com.revolut.moneytransfer.domain.service;

import java.util.Optional;

import com.revolut.moneytransfer.domain.entity.Transfer;

public interface TransferService {

  Optional<Transfer> findById(Long id);

  Transfer transferMoney(Transfer transfer);
}
