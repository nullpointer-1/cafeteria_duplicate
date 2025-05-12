package com.example.shopmanagement.Service;


import com.example.shopmanagement.dto.CredentialDto;
import com.example.shopmanagement.dto.LoginDto;
import com.example.shopmanagement.dto.VendorDto;
import com.example.shopmanagement.model.Vendor;
import com.example.shopmanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendorService {

	@Autowired
	private VendorRepository vendorRepository;

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private EmailService emailService;

	public Vendor addIntoDb(CredentialDto dto) {
		String email = dto.getEmail();
		String userName = dto.getUsername();
		String passWord = dto.getPassword();
		Optional<Vendor> obj = this.getShopById(dto.getVendorId());
		Vendor vendor = obj.orElseThrow(() -> new RuntimeException("Vendor not found"));
		vendor.setEmail(email);
		vendor.setUsername(userName);
		vendor.setPassword(passWord);
		String subject="Congratulations , your credentials in our cafeteria got generated!!";
		String message= "The admin has generated the credentials for you /n "
				+ "Your username is :"+userName +"/n" + "your current password is : "+ passWord;
//		emailService.sendEmail(email,subject,message);
		emailService.sendContactEmail(userName, email, message);
//		System.out.println(vendor.getId() + " " + vendor.getName() + " " + vendor.getCreatedAt() + " " + vendor.getContactNumber());
//		System.out.println(dto.getShopId() + " " + email + " " + userName + " " + passWord);
		return vendorRepository.save(vendor);
	}

	public Optional<Vendor> getShopById(Long id) {
		return vendorRepository.findById(id);
	}
	public ResponseEntity<?> login(LoginDto loginDto) {
		Vendor vendor = vendorRepository.findByUsername(loginDto.getUsername())
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		// Validate password (either plain-text or password encoded)
		if (!loginDto.getPassword().equals(vendor.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		// Generate JWT token
		String token = jwtService.generateToken(vendor.getUsername());

		// Prepare response
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("vendor", convertToDto(vendor));
		System.out.println("Generated Token: " + token);


		return ResponseEntity.ok(response); // Return token and vendor data
	}

//
//	public VendorDto getCurrentVendor() {
//		String username = jwtService.getCurrentUsername();
//		return vendorRepository.findByUsername(username)
//				.map(this::convertToDto)
//				.orElseThrow(() -> new RuntimeException("Vendor not found"));
//	}

	public List<VendorDto> getAllVendors() {
		return vendorRepository.findAll().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	public VendorDto getVendorById(Long id) {
		return vendorRepository.findById(id)
				.map(this::convertToDto)
				.orElseThrow(() -> new RuntimeException("Vendor not found"));
	}




	@Transactional
	public void deleteVendor(Long id) {
		if (!vendorRepository.existsById(id)) {
			throw new RuntimeException("Vendor not found");
		}
		vendorRepository.deleteById(id);
	}

//
//	public Map<String, Object> getVendorStats() {
//		String username = jwtService.getCurrentUsername();
//		Vendor vendor = vendorRepository.findByUsername(username)
//				.orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//		Map<String, Object> stats = new HashMap<>();
//		stats.put("totalProducts", 0); // Implement product count
//		stats.put("pendingOrders", 0); // Implement pending orders count
//		stats.put("completedToday", 0); // Implement completed orders count
//		stats.put("todayRevenue", 0.0); // Implement today's revenue calculation
//
//		return stats;
//	}

	private VendorDto convertToDto(Vendor vendor) {
		return new VendorDto(
				vendor.getId(),
				vendor.getName(),
				vendor.getEmail(),
				vendor.getUsername(),
				null, // Don't send password
				vendor.getContactNumber(),
				vendor.isActive()
		);
	}

	private void updateVendorFromDto(Vendor vendor, VendorDto dto) {
		vendor.setName(dto.getName());
		vendor.setEmail(dto.getEmail());
		vendor.setUsername(dto.getUsername());
		vendor.setContactNumber(dto.getContactNumber());
		vendor.setActive(dto.getActive());
	}
}