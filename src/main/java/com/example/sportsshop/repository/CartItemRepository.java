package com.example.sportsshop.repository;

import com.example.sportsshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 1. Lấy toàn bộ đồ trong giỏ của 1 người
    List<CartItem> findByUsername(String username);
    // 2. Tìm xem món đồ này đã có trong giỏ chưa (để cộng dồn số lượng)
    Optional<CartItem> findByUsernameAndProductId(String username, Integer productId);

    void deleteByProductId(Integer productId);

    void deleteByUsernameAndProductId(String username, Integer productId);

    // 3. Đổ rác (Xóa sạch giỏ sau khi chốt đơn thành công)
    void deleteByUsername(String username);
}
