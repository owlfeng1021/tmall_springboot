package com.owl.tmall.dao;

import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDao extends JpaRepository<OrderItem,Integer> {
//    另外还提供过了通过订单查询的方法 这里使用了orderby的方法
    public List<OrderItem> findByOrderOrderByIdDesc(Order order);

}

