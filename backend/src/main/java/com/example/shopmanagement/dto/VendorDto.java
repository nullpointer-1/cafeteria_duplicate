package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorDto {
    private Long id;

    @NotBlank(message = "Vendor name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    private String password;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotNull(message = "Active status is required")
    private Boolean active;

    public VendorDto(String username, String name, String email) {
        this.username=username;
        this.name=name;
        this.email=email;
    }
}