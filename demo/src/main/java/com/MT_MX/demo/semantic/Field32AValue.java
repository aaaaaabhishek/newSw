package com.MT_MX.demo.semantic;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Field32AValue {

    private final LocalDate valueDate;
    private final String currency;
    private final BigDecimal amount;

    public Field32AValue(LocalDate valueDate, String currency, BigDecimal amount) {
        this.valueDate = valueDate;
        this.currency = currency;
        this.amount = amount;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}