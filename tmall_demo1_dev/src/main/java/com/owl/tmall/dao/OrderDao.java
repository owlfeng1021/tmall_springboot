package com.owl.tmall.dao;


import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

//SpecificationExecutor
public interface OrderDao extends JpaRepository<Order,Integer> , JpaSpecificationExecutor<Order>{
    // select * from order_ where user = ? and status not delete order by id desc
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);

}
