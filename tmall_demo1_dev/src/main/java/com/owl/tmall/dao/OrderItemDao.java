package com.owl.tmall.dao;

import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.OrderItem;
import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDao extends JpaRepository<OrderItem,Integer> {
//    另外还提供过了通过订单查询的方法 这里使用了orderby的方法
    public List<OrderItem> findByOrderOrderByIdDesc(Order order);
//  修改OrderItemService，增加根据产品获取OrderItem的方法：
    public List<OrderItem> findByProduct(Product product);
//
    public List<OrderItem> findByUserAndOrderIsNull(User user);
}

