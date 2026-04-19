package com.example.sportsshop.controller;

import com.example.sportsshop.entity.CartItem;
import com.example.sportsshop.entity.Product;
import com.example.sportsshop.repository.CartItemRepository;
import com.example.sportsshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. LẤY GIỎ HÀNG CỦA TÔI
    @GetMapping
    public List<CartItem> getMyCart() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartItemRepository.findByUsername(username);
    }

    // 2. THÊM VÀO GIỎ HÀNG
    @PostMapping("/add/{productId}")
    public ResponseEntity<String> addToCart(@PathVariable Integer productId, @RequestParam(defaultValue = "1") int quantity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (quantity < 1) {
            return ResponseEntity.badRequest().body("Số lượng thêm vào giỏ phải lớn hơn 0");
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) return ResponseEntity.status(404).body("Sản phẩm không tồn tại!");

        // Kiểm tra xem món này có trong giỏ chưa
        Optional<CartItem> existingItem = cartItemRepository.findByUsernameAndProductId(username, productId);

        if (existingItem.isPresent()) {
            // Có rồi thì cộng dồn số lượng
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getQuantity() < newQuantity) {
                return ResponseEntity.badRequest().body("Kho không đủ hàng cho số lượng bạn chọn!");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Chưa có thì tạo mới
            if (product.getQuantity() < quantity) {
                return ResponseEntity.badRequest().body("Kho không đủ hàng!");
            }
            CartItem newItem = new CartItem();
            newItem.setUsername(username);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
        return ResponseEntity.ok("Đã thêm " + product.getName() + " vào giỏ hàng!");
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateQuantity(@PathVariable Integer productId, @RequestParam int quantity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (quantity < 0) {
            return ResponseEntity.badRequest().body("Số lượng không hợp lệ");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByUsernameAndProductId(username, productId);
        if (existingItem.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm trong giỏ");
        }

        if (quantity == 0) {
            cartItemRepository.delete(existingItem.get());
            return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(404).body("Sản phẩm không tồn tại!");
        }
        if (quantity > product.getQuantity()) {
            return ResponseEntity.badRequest().body("Kho chỉ còn " + product.getQuantity() + " sản phẩm");
        }

        CartItem item = existingItem.get();
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return ResponseEntity.ok("Cập nhật số lượng thành công");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Integer productId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<CartItem> existingItem = cartItemRepository.findByUsernameAndProductId(username, productId);
        if (existingItem.isEmpty()) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm trong giỏ");
        }

        cartItemRepository.delete(existingItem.get());
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<String> removeByCartItemId(@PathVariable Long cartItemId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<CartItem> existingItem = cartItemRepository.findById(cartItemId);
        if (existingItem.isEmpty() || !username.equals(existingItem.get().getUsername())) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm trong giỏ");
        }

        cartItemRepository.delete(existingItem.get());
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }
}