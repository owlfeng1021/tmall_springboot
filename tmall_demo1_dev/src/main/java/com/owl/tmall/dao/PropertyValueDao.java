package com.owl.tmall.dao;

import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.Property;
import com.owl.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDao extends JpaRepository<PropertyValue,Integer> {

    //DAO类，除了继承JpaRepository 还提供根据产品查询：
    List<PropertyValue> findByProductOrderByIdDesc(Product product);
    //    和根据产品和属性获取PropertyValue对象
    PropertyValue findByProductAndProperty(Product product, Property property);



}
