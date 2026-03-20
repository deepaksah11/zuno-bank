package com.zunoBank.AccountManagemnet.entity.type;

public enum CustomerStatus {
    PENDING_APPROVAL,   // RO submitted, waiting for manager
    ACTIVE,             // Manager approved, CIF generated
    REJECTED,           // Manager rejected
    INACTIVE
}
