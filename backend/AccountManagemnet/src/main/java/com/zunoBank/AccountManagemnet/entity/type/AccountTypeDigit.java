package com.zunoBank.AccountManagemnet.entity.type;

public enum AccountTypeDigit {

    SAVINGS(1),
    CURRENT(2),
    LOAN(3);

    private final int digit;

    AccountTypeDigit(int digit) {
        this.digit = digit;
    }

    public int getDigit() {
        return digit;
    }

    public static int fromAccountType(AccountType type) {
        return switch (type) {
            case SAVINGS -> 1;
            case CURRENT -> 2;
        };
    }
}
