package com.owl.tmall.dao;

import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.ProductImage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductImageDao extends JpaRepository<ProductImage,Integer> {
//    @Modifying
//    @Query(value = "SELECT * from productimage image WHERE pid=?1 AND type=?2 ORDER BY image.id desc ",nativeQuery = true)
    public List<ProductImage> findByProductaAndTypeOrderByIdDesc(Product product,String type);
    //
}
