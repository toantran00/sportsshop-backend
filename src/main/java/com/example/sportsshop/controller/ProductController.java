package com.example.sportsshop.controller;

import com.example.sportsshop.entity.Product;
import com.example.sportsshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
       Pageable pageable = PageRequest.of(page, size);
       if(keyword != null && !keyword.trim().isEmpty()) {
           return productRepository.findByNameContaining(keyword, pageable);
       }
       return productRepository.findAll(pageable);
    }
    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    //Tìm sản phẩm qua id
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable int id) {
        return productRepository.findById(id).orElse(null);
    }

    //Cập nhật sản phẩm qua id
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable int id, @RequestBody Product product) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setCategory(product.getCategory());
            return productRepository.save(existingProduct);
        }
        return null;
    }
    //Xóa sản phẩm bằng API
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable int id) {
        if(productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return "Sản phẩm có id " + id + " đã được xóa.";
        }
        return "Không tìm thấy sản phẩm này";
    }




}
