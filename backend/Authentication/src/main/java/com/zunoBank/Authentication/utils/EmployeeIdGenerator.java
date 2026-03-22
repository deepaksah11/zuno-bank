package com.zunoBank.Authentication.utils;

import com.zunoBank.Authentication.repository.StaffUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class EmployeeIdGenerator {

    private final StaffUserRepository staffUserRepository;

    public String generate() {
        int year = Year.now().getValue();

        // get the count of ALL staff ever created
        // even if some are deleted, ID never repeats
        long nextSequence = staffUserRepository.count() + 1;

        return String.format("EMP-%d-%04d", year, nextSequence);
        // EMP-2026-0001, EMP-2026-0002, etc.
    }
}