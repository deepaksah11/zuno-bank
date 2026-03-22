package com.zunoBank.Authentication.entity;

import com.zunoBank.Authentication.entity.type.StaffRole;
import com.zunoBank.Authentication.entity.type.StaffStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "staff_users")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", unique = true, nullable = false, length = 20)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private StaffRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StaffStatus status;

    @Column(name = "branch_code", nullable = false, length = 20)
    private String branchCode;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "designation", length = 100)
    private String designation;
    // e.g. "Senior Relationship Officer" — human readable title

    @Column(name = "requires_password_change", nullable = false)
    private boolean requiresPasswordChange;

    @Column(name = "temp_password_expires_at")
    private LocalDateTime tempPasswordExpiresAt;
    // temp password valid for 48 hours only

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;
    // incremented on wrong password, reset on success

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    // account locked after 5 failures — locked for 30 minutes

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private StaffUser manager;
    // the branch manager this person reports to
    // null for BRANCH_MANAGER and above

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private StaffUser createdBy;
    // who created this account — audit trail
    // null only for seeded SUPER_ADMIN

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    @Column(name = "deactivation_reason", length = 255)
    private String deactivationReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── UserDetails implementation ───────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // FIXED — was returning List.of() which broke all @PreAuthorize checks
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        // ADMIN     → ROLE_ADMIN
        // BRANCH_MANAGER → ROLE_BRANCH_MANAGER
        // matches hasRole('ADMIN') in @PreAuthorize
    }

    @Override
    public String getPassword() {
        // removed @Nullable — passwordHash is never null
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return employeeId;
        // staff logs in with employeeId
        // this goes into JWT "sub" claim
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // false if account is temporarily locked due to failed attempts
        if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // SUSPENDED and DEACTIVATED cannot log in
        // FIRST_LOGIN can log in but is forced to change password
        return status == StaffStatus.ACTIVE ||
                status == StaffStatus.FIRST_LOGIN;
    }
}