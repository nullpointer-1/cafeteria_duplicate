package com.example.shopmanagement.Service;

import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;

    // Get all products for a specific vendor
    public List<Product> getProductsByVendorId(Long vendorId) {
        return productRepository.findByVendorId(vendorId);
    }

    // Get a product by its ID
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    // Save a new product for a vendor
    public Product saveProduct(Product product, Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found with ID: " + vendorId));
        product.setVendor(vendor);
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(Long productId, Product updatedProduct, Long vendorId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setAvailable(updatedProduct.isAvailable());
        existingProduct.setVendor(vendor);

        return productRepository.save(existingProduct);
    }

    // Delete a product
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    // Update availability of a product
    public Product updateAvailability(Long productId, boolean available) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setAvailable(available);
        return productRepository.save(product);
    }
}
