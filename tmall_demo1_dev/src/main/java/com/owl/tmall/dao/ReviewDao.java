package com.owl.tmall.dao;

import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDao extends JpaRepository<Review,Integer> {

//    增加 ReviewDAO，并提供两个查询方法，一个返回某产品对应的评价集合，一个返回某产品对应的评价数量
    public List<Review> findByProductOrderByIdDesc(Product product);
    public int countByProduct(Product product);
}

