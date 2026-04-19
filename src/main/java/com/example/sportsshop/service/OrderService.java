package com.example.sportsshop.service;

import com.example.sportsshop.entity.CartItem;
import com.example.sportsshop.entity.Order;
import com.example.sportsshop.entity.Product;
import com.example.sportsshop.entity.User;
import com.example.sportsshop.repository.CartItemRepository;
import com.example.sportsshop.repository.OrderRepository;
import com.example.sportsshop.repository.ProductRepository;
import com.example.sportsshop.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public String checkoutCart(String username) {
        // 1. Lấy hết đồ trong giỏ ra
        List<CartItem> cart = cartItemRepository.findByUsername(username);
        if (cart.isEmpty()) {
            return "Giỏ hàng trống trơn! Hãy thêm sản phẩm trước khi chốt đơn.";
        }

        double total = 0;
        int totalQuantity = 0;
        StringBuilder orderDetails = new StringBuilder();

        // 2. Quét từng món: kiểm tra kho, trừ kho và tính tiền
        for (CartItem item : cart) {
            Product p = item.getProduct();
            if (p.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                    "Món \"" + p.getName() + "\" không đủ hàng! Kho chỉ còn " + p.getQuantity() + " sản phẩm."
                );
            }
            p.setQuantity(p.getQuantity() - item.getQuantity());
            productRepository.save(p);

            total += (p.getPrice() * item.getQuantity());
            totalQuantity += item.getQuantity();
            orderDetails.append("- ").append(p.getName())
                         .append(" x").append(item.getQuantity())
                         .append(" = ").append(String.format("%,.0f", p.getPrice() * item.getQuantity())).append(" đ\n");
        }

        // 3. Lấy thông tin user để lưu vào đơn hàng
        User user = userRepository.findFirstByUsername(username).orElse(null);
        String customerEmail = (user != null && user.getEmail() != null) ? user.getEmail() : "";
        String shippingAddress = (user != null && user.getAddress() != null) ? user.getAddress() : "Chưa cập nhật";

        // 4. Tạo hóa đơn
        Order order = new Order();
        order.setCustomerName(username);
        order.setCustomerEmail(customerEmail);
        order.setShippingAddress(shippingAddress);
        order.setProductName("Đơn hàng gồm " + cart.size() + " loại sản phẩm");
        order.setQuantityBought(totalQuantity);
        order.setTotalPrice(total);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("SUCCESS");
        orderRepository.save(order);

        // 5. Dọn sạch giỏ hàng
        cartItemRepository.deleteByUsername(username);

        // 6. Gửi Email xác nhận (Bọc Try-Catch để lỡ sai config mail thì đơn vẫn được lưu)
        if (customerEmail != null && !customerEmail.isBlank()) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(customerEmail);
                message.setSubject("🎉 ShopeeSport — Đặt hàng thành công!");
                message.setText(
                    "Chào " + username + ",\n\n" +
                    "Cảm ơn bạn đã mua sắm tại ShopeeSport!\n\n" +
                    "Chi tiết đơn hàng:\n" + orderDetails +
                    "\nTổng thanh toán: " + String.format("%,.0f", total) + " đ" +
                    "\nĐịa chỉ giao: " + shippingAddress +
                    "\n\nChúng tôi sẽ xử lý và giao hàng sớm nhất có thể. Cảm ơn!\n\nShopeeSport Team 🏅"
                );
                mailSender.send(message);
            } catch (Exception e) {
                System.out.println("⚠️ Đơn đã lưu nhưng gửi mail thất bại: " + e.getMessage());
            }
        }

        return "🎉 Chốt đơn thành công! " + (customerEmail.isBlank() ? "" : "Biên lai đã gửi qua " + customerEmail + ".");
    }
}
