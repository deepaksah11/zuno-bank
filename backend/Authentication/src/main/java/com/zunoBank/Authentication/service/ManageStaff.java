package com.zunoBank.Authentication.service;

import com.zunoBank.Authentication.dto.ChangePasswordRequestDto;
import com.zunoBank.Authentication.dto.ApiResponseDto;
import com.zunoBank.Authentication.dto.CreateStaffRequestDto;
import com.zunoBank.Authentication.dto.StaffResponseDto;

import java.util.List;

public interface ManageStaff {
    public StaffResponseDto createStaff(CreateStaffRequestDto createStaffRequestDto, String createdByEmployeeId);

    List<StaffResponseDto> getAllStaff();

    // get single staff by id
    StaffResponseDto getStaffById(Long id);

    // suspend a staff account temporarily
    void suspendStaff(Long id, String reason);

    // reactivate a suspended account
    void reactivateStaff(Long id);

    // permanently deactivate — only SUPER_ADMIN
    void deactivateStaff(Long id, String reason);

    // reset password — generate new temp password, email it
    String resetPassword(Long id);

    ApiResponseDto changePassword(String employeeId, ChangePasswordRequestDto request);
}
