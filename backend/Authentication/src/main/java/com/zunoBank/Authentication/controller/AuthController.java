package com.zunoBank.Authentication.controller;

import com.zunoBank.Authentication.dto.ChangePasswordRequestDto;
import com.zunoBank.Authentication.dto.ApiResponseDto;
import com.zunoBank.Authentication.dto.LoginRequestDto;
import com.zunoBank.Authentication.dto.LoginResponseDto;
import com.zunoBank.Authentication.entity.StaffUser;
import com.zunoBank.Authentication.security.AuthService;
import com.zunoBank.Authentication.service.ManageStaff;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/staff")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ManageStaff manageStaff;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        System.out.println("LOGIN API HIT");
        LoginResponseDto response = authService.login(loginRequestDto);

        ResponseCookie clearCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/") // MUST match
                .maxAge(0) // delete
                .sameSite("Lax")
                .build();
        // 🔐 Create HTTP-only cookie
        ResponseCookie newCookie = ResponseCookie.from("token", response.getJwt())
                .httpOnly(true)
                .secure(false) // ⚠️ true in production (HTTPS)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString()) // delete old
                .header(HttpHeaders.SET_COOKIE, newCookie.toString())
                .body(new ApiResponseDto(
                        HttpStatus.OK.value(), // ✅ FIXED
                        "Login successful",
                        Map.of(
                                "id", response.getId(),
                                "name", response.getName(),
                                "role", response.getRole()
                        )
                ));
    }

    @GetMapping("/hash")
    public String hash() {
        return passwordEncoder.encode("Pass@123");
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponseDto> changePassword(
            @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
            @AuthenticationPrincipal UserDetails currentUser) {

        // currentUser.getUsername() = employeeId from JWT
        ApiResponseDto apiResponseDto = manageStaff.changePassword(currentUser.getUsername(), changePasswordRequestDto);

        return ResponseEntity.ok(apiResponseDto);
    }

    @GetMapping("/check-auth")
    public ResponseEntity<ApiResponseDto> checkAuth(
            @AuthenticationPrincipal StaffUser user
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto(
                            401,
                            "Not authenticated",
                            null
                    ));
        }
        return ResponseEntity.ok(
                new ApiResponseDto(
                        200,
                        "User authenticated",
                        Map.of(
                                "id", user.getId(),
                                "name", user.getFullName(),
                                "role", user.getRole()
                        )
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0) // delete cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "Logged out"));
    }
}
