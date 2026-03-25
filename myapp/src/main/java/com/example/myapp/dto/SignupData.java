package com.example.myapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupData {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 5, message = "Password must be at least 5 characters")
    private String password;

    // Optional — defaults to "USER" if not provided
    private String role;
}
