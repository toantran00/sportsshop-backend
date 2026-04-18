package com.example.sportsshop.controller;

import com.example.sportsshop.dto.AuthRequest;
import com.example.sportsshop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {

        if ("admin".equals(request.getUsername()) && "123".equals(request.getPassword())) {
            return jwtUtil.generateToken("admin", "ADMIN");
        }

        else if ("toan".equals(request.getUsername()) && "123".equals(request.getPassword())) {
            return jwtUtil.generateToken("toan", "CUSTOMER");
        }

        return "Sai tài khoản hoặc mật khẩu!";
    }
}