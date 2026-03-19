package com.zunoBank.AccountManagemnet.entity.type;

public enum AccountStatus {
    PENDING_APPROVAL,   // RO submitted, waiting for manager
    ACTIVE,             // Manager approved
    REJECTED,           // Manager rejected
    FROZEN,             // Admin/Manager froze
    DORMANT,            // No activity for 180 days
    CLOSED              // Soft deleted
}
