package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@CrossOrigin(
        origins = "http://127.0.0.1:5500",   // your actual front-end origin
        allowCredentials = "true"
)

@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    // Send OTP
    @GetMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestParam String phone) {
        String otp = String.valueOf((int)(Math.random() * 9000) + 1000); // Generate 4-digit OTP
        String responseMessage = otpService.sendOtp(phone, otp);

        // Return JSON response
        if (responseMessage.contains("SID:")) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "OTP Sent!",
                    "sid", responseMessage.split("SID:")[1].trim()
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", responseMessage
            ));
        }
    }

    // Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String phone, @RequestParam String otp) {
        boolean isVerified = otpService.verifyOtp(phone, otp);
        if (isVerified) {
            otpService.clearOtp(phone); // Clear OTP once verified
            return ResponseEntity.ok().body(Map.of("success", true, "message", "OTP Verified Successfully!"));
        } else {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", "Invalid or Expired OTP."));
        }
    }
}
