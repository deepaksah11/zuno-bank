package com.zunoBank.Authentication.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String employeeId;
    private String password;
}
