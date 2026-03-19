package com.zunoBank.AccountManagemnet.repository;

import com.zunoBank.AccountManagemnet.entity.AccountSequence;
import com.zunoBank.AccountManagemnet.entity.AccountSequenceId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountSequenceRepository
        extends JpaRepository<AccountSequence, AccountSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AccountSequence s WHERE s.id = :id")
    Optional<AccountSequence> findByIdWithLock(
            @Param("id") AccountSequenceId id);
}
