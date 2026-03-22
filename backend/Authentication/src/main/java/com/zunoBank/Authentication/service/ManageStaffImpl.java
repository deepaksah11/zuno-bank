package com.zunoBank.Authentication.service;

import com.zunoBank.Authentication.dto.ChangePasswordRequestDto;
import com.zunoBank.Authentication.dto.ApiResponseDto;
import com.zunoBank.Authentication.dto.CreateStaffRequestDto;
import com.zunoBank.Authentication.dto.StaffResponseDto;
import com.zunoBank.Authentication.entity.StaffUser;
import com.zunoBank.Authentication.entity.type.StaffRole;
import com.zunoBank.Authentication.entity.type.StaffStatus;
import com.zunoBank.Authentication.error.DuplicateResourceException;
import com.zunoBank.Authentication.error.InvalidRoleAssignmentException;
import com.zunoBank.Authentication.error.ResourceNotFoundException;
import com.zunoBank.Authentication.repository.StaffUserRepository;
import com.zunoBank.Authentication.utils.EmployeeIdGenerator;
import com.zunoBank.Authentication.utils.TempPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManageStaffImpl implements ManageStaff {

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder     passwordEncoder;
    private final EmailService        emailService;
    private final EmployeeIdGenerator employeeIdGenerator;
    private final ModelMapper         modelMapper;
    // injected automatically because we declared it as a @Bean

    // ─────────────────────────────────────────────────────────────────────
    // PUBLIC METHODS
    // ─────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public StaffResponseDto createStaff(CreateStaffRequestDto createStaffRequestDto,
                                        String createdByEmployeeId) {

        // 1. find who is creating
        StaffUser creator = staffUserRepository
                .findByEmployeeId(createdByEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Creator not found: " + createdByEmployeeId));

        // 2. check permission
        validateCreationPermission(
                creator.getRole(),
                createStaffRequestDto.getRole()
        );

        // 3. check duplicate email
        if (staffUserRepository.existsByEmail(createStaffRequestDto.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + createStaffRequestDto.getEmail()
                            + "' is already registered");
        }

        // 4. resolve manager
        StaffUser manager = resolveManager(creator, createStaffRequestDto);

        // 5. generate credentials
        String employeeId   = employeeIdGenerator.generate();
        String tempPassword = TempPasswordGenerator.generate();

        // 6. build entity
        StaffUser newStaff = StaffUser.builder()
                .employeeId(employeeId)
                .fullName(createStaffRequestDto.getFullName())
                .email(createStaffRequestDto.getEmail())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .role(createStaffRequestDto.getRole())
                .status(StaffStatus.FIRST_LOGIN)
                .branchCode(createStaffRequestDto.getBranchCode())
                .phoneNumber(createStaffRequestDto.getPhoneNumber())
                .department(createStaffRequestDto.getDepartment())
                .designation(createStaffRequestDto.getDesignation())
                .requiresPasswordChange(true)
                .tempPasswordExpiresAt(LocalDateTime.now().plusHours(48))
                .failedLoginAttempts(0)
                .manager(manager)
                .createdBy(creator)
                .build();

        StaffUser saved = staffUserRepository.save(newStaff);

        // 7. send welcome email async
        emailService.sendWelcomeEmail(
                saved.getEmail(),
                saved.getFullName(),
                saved.getEmployeeId(),
                tempPassword,
                saved.getRole().name()
        );

        log.info("Staff created — employeeId: {}, role: {}, by: {}",
                employeeId, createStaffRequestDto.getRole(), createdByEmployeeId);

        // 8. map entity to response DTO using ModelMapper
        return toResponse(saved);
    }

    @Override
    public List<StaffResponseDto> getAllStaff() {
        return staffUserRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                // this::toResponse calls the private toResponse() below
                // which uses ModelMapper
                .toList();
    }

    @Override
    public StaffResponseDto getStaffById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional
    public void suspendStaff(Long id, String reason) {
        StaffUser staff = findById(id);

        if (staff.getStatus() == StaffStatus.DEACTIVATED) {
            throw new InvalidRoleAssignmentException(
                    "Cannot suspend a deactivated account");
        }
        if (staff.getRole() == StaffRole.SUPER_ADMIN) {
            throw new InvalidRoleAssignmentException(
                    "Cannot suspend the SUPER_ADMIN account");
        }

        staff.setStatus(StaffStatus.SUSPENDED);
        staff.setDeactivationReason(reason);
        staffUserRepository.save(staff);

        log.info("Staff suspended. id: {}, reason: {}", id, reason);
    }

    @Override
    @Transactional
    public void reactivateStaff(Long id) {
        StaffUser staff = findById(id);

        if (staff.getStatus() == StaffStatus.DEACTIVATED) {
            throw new InvalidRoleAssignmentException(
                    "Cannot reactivate a deactivated account. Create a new one.");
        }

        staff.setStatus(StaffStatus.ACTIVE);
        staff.setDeactivationReason(null);
        staffUserRepository.save(staff);

        log.info("Staff reactivated. id: {}", id);
    }

    @Override
    @Transactional
    public void deactivateStaff(Long id, String reason) {
        StaffUser staff = findById(id);

        if (staff.getRole() == StaffRole.SUPER_ADMIN) {
            throw new InvalidRoleAssignmentException(
                    "Cannot deactivate the SUPER_ADMIN account");
        }

        staff.setStatus(StaffStatus.DEACTIVATED);
        staff.setDeactivationReason(reason);
        staff.setDeactivatedAt(LocalDateTime.now());
        staffUserRepository.save(staff);

        log.info("Staff deactivated. id: {}, reason: {}", id, reason);
    }

    @Override
    @Transactional
    public String resetPassword(Long id) {
        StaffUser staff = findById(id);

        if (staff.getStatus() == StaffStatus.DEACTIVATED) {
            throw new InvalidRoleAssignmentException(
                    "Cannot reset password for a deactivated account");
        }

        String newTempPassword = TempPasswordGenerator.generate();

        staff.setPasswordHash(passwordEncoder.encode(newTempPassword));
        staff.setRequiresPasswordChange(true);
        staff.setTempPasswordExpiresAt(LocalDateTime.now().plusHours(48));
        staff.setStatus(StaffStatus.FIRST_LOGIN);
        staffUserRepository.save(staff);

        emailService.sendWelcomeEmail(
                staff.getEmail(),
                staff.getFullName(),
                staff.getEmployeeId(),
                newTempPassword,
                staff.getRole().name()
        );

        log.info("Password reset for staff id: {}", id);
        return newTempPassword;
    }

    public void changePassword(String email, String oldPassword, String newPassword) {

        StaffUser user = staffUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Invalid old password");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setRequiresPasswordChange(false);
        user.setStatus(StaffStatus.ACTIVE);

        staffUserRepository.save(user);
    }

    @Override
    @Transactional
    public ApiResponseDto changePassword(String employeeId, ChangePasswordRequestDto changePasswordRequestDto) {

        // 1. find the staff member
        StaffUser staff = staffUserRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found: " + employeeId));

        // 2. verify current password is correct
        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), staff.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // 3. new password and confirm must match
        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // 4. new password cannot be same as current
        if (passwordEncoder.matches(changePasswordRequestDto.getNewPassword(), staff.getPasswordHash())) {
            throw new RuntimeException("New password cannot be same as current password");
        }

        // 5. update password and set status to ACTIVE
        staff.setPasswordHash(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        staff.setRequiresPasswordChange(false);
        staff.setPasswordChangedAt(LocalDateTime.now());
        staff.setStatus(StaffStatus.ACTIVE);
        staffUserRepository.save(staff);

        log.info("Password changed by staff: {}", employeeId);

        return new ApiResponseDto(
                200,
                "Password changed successfully",
                null
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Converts StaffUser entity to StaffResponseDto using ModelMapper.
     *
     * ModelMapper handles automatically:
     *   id              → id
     *   employeeId      → employeeId
     *   fullName        → fullName
     *   email           → email
     *   role            → role
     *   status          → status
     *   branchCode      → branchCode
     *   phoneNumber     → phoneNumber
     *   department      → department
     *   designation     → designation
     *   createdAt       → createdAt
     *   lastLoginAt     → lastLoginAt
     *   passwordChangedAt → passwordChangedAt
     *
     * ModelMapper handles via custom typeMap in ModelMapperConfig:
     *   createdBy.employeeId → createdByEmployeeId
     *   passwordHash         → skipped (always null in response)
     */
    private StaffResponseDto toResponse(StaffUser staff) {
        return modelMapper.map(staff, StaffResponseDto.class);
        // one line replaces the entire manual builder chain
    }

    private void validateCreationPermission(StaffRole creatorRole,
                                            StaffRole targetRole) {
        if (targetRole == StaffRole.SUPER_ADMIN) {
            throw new InvalidRoleAssignmentException(
                    "SUPER_ADMIN cannot be created via API");
        }

        switch (creatorRole) {
            case SUPER_ADMIN -> { }
            case ADMIN -> {
                if (targetRole != StaffRole.BRANCH_MANAGER)
                    throw new InvalidRoleAssignmentException(
                            "ADMIN can only create BRANCH_MANAGER. Tried: " + targetRole);
            }
            case BRANCH_MANAGER -> {
                boolean allowed =
                        targetRole == StaffRole.RELATIONSHIP_OFFICER ||
                                targetRole == StaffRole.LOAN_OFFICER         ||
                                targetRole == StaffRole.TELLER               ||
                                targetRole == StaffRole.SUPPORT_AGENT;
                if (!allowed)
                    throw new InvalidRoleAssignmentException(
                            "BRANCH_MANAGER can only create field staff. Tried: " + targetRole);
            }
            default -> throw new InvalidRoleAssignmentException(
                    "You cannot create staff accounts. Your role: " + creatorRole);
        }
    }

    private StaffUser resolveManager(StaffUser creator,
                                     CreateStaffRequestDto dto) {
        if (dto.getRole() == StaffRole.ADMIN ||
                dto.getRole() == StaffRole.BRANCH_MANAGER) {
            return null;
        }
        if (creator.getRole() == StaffRole.BRANCH_MANAGER) {
            return creator;
        }
        if (dto.getManagerId() != null) {
            StaffUser manager = staffUserRepository
                    .findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found: " + dto.getManagerId()));

            if (manager.getRole() != StaffRole.BRANCH_MANAGER) {
                throw new InvalidRoleAssignmentException(
                        "Specified manager must be a BRANCH_MANAGER. Found: "
                                + manager.getRole());
            }
            if (!manager.getBranchCode().equals(dto.getBranchCode())) {
                throw new InvalidRoleAssignmentException(
                        "Manager and staff must belong to the same branch");
            }
            return manager;
        }
        log.warn("Field staff created without manager. Role: {}, Email: {}",
                dto.getRole(), dto.getEmail());
        return null;
    }

    private StaffUser findById(Long id) {
        return staffUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found with id: " + id));
    }
}