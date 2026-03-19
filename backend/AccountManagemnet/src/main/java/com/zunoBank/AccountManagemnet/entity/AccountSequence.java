package com.zunoBank.AccountManagemnet.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_sequence")
@Data
@NoArgsConstructor
public class AccountSequence {

    @EmbeddedId
    private AccountSequenceId id;

    private int lastSequence;
}
