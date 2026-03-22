package com.zunoBank.Authentication.controller;

import com.zunoBank.Authentication.dto.ChangePasswordRequestDto;
import com.zunoBank.Authentication.dto.ApiResponseDto;
import com.zunoBank.Authentication.dto.LoginRequestDto;
import com.zunoBank.Authentication.dto.LoginResponseDto;
import com.zunoBank.Authentication.security.AuthService;
import com.zunoBank.Authentication.service.ManageStaff;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/staff")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ManageStaff manageStaff;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @GetMapping("/hash")
    public String hash() {
        return passwordEncoder.encode("Admin@2026");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponseDto> changePassword(
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
            @AuthenticationPrincipal UserDetails currentUser) {

        // currentUser.getUsername() = employeeId from JWT
        ApiResponseDto apiResponseDto = manageStaff.changePassword(currentUser.getUsername(), changePasswordRequestDto);

        return ResponseEntity.ok(apiResponseDto);
    }
}
