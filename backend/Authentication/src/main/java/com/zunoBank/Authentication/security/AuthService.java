package com.zunoBank.Authentication.security;

import com.zunoBank.Authentication.dto.LoginRequestDto;
import com.zunoBank.Authentication.dto.LoginResponseDto;
import com.zunoBank.Authentication.entity.StaffUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmployeeId(), loginRequestDto.getPassword())
        );
        StaffUser staffUser = (StaffUser) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(staffUser);

        return new LoginResponseDto(token, staffUser.getId());
    }
}
