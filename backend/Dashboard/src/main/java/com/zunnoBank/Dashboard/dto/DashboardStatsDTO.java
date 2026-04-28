package com.zunnoBank.Dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {

    private long totalCustomers;
    private long totalCustomersLastMonth;

    private BigDecimal totalDeposits;
    private BigDecimal totalDepositsLastMonth;

    private long todaysTransactions;
    private long yesterdayTransactions;

}