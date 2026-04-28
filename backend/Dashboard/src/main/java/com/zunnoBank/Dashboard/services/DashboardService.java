package com.zunnoBank.Dashboard.services;

import com.zunnoBank.Dashboard.dto.DashboardStatsDTO;
import com.zunnoBank.Dashboard.feignClients.CustomerFeign;
import com.zunnoBank.Dashboard.feignClients.LoanFeign;
import com.zunnoBank.Dashboard.feignClients.TransactionFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionFeign transactionFeign;
    private final LoanFeign loanFeign;
    private final CustomerFeign customerFeign;

    public DashboardStatsDTO getStats() {  // ✅ THIS is correct place

        long todaysTransactions = transactionFeign.getTodayTransactions();

        long currentCustomers = customerFeign.getTotalCustomers();

        // 🔥 You need to create this API
        long lastMonthCustomers = customerFeign.getLastMonthCustomers();

        BigDecimal totalDeposits = transactionFeign.getTotalDeposits();
        BigDecimal lastMonthDeposits = transactionFeign.getLastMonthDeposits(); // ✅ now exists
        long yesterdayTransactions = transactionFeign.getYesterdayTransactions();

        return new DashboardStatsDTO(
                currentCustomers,
                lastMonthCustomers,
                totalDeposits,
                lastMonthDeposits,
                todaysTransactions,
                yesterdayTransactions
        );
    }

//    private double calculateGrowth(double current, double previous) {
//        if (previous == 0) return 100; // avoid divide by zero
//        return ((current - previous) / previous) * 100;
//    }
}