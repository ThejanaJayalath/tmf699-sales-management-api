package com.tmforum.salesmanagement.model;

public enum SalesLeadStateType {
    ACKNOWLEDGED("acknowledged"),
    PENDING("pending"),
    ACCEPTED("accepted"),
    QUALIFIED("qualified"),
    REJECTED("rejected"),
    CONVERTED("converted"),
    INPROGRESS("inProgress"),
    CANCELLED("cancelled");

    private final String value;

    SalesLeadStateType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}