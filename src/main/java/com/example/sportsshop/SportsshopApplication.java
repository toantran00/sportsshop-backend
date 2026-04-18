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
}
