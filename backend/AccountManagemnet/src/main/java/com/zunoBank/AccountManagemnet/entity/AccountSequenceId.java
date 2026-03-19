package com.zunoBank.AccountManagemnet.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSequenceId implements Serializable {
    private String branchCode;
    private int accountType;
}
