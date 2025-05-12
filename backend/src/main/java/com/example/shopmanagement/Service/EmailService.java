package com.example.shopmanagement.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void sendContactEmail(String name, String email, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email); // where you receive
        mailMessage.setSubject("Congratulations , your credentials in our cafeteria got generated!!");

      
        mailMessage.setText(message);
        mailMessage.setFrom("sanjeevsaisasank9@gmail.com");
        
        System.out.println(message +" ,"+ name+" , "+email);

        mailSender.send(mailMessage);
    }
    @Async
    public void sendEmail(String to, String subject, String body) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Set to true if you want to send HTML email
            mailSender.send(mimeMessage);
            System.out.println("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
        }
    }
}