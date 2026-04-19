package com.example.sportsshop.repository;

import com.example.sportsshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
	Optional<Category> findByNameIgnoreCase(String name);

	List<Category> findAllByOrderByNameAsc();
}
