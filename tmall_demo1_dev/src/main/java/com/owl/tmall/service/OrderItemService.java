package com.owl.tmall.service;

import com.owl.tmall.dao.OrderItemDao;
import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.OrderItem;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderItemService {
    @Autowired
    private OrderItemDao OrderItemDao;
    @Autowired
    private ProductImageService productImageServicel;
    /**
     * OrderItemService，提供对OrderItem的业务操作，其中主要是 fill 方法。
     * 从数据库中取出来的 Order 是没有 OrderItem集合的，这里通过 OrderItemService取出来再放在 Order的 orderItems属性上。
     * 除此之外，还计算订单总数，总金额等等信息。
     */
    public void fill(Order order) {
        List<OrderItem>  orderItemList= listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi:orderItemList )
        {
            total+=oi.getNumber()*oi.getProduct().getPromotePrice();// 这里是计算总价钱 从产品数量 * 商品促销价 promote price
            totalNumber+=oi.getNumber();
            productImageServicel.setFirstProductImage(oi.getProduct());// oi会把product取到所以这里可以把图片set到product中去
        }
        order.setOrderItems(orderItemList);
        order.setTotal(total);
        order.setTotalNumber(totalNumber);
    }
    public void fill(List<Order> orderList){
        for (Order o: orderList )
        {
            this.fill(o);
        }
    }


//    public void fill(Order order) {}
    public List<OrderItem> listByOrder(Order order){
        return OrderItemDao.findByOrderOrderByIdDesc(order);
    }

}
