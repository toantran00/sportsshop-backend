package com.example.sportsshop.repository;

import com.example.sportsshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo tên đăng nhập
    Optional<User> findFirstByUsername(String username);

    // Kiểm tra tên đăng nhập đã tồn tại chưa
    boolean existsByUsername(String username);
}
