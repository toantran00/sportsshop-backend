package com.example.sportsshop.controller;

import com.example.sportsshop.dto.ProductRequest;
import com.example.sportsshop.entity.Category;
import com.example.sportsshop.entity.Product;
import com.example.sportsshop.repository.CartItemRepository;
import com.example.sportsshop.repository.CategoryRepository;
import com.example.sportsshop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ============================
    // LẤY DANH SÁCH SẢN PHẨM (có filter + phân trang + sort)
    // ============================
    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 24);

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("Giá tối thiểu không được lớn hơn giá tối đa");
        }

        // Validate sortBy để tránh injection
        String safeSortBy = switch (sortBy) {
            case "price", "name", "quantity" -> sortBy;
            default -> "id";
        };

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(safeSortBy).descending()
                : Sort.by(safeSortBy).ascending();

        Pageable pageable = PageRequest.of(safePage, safeSize, sort);
        return productRepository.filterProducts(keyword, minPrice, maxPrice, category, pageable);
    }

    // ============================
    // LẤY DANH SÁCH DANH MỤC
    // ============================
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(Category::getName)
                .toList();
    }

    // ============================
    // THÊM SẢN PHẨM MỚI
    // ============================
    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductRequest request) {
        Product product = new Product();
        applyRequestToProduct(product, request);
        return ResponseEntity.ok(productRepository.save(product));
    }

    // ============================
    // TÌM SẢN PHẨM THEO ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================
    // CẬP NHẬT SẢN PHẨM
    // ============================
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @Valid @RequestBody ProductRequest request) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    applyRequestToProduct(existingProduct, request);
                    return ResponseEntity.ok(productRepository.save(existingProduct));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ============================
    // XÓA SẢN PHẨM
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Không tìm thấy sản phẩm này");
        }
        try {
            cartItemRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            return ResponseEntity.ok("Sản phẩm #" + id + " đã được xóa thành công.");
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(409).body("Không thể xóa sản phẩm vì đang có dữ liệu liên quan.");
        }
    }

    // ============================
    // UPLOAD ẢNH SẢN PHẨM
    // ============================
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File rỗng, không thể upload!");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            return ResponseEntity.badRequest().body("File không hợp lệ!");
        }

        // Chỉ cho phép ảnh
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!ext.matches("\\.(jpg|jpeg|png|gif|webp)")) {
            return ResponseEntity.badRequest().body("Chỉ chấp nhận file ảnh (jpg, png, gif, webp)!");
        }

        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String newFileName = UUID.randomUUID() + ext;
            Path path = Paths.get(uploadDir + newFileName);
            Files.write(path, file.getBytes());

            return ResponseEntity.ok("/uploads/" + newFileName);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi lưu file: " + e.getMessage());
        }
    }

    // ============================
    // HELPER
    // ============================
    private void applyRequestToProduct(Product product, ProductRequest request) {
        Category category = categoryRepository.findByNameIgnoreCase(request.getCategoryName())
                .orElseGet(() -> categoryRepository.save(new Category(0, request.getCategoryName().trim())));

        product.setName(request.getName().trim());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
    }
}
