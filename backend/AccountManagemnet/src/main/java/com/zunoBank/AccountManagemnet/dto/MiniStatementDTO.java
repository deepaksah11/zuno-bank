package com.zunoBank.AccountManagemnet.dto;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MiniStatementDTO {
    private String referenceId;
    private BigDecimal amount;
    private String type;
    private String description;
    private String status;
    private LocalDateTime date;
}