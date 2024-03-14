package com.fiap.fourlanches.order.application.exception;

public class FailPublishToQueueException extends RuntimeException {
    public FailPublishToQueueException(String msg, Throwable e) {
        super(msg, e);
    }
}
