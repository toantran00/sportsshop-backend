package com.example.sportsshop.controller;

import com.example.sportsshop.dto.AuthRequest;
import com.example.sportsshop.dto.RegisterRequest;
import com.example.sportsshop.entity.Role;
import com.example.sportsshop.entity.User;
import com.example.sportsshop.repository.RoleRepository;
import com.example.sportsshop.repository.UserRepository;
import com.example.sportsshop.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============================
    // ĐĂNG NHẬP
    // ============================
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        User user = userRepository.findFirstByUsername(request.getUsername()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Sai tài khoản hoặc mật khẩu!");
        }

        // Lấy role đầu tiên (ROLE_ADMIN hoặc ROLE_CUSTOMER)
        String role = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_CUSTOMER");

        // Strip prefix "ROLE_" để nhất quán với token claim
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return ResponseEntity.ok(jwtUtil.generateToken(user.getUsername(), role));
    }

    // ============================
    // ĐĂNG KÝ
    // ============================
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!");
        }

        // Tìm role CUSTOMER trong DB, nếu chưa có thì tạo mới
        Role customerRole = roleRepository.findFirstByName("ROLE_CUSTOMER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_CUSTOMER");
                    return roleRepository.save(r);
                });

        // Tạo user mới
        User newUser = new User();
        newUser.setUsername(request.getUsername().trim());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail());
        newUser.setAddress(request.getAddress());
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        newUser.setRoles(roles);

        userRepository.save(newUser);

        // Auto-login: trả về JWT luôn
        return ResponseEntity.ok(jwtUtil.generateToken(newUser.getUsername(), "CUSTOMER"));
    }
}