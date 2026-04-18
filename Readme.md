# 🏢 Sports Shop Management System - Backend API

Dự án Backend cho hệ thống quản lý cửa hàng đồ thể thao, được xây dựng trên nền tảng **Spring Boot**. Hệ thống cung cấp các API RESTful chuyên nghiệp phục vụ quản lý sản phẩm, danh mục và quy trình đặt hàng với cơ chế bảo mật **JWT (JSON Web Token)**.

## 🚀 Công nghệ sử dụng

| Công nghệ | Phiên bản | Mô tả |
| :--- | :--- | :--- |
| **Java** | 17+ | Ngôn ngữ lập trình chính |
| **Spring Boot** | 3.x | Framework phát triển ứng dụng |
| **Spring Data JPA** | - | Quản lý và thao tác Database (ORM) |
| **Spring Security** | - | Hệ thống bảo mật và phân quyền |
| **JWT (jjwt)** | 0.11.5 | Cơ chế xác thực Token-based |
| **SQL Server** | 2019+ | Hệ quản trị cơ sở dữ liệu |
| **Lombok** | - | Tối ưu hóa code (Getter/Setter/Constructor) |

## ✨ Tính năng nổi bật

- **🔒 Authentication & Authorization**:
    - Đăng nhập và cấp phát **JWT Token** bảo mật.
    - Phân quyền người dùng chặt chẽ: `ADMIN` (có quyền quản lý kho) và `CUSTOMER` (có quyền xem và mua hàng).
- **📦 Product Management (CRUD)**:
    - Quản lý danh mục và sản phẩm (Giày Kawasaki, Vợt Yonex, Lining...).
    - Tự động đồng bộ hóa cấu trúc Database từ Entity Java (JPA).
- **🛒 Checkout Logic (Nghiệp vụ cốt lõi)**:
    - Kiểm tra số lượng tồn kho theo thời gian thực.
    - Tự động trừ kho khi chốt đơn thành công.
    - Lưu trữ lịch sử hóa đơn chi tiết trong bảng `customer_orders`.
- **🛡️ Security Filters**:
    - Hệ thống Filter chặn mọi truy cập trái phép.
    - Bảo mật API theo phương thức (GET công khai, POST/DELETE bảo mật).

## 📂 Cấu trúc dự án

```text
src/main/java/com/example/sportsshop/
├── controller/     # Xử lý các API Endpoints (Auth, Product, Order)
├── dto/            # Các đối tượng vận chuyển dữ liệu (AuthRequest, OrderRequest)
├── entity/         # Các khuôn mẫu dữ liệu (Product, Category, Order)
├── repository/     # Tầng giao tiếp Database (JPA Repository)
└── security/       # Cấu hình bảo mật, JWT Filter và JwtUtil