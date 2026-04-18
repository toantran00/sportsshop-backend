package com.example.sportsshop.controller;

import com.example.sportsshop.dto.OrderRequest;
import com.example.sportsshop.entity.Order;
import com.example.sportsshop.entity.Product;
import com.example.sportsshop.repository.OrderRepository;
import com.example.sportsshop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/checkout")
    public String checkout(@Valid @RequestBody OrderRequest Request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Product product = productRepository.findById(Request.getProductId()).orElse(null);
        if (product == null) {
            return "Sản phẩm không tồn tại!";
        }
        if (Request.getQuantity() > product.getQuantity()) {
            return "Xin loi, số lượng đặt hàng vượt quá tồn kho!";
        }
        product.setQuantity(product.getQuantity() - Request.getQuantity());
        productRepository.save(product);
        Order newOrder = new Order();
        newOrder.setCustomerName(currentUsername);
        newOrder.setProductName(product.getName());
        newOrder.setQuantityBought(Request.getQuantity());
        newOrder.setTotalPrice(Request.getQuantity() * product.getPrice());
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("Success");
        orderRepository.save(newOrder);
        return "Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại cửa hàng thể thao của chúng tôi.";
    }
    @GetMapping("/my-history")
    public List<Order> getMyHistory() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderRepository.findByCustomerNameOrderByOrderDateDesc(currentUsername);
    }
}
