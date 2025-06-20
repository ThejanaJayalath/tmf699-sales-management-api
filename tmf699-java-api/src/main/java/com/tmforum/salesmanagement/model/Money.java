package com.tmforum.salesmanagement.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class Money {
    private String unit;
    private BigDecimal value;

    public Money() {}

    public Money(String unit, BigDecimal value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}