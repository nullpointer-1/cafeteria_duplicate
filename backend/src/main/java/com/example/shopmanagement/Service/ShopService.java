package com.example.shopmanagement.Service;

import com.example.shopmanagement.dto.ShopDto;
import com.example.shopmanagement.model.Shop;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    // Create a new food outlet
    public Shop createShop(Shop shop) {
        Vendor vendor = new Vendor();
        vendor.setEmail("vendor@example.com");
        return shopRepository.save(shop);
    }

    // Get a list of all food outlets
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    // Get a specific food outlet by ID
    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }
}
