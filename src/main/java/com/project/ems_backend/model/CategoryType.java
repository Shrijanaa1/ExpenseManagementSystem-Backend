package com.project.ems_backend.model;

public enum CategoryType {
    SALARY(TransactionType.INCOME),
    INTEREST(TransactionType.INCOME),
    RENT(TransactionType.INCOME),
    FREELANCING(TransactionType.INCOME),
    FOOD(TransactionType.EXPENSE),
    ENTERTAINMENT(TransactionType.EXPENSE),
    SHOPPING(TransactionType.EXPENSE),
    TRAVEL(TransactionType.EXPENSE),
    EDUCATION(TransactionType.EXPENSE),
    OTHERS(TransactionType.EXPENSE);

    private final TransactionType type;

    CategoryType(TransactionType type) {
        this.type = type;
    }

    public TransactionType getType() {
        return type;
    }
}

