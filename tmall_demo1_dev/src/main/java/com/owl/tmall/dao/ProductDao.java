package com.owl.tmall.dao;

import com.owl.tmall.pojo.Category;
import com.owl.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDao extends JpaRepository<Product,Integer> {
    Page<Product> findByCategory(Category category, Pageable pageable);
}
