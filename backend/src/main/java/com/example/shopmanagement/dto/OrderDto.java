package com.example.shopmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderId;
    
    @NotNull(message = "Shop ID is required")
    private Long shopId;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemDto> items;
    
    @NotNull(message = "Total amount is required")
    private Double total;
    
    private String status;
    private String paymentStatus;
    private String qrCode;
}