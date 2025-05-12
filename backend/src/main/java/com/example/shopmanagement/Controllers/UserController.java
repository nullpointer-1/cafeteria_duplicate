package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.dto.OrderDto;
//import com.example.shopmanagement.dto.UserDto;
import com.example.shopmanagement.Service.UserService;
import com.example.shopmanagement.dto.OtpRequestDto;
import com.example.shopmanagement.dto.OtpVerificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
        origins = "http://127.0.0.1:5500",   // your actual front-end origin
        allowCredentials = "true"
)

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @GetMapping("/shops")
    public ResponseEntity<?> getAvailableShops() {
        return ResponseEntity.ok(userService.getAvailableShops());
    }

    @GetMapping("/shops/{shopId}/products")
    public ResponseEntity<?> getShopProducts(@PathVariable Long shopId) {
        return ResponseEntity.ok(userService.getShopProducts(shopId));
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(@RequestBody @Valid OrderDto orderDto) {
        OrderDto savedOrder = userService.placeOrder(orderDto);
        
        // Notify vendor through WebSocket
        messagingTemplate.convertAndSend(
            "/topic/orders/" + savedOrder.getShopId(), 
            savedOrder
        );
        
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
        return ResponseEntity.ok(userService.getOrderDetails(orderId));
    }

    @GetMapping("/orders/history")
    public ResponseEntity<?> getOrderHistory() {
        return ResponseEntity.ok(userService.getCurrentUserOrders());
    }
}