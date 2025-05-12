package com.example.shopmanagement.Service;

import com.example.shopmanagement.model.User;
import com.example.shopmanagement.repository.UserRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Value("${twilio.whatsappFrom}")
    private String fromWhatsApp;

    @Autowired
    private UserRepository userRepository;

    // Send OTP
    public String sendOtp(String toPhoneNumber, String otp) {
        try {

            if (!toPhoneNumber.startsWith("+")) {
                toPhoneNumber = "+91" + toPhoneNumber;
            }

            String fullTo = "whatsapp:" + toPhoneNumber;

         
            LocalDateTime otpExpiry = LocalDateTime.now().plusMinutes(5);

            // Save the OTP and expiry in the User table
            Optional<User> userOptional = userRepository.findByPhoneNumber(toPhoneNumber);
            User user = userOptional.orElseGet(() -> new User());
            user.setPhoneNumber(toPhoneNumber);
            user.setCurrentOtp(otp);
            user.setOtpExpiry(otpExpiry);
            user.setActive(false); // User is not active until OTP is verified
            userRepository.save(user);

            // Send OTP via WhatsApp using Twilio
            Message message = Message.creator(
                    new PhoneNumber(fullTo),
                    new PhoneNumber(fromWhatsApp),
                    "Your OTP is: " + otp
            ).create();

            return "✅ OTP Sent! SID: " + message.getSid();
        } catch (Exception e) {
            return "❌ Error sending OTP: " + e.getMessage();
        }
    }

    // Verify OTP
    public boolean verifyOtp(String phoneNumber, String inputOtp) {
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+91" + phoneNumber;
        }

        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if OTP has expired
            if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                return false; // OTP has expired
            }

            // Compare the stored OTP with the user input OTP
            if (inputOtp.equals(user.getCurrentOtp())) {
                user.setActive(true); // Activate the user after successful OTP verification
                user.setCurrentOtp(null); // Clear the OTP after verification
                userRepository.save(user); // Update the user in the database
                return true;
            }
        }

        return false; // OTP not found or incorrect
    }

    // Clear OTP from the database (optional)
    public void clearOtp(String phoneNumber) {
        Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);
        userOptional.ifPresent(user -> {
            user.setCurrentOtp(null);
            userRepository.save(user);
        });
    }
}
