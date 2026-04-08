package com.zunoBank.Authentication.controller;

import com.zunoBank.Authentication.dto.CreateStaffRequestDto;
import com.zunoBank.Authentication.dto.StaffResponseDto;
import com.zunoBank.Authentication.service.ManageStaff;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffManagementController {

    private final ManageStaff createStaff;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<StaffResponseDto> createStaff(
            @Valid @RequestBody CreateStaffRequestDto request,
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createStaff.createStaff(request, currentUser.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        return ResponseEntity.ok(createStaff.getAllStaff());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'BRANCH_MANAGER')")
    public ResponseEntity<StaffResponseDto> getStaff(@PathVariable Long id) {
        return ResponseEntity.ok(createStaff.getStaffById(id));
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> suspend(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        createStaff.suspendStaff(id, body.get("reason"));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> reactivate(@PathVariable Long id) {
        createStaff.reactivateStaff(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        createStaff.deactivateStaff(id, body.get("reason"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        String temp = createStaff.resetPassword(id);
        return ResponseEntity.ok(Map.of("tempPassword", temp));
    }
}