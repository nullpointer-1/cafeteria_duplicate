package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.dto.ShopDto;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.Service.ShopService;
import com.example.shopmanagement.Service.ShopDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
@RestController
@CrossOrigin(
        origins = "http://127.0.0.1:5500",   // your actual front-end origin
        allowCredentials = "true"
)

@RequestMapping("/api/shops")
public class ShopController {

    private final ShopService shopService;
    private final ShopDTOService shopDTOService;

    @Autowired
    public ShopController(ShopService shopService, ShopDTOService shopDTOService) {
        this.shopService = shopService;
        this.shopDTOService = shopDTOService;
    }

    // Create a new shop (food outlet) with a new vendor
    @PostMapping
    public ResponseEntity<ShopDto> createShop(@Valid @RequestBody ShopDto shopDto) {
        Shop shop = shopDTOService.convertToEntity(shopDto); // Convert DTO to Entity
        Shop createdShop = shopService.createShop(shop);
        ShopDto responseDto = shopDTOService.convertToDTO(createdShop); // Convert back to DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // Get all shops (food outlets)
    @GetMapping
    public ResponseEntity<List<ShopDto>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        List<ShopDto> shopDtos = shops.stream()
                .map(shopDTOService::convertToDTO)
                .toList();
        return new ResponseEntity<>(shopDtos, HttpStatus.OK);
    }

    // Get a shop by ID
    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getShopById(@PathVariable("id") Long id) {
        Optional<Shop> shop = shopService.getShopById(id);
        return shop.map(value -> new ResponseEntity<>(shopDTOService.convertToDTO(value), HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}

