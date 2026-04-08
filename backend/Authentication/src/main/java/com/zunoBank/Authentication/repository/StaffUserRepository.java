package com.zunoBank.Authentication.repository;

import com.zunoBank.Authentication.entity.StaffUser;
import com.zunoBank.Authentication.entity.type.StaffRole;
import com.zunoBank.Authentication.entity.type.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {

    // login — find by employeeId
    Optional<StaffUser> findByEmployeeId(String employeeId);

    // login alternative — find by email
    Optional<StaffUser> findByEmail(String email);

    // check before creating — prevent duplicate email
    boolean existsByEmail(String email);

    // check before creating — prevent duplicate employeeId
    boolean existsByEmployeeId(String employeeId);

    // used in getAllStaff() — newest first
    // Spring Data JPA generates: ORDER BY created_at DESC
    List<StaffUser> findAllByOrderByCreatedAtDesc();

    // filter staff by role
    List<StaffUser> findByRole(StaffRole role);

    // filter staff by status
    List<StaffUser> findByStatus(StaffStatus status);

    // filter by branch — branch manager sees their branch only
    List<StaffUser> findByBranchCode(String branchCode);

    // filter by role AND branch — e.g. all LOAN_OFFICERs in DEL-01
    List<StaffUser> findByRoleAndBranchCode(StaffRole role, String branchCode);

    // find staff reporting to a specific manager
    List<StaffUser> findByManagerId(Long managerId);

    // count by status — used by admin dashboard
    long countByStatus(StaffStatus status);

    // update last login time — called after successful login
    @Modifying
    @Query("UPDATE StaffUser s SET s.lastLoginAt = :time WHERE s.id = :id")
    void updateLastLoginAt(@Param("id") Long id,
                           @Param("time") LocalDateTime time);

    @Modifying
    @Query("UPDATE StaffUser s " +
            "SET s.failedLoginAttempts = s.failedLoginAttempts + 1 " +
            "WHERE s.id = :id")
    void incrementFailedAttempts(@Param("id") Long id);

    // reset failed attempts — called after successful login
    @Modifying
    @Query("UPDATE StaffUser s " +
            "SET s.failedLoginAttempts = 0, s.lockedUntil = null " +
            "WHERE s.id = :id")
    void resetFailedAttempts(@Param("id") Long id);
}