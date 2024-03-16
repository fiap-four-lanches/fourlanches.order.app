package com.fiap.fourlanches.order.domain.valueobjects;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    CREATED("created"),
    IN_PREPARATION("in_preparation"),
    RECEIVED("received"),
    READY("ready"),
    FINISHED("finished"),
    CANCELED("canceled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    public static OrderStatus fromString(String text) {
        for (OrderStatus o : OrderStatus.values()) {
            if (o.value.equalsIgnoreCase(text)) {
                return o;
            }
        }
        return null;
    }
}
