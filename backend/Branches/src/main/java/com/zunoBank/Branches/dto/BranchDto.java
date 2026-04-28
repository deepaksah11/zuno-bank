package com.zunoBank.Branches.dto;

import lombok.Data;

@Data
public class BranchDto {
    private Long id;
    private String branchCode;
    private String branchName;
    private String city;
    private String state;
    private boolean active;
}