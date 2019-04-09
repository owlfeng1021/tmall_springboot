package com.owl.tmall.dao;


import com.owl.tmall.pojo.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderDao extends JpaRepository<Order,Integer> , JpaSpecificationExecutor<Order>{

}
