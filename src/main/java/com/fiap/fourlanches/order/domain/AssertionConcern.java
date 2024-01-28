package com.fiap.fourlanches.order.domain;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public final class AssertionConcern {

    private AssertionConcern() {
    }

    public static boolean isNotEmpty(String string) {
        return !StringUtils.isEmpty(string);
    }

    public static boolean isPositive(BigDecimal number) {
        return !ObjectUtils.isEmpty(number) && (number.compareTo(BigDecimal.ZERO) > 0);
    }

}
