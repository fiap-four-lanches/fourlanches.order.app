package com.fiap.fourlanches.order.application.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FailPublishToQueueExceptionTest {

    @Test
    public void givenAnQueuePublishErrorThenThrowAnFAilPublishToQueueException() {
        String errorMsg = "error message";
        Throwable cause = new Throwable();

        var exception = new FailPublishToQueueException(errorMsg, cause);
        assertEquals(errorMsg, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}