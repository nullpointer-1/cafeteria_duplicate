package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationDto {
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    @NotBlank(message = "OTP is required")
    private String otp;
}