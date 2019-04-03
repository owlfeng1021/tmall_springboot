package com.owl.tmall.dao;

import com.owl.tmall.pojo.Category;
import com.owl.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDao extends JpaRepository<Property,Integer> {
// 声明一个使用categories 查询
    Page<Property> findByCategory(Category category, Pageable pageable);

}
