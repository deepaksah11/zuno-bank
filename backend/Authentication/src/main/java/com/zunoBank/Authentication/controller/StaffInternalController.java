package com.zunoBank.Authentication.controller;

import com.zunoBank.Authentication.dto.StaffResponseDto;
import com.zunoBank.Authentication.entity.StaffUser;
import com.zunoBank.Authentication.error.ResourceNotFoundException;
import com.zunoBank.Authentication.repository.StaffUserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/staff")
@RequiredArgsConstructor
public class StaffInternalController {

    private final StaffUserRepository staffUserRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/{employeeId}")
    public ResponseEntity<StaffResponseDto> getStaffByEmployeeId(
            @PathVariable String employeeId) {

        StaffUser staff = staffUserRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Staff not found: " + employeeId));

        return ResponseEntity.ok(modelMapper.map(staff, StaffResponseDto.class));
    }
}
