package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDto {
    private Long id;

    @NotBlank(message = "Shop name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Shop type is required")
    private String shopType;

    @NotNull(message = "Active status is required")
    private Boolean active;

    private Long vendorId;
    private String vendorName;

    private String vendorEmail;

    private String vendorUsername;

    private String vendorPassword;

    private String vendorContactNumber;

}
