package com.owl.tmall.dao;

import com.owl.tmall.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDao extends JpaRepository<Category, Integer> {
    // 集成jpa基本库
}
