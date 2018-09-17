package com.revolut.moneytransfer.domain.exception;

public class MoneyTransferException extends BusinessException {
    public MoneyTransferException() {
    }

    public MoneyTransferException(String s) {
        super(s);
    }

    public MoneyTransferException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public MoneyTransferException(Throwable throwable) {
        super(throwable);
    }

    public MoneyTransferException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }

}
