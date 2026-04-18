package com.example.sportsshop;

import com.example.sportsshop.entity.Category;
import com.example.sportsshop.entity.Product;
import com.example.sportsshop.repository.CategoryRepository;
import com.example.sportsshop.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SportsshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SportsshopApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(CategoryRepository categoryRepo, ProductRepository productRepo) {
		return args -> {
			// Kiểm tra xem kho có trống không, trống thì mới nhét vào
			if (categoryRepo.count() == 0) {
				// 1. Tạo 2 cái Danh mục
				Category cat1 = new Category(0, "Giày Cầu Lông");
				Category cat2 = new Category(0, "Vợt Cầu Lông");
				categoryRepo.save(cat1);
				categoryRepo.save(cat2);

				// 2. Nhập kho 3 món hàng
				productRepo.save(new Product(0, "Giày Kawasaki C32011", 1200000, 50, cat1));
				productRepo.save(new Product(0, "Vợt Yonex Astrox 88D Pro", 3500000, 20, cat2));
				productRepo.save(new Product(0, "Vợt Lining Halbertec 8000", 3200000, 15, cat2));

				System.out.println("====== ĐÃ NHẬP KHO THÀNH CÔNG! ======");
			}
		};
	}

}
