package com.zunoBank.AccountManagemnet.util;

import com.zunoBank.AccountManagemnet.entity.Account;
import com.zunoBank.AccountManagemnet.entity.type.AccountStatus;
import com.zunoBank.AccountManagemnet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DormancyScheduler {

    private final AccountRepository accountRepository;

    @Value("${account.dormancy-days}")
    private int dormancyDays;

    // runs every day at 1:00 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void markDormantAccounts() {

        LocalDateTime cutoff = LocalDateTime.now().minusDays(dormancyDays);

        List<Account> candidates = accountRepository
                .findByStatusAndLastTransactionAtBefore(
                        AccountStatus.ACTIVE, cutoff);

        if (!candidates.isEmpty()) {
            candidates.forEach(a -> a.setStatus(AccountStatus.DORMANT));
            accountRepository.saveAll(candidates);
            System.out.println("[DormancyScheduler] Marked "
                    + candidates.size() + " account(s) as DORMANT at "
                    + LocalDateTime.now());
        }
    }
}
