package com.zunoBank.Authentication.security;

import com.zunoBank.Authentication.repository.StaffUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomStaffDetailsService implements UserDetailsService {

    private final StaffUserRepository staffUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return staffUserRepository.findByEmployeeId(username).orElseThrow();
    }
}
