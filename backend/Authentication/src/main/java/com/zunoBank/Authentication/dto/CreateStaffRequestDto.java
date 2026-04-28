package com.zunoBank.Authentication.dto;

import com.zunoBank.Authentication.entity.type.StaffRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStaffRequestDto {
    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required")
    private StaffRole role;

    @NotBlank(message = "Branch code is required")
    private String branchCode;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phoneNumber;

    private String department; 

    private String designation;

    private Long managerId;

}
