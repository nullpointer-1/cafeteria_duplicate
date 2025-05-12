package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.ShopDto;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ShopDTOService {

    // Convert ShopDto to Shop entity
    public Shop convertToEntity(ShopDto shopDto) {
        Shop shop = new Shop();
        shop.setId(shopDto.getId());
        shop.setName(shopDto.getName());
        shop.setLocation(shopDto.getLocation());
        shop.setContactNumber(shopDto.getContactNumber());
        shop.setShopType(shopDto.getShopType());
        shop.setActive(shopDto.getActive());

        // Create a new vendor for the shop
        Vendor vendor = new Vendor();
        vendor.setName(shopDto.getVendorName());
        vendor.setEmail(shopDto.getVendorEmail());
        vendor.setUsername(shopDto.getVendorUsername());
        vendor.setPassword(shopDto.getVendorPassword());
        vendor.setContactNumber(shopDto.getVendorContactNumber());
        vendor.setActive(true);

        // Set bidirectional relationship
        shop.setVendor(vendor);       // Set vendor on shop
        vendor.setShop(shop);         // Set shop on vendor ← THIS IS THE MISSING PART

        return shop;
    }


    // Convert Shop entity to ShopDto
    public ShopDto convertToDTO(Shop shop) {
        Vendor vendor = shop.getVendor();

        return new ShopDto(
                shop.getId(),
                shop.getName(),
                shop.getLocation(),
                shop.getContactNumber(),
                shop.getShopType(),
                shop.isActive(),
                vendor != null ? vendor.getId() : null,
                vendor != null ? vendor.getName() : null,
                vendor != null ? vendor.getEmail() : null,
                vendor != null ? vendor.getUsername() : null,
                vendor != null ? vendor.getPassword() : null,
                vendor != null ? vendor.getContactNumber() : null
        );
    }

}
