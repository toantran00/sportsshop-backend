package com.example.sportsshop.config;

import com.example.sportsshop.entity.Role;
import com.example.sportsshop.entity.User;
import com.example.sportsshop.repository.RoleRepository;
import com.example.sportsshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Tự động tạo dữ liệu mặc định khi app khởi động lần đầu:
 *  - Roles: ROLE_ADMIN, ROLE_CUSTOMER
 *  - Users: admin / 123456 (ADMIN), toan / 123456 (CUSTOMER)
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Đảm bảo 2 role tồn tại trong DB
        Role adminRole = roleRepository.findFirstByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(createRole("ROLE_ADMIN")));

        Role customerRole = roleRepository.findFirstByName("ROLE_CUSTOMER")
                .orElseGet(() -> roleRepository.save(createRole("ROLE_CUSTOMER")));

        // 2. Tạo hoặc update user ADMIN mặc định
        User admin = userRepository.findFirstByUsername("admin").orElse(new User());
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setEmail("admin@shopeesport.vn");
        admin.setAddress("Hà Nội, Việt Nam");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);
        System.out.println("✅ Đã tạo/update tài khoản admin (admin / 123456)");

        // 3. Tạo hoặc update user CUSTOMER mặc định
        User customer = userRepository.findFirstByUsername("toan").orElse(new User());
        customer.setUsername("toan");
        customer.setPassword(passwordEncoder.encode("123456"));
        customer.setEmail("toan008888@gmail.com");
        customer.setAddress("TP. Hồ Chí Minh, Việt Nam");
        customer.setRoles(Set.of(customerRole));
        userRepository.save(customer);
        System.out.println("✅ Đã tạo/update tài khoản toan (toan / 123456)");
    }

    private Role createRole(String name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
