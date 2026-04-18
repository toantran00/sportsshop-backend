package com.example.sportsshop.repository;

import com.example.sportsshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {
    // Thêm dòng này: Tìm đơn hàng theo tên khách và sắp xếp từ mới tới cũ
    List<Order> findByCustomerNameOrderByOrderDateDesc(String customerName);
}
