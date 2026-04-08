package com.zunoBank.Authentication.dto;

import com.zunoBank.Authentication.entity.type.StaffRole;
import com.zunoBank.Authentication.entity.type.StaffStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffResponseDto {

    // these match StaffUser field names exactly
    // ModelMapper maps them automatically
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private StaffRole role;
    private StaffStatus status;
    private String branchCode;
    private String phoneNumber;
    private String department;
    private String designation;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime passwordChangedAt;

    // this does NOT match any field in StaffUser directly
    // mapped via custom typeMap in ModelMapperConfig
    private String createdByEmployeeId;

    // this field exists to allow ModelMapper to skip it explicitly
    // it will always be null in the response — never populated
    private String passwordHash;
}