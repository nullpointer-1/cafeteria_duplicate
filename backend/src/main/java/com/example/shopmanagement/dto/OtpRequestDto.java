package com.example.shopmanagement.dto;

public class OtpRequestDto {
    private String phoneNumber;
    private String otp;

    // Getter and Setter methods
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
