package com.zunoBank.Authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponseDto {
    private int status;
    private String message;
    private Object data;
}
