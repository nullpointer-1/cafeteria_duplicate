package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.OrderDto;
import com.example.shopmanagement.dto.ProductDTO;
import com.example.shopmanagement.dto.ShopDto;
import com.example.shopmanagement.model.Order;
import com.example.shopmanagement.model.User;
import com.example.shopmanagement.repository.OrderRepository;
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.repository.ShopRepository;
import com.example.shopmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderDto placeOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setTotal(orderDto.getTotal());
        order.setStatus("PENDING");
        order.setPaymentStatus("PAID");
        
        // Set other order properties and save
        Order savedOrder = orderRepository.save(order);
        return convertToOrderDto(savedOrder);
    }

    public List<OrderDto> getCurrentUserOrders() {
        String username = jwtService.getCurrentUsername();
        User user = userRepository.findByPhoneNumber(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(user.getId())
            .stream()
            .map(this::convertToOrderDto)
            .collect(Collectors.toList());
    }



    private OrderDto convertToOrderDto(Order order) {
        // Implementation for converting Order to OrderDto
        return new OrderDto();
    }

    @Transactional(readOnly = true)
    public List<ShopDto> getAvailableShops() {
        return shopRepository.findByActive(true).stream()
                .map(shop -> {
                    ShopDto dto = new ShopDto();
                    dto.setId(shop.getId());
                    dto.setName(shop.getName());
                    dto.setLocation(shop.getLocation());
                    dto.setShopType(shop.getShopType());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getShopProducts(Long shopId) {
        return productRepository.findByVendorId(shopId).stream()  // You may need to update the method name to findByVendorShopId() if required.
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    dto.setId(product.getId());
                    dto.setName(product.getName());
                    dto.setCategory(product.getCategory());
                    dto.setPrice(product.getPrice());
                    dto.setAvailable(product.isAvailable());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderDetails(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return convertToOrderDto(order);  // Assuming convertToOrderDto() will be responsible for converting Order to OrderDto
    }

}