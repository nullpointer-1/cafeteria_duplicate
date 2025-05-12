package com.example.shopmanagement.Controllers;

import com.example.shopmanagement.Service.JwtService;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.ProductDTO;
import com.example.shopmanagement.model.Product;
import com.example.shopmanagement.Service.ProductService;
import com.example.shopmanagement.Service.VendorService;
import com.example.shopmanagement.dto.CredentialDto;
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.ProductRepository;
import com.example.shopmanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(
        origins = "http://127.0.0.1:5500",   // your actual front-end origin
        allowCredentials = "true"
)

public class VendorController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDto loginDto) {
        return vendorService.login(loginDto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getVendorDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String username = jwtService.getUsernameFromToken(token);
        Vendor vendor = vendorRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        return ResponseEntity.ok(convertToDto(vendor));
    }


    public VendorDto convertToDto(Vendor vendor) {
        return new VendorDto(vendor.getUsername(), vendor.getName(), vendor.getEmail());
    }


    @GetMapping
    public ResponseEntity<List<VendorDto>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorDto> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    // Get all products for a specific vendor
    @GetMapping("/{vendorId}/products")
    public ResponseEntity<List<Product>> getVendorProducts(@PathVariable Long vendorId) {
        return ResponseEntity.ok(productService.getProductsByVendorId(vendorId));
    }

    // Add a new product for a vendor
    @PostMapping("/{vendorId}/products")
    public ResponseEntity<String> addProduct(@PathVariable Long vendorId, @RequestBody @Valid ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setAvailable(productDTO.isAvailable());
        productService.saveProduct(product, vendorId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product details has been added");
    }

    // Update an existing product
    @PutMapping("/{vendorId}/products/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long vendorId, @PathVariable Long productId, @RequestBody @Valid ProductDTO productDTO) {
        Product updatedProduct = new Product();
        updatedProduct.setName(productDTO.getName());
        updatedProduct.setDescription(productDTO.getDescription());
        updatedProduct.setPrice(productDTO.getPrice());
        updatedProduct.setCategory(productDTO.getCategory());
        updatedProduct.setAvailable(productDTO.isAvailable());
        return ResponseEntity.ok(productService.updateProduct(productId, updatedProduct, vendorId));
    }

    // Update the availability of a product
    @PutMapping("/{vendorId}/products/{productId}/availability")
    public ResponseEntity<Void> updateAvailability(@PathVariable Long vendorId, @PathVariable Long productId, @RequestBody ProductDTO productDTO) {
        productService.updateAvailability(productId, productDTO.isAvailable());
        return ResponseEntity.ok().build();
    }

    // Delete a product for a vendor
    @DeleteMapping("/{vendorId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long vendorId, @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
//dummy for testing
@GetMapping("/products/{id}")
public ResponseEntity<ProductDTO> getProductDetails(@PathVariable Long id) {
    return productRepository.findById(id)
            .map(product -> {
                ProductDTO dto = new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategory(),
                        product.isAvailable()
                );
                return ResponseEntity.ok(dto);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
}



    // Get a product by its ID
    @GetMapping("/{vendorId}/products/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long vendorId, @PathVariable Long productId) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ProductDTO dto = new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.isAvailable()
        );

        return ResponseEntity.ok(dto);
    }


    // Handle order completion (example)
//    @PutMapping("/orders/{orderId}/complete")
//    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
//        vendorService.completeOrder(orderId);
//        return ResponseEntity.ok().build();
//    }

    // Get all vendor orders
//    @GetMapping("/orders")
//    public ResponseEntity<List<?>> getVendorOrders() {
//        return ResponseEntity.ok(vendorService.getOrders());
//    }

    // Delete a vendor (example)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/credentials")
    public ResponseEntity<String> createCredentials(@RequestBody CredentialDto dto) {
        System.out.println("hiiiiiiiiiiiiiiiiii"+dto.getVendorId());
        Vendor response = vendorService.addIntoDb(dto);
        return new ResponseEntity<>("Credentials added,/nEmail Sent Successfully", HttpStatus.OK);
    }
}
