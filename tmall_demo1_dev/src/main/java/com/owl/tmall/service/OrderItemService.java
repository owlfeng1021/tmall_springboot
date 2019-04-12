package com.owl.tmall.service;

import com.owl.tmall.dao.OrderItemDao;
import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.OrderItem;
import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Cacheable(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    private OrderItemDao orderItemDao;
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
//    @Cacheable(key = "'orderItems-oid-'+#p0")
    public List<OrderItem> listByOrder(Order order){
        return orderItemDao.findByOrderOrderByIdDesc(order);
    }

    public int getSaleCount(Product product) {
        List<OrderItem> ois =listByProduct(product);
        int result =0;
        for (OrderItem oi : ois) {
            if(null!=oi.getOrder())
                if(null!= oi.getOrder() && null!=oi.getOrder().getPayDate())
                    result+=oi.getNumber();
        }
        return result;
    }

    public List<OrderItem> listByProduct(Product product) {
        return orderItemDao.findByProduct(product);
    }
//    @Cacheable(key = "'orderItems-uid-'+#p0")
    public List<OrderItem> listByUser(User user){
        return orderItemDao.findByUserAndOrderIsNull(user);
    }
//    @CacheEvict(allEntries = true)
    public void add(OrderItem orderItem){
        orderItemDao.save(orderItem);
    }
//    @CacheEvict(allEntries = true)
    public void update(OrderItem orderItem){
        orderItemDao.save(orderItem);
    }
//    @Cacheable(key = "'orderItems-one-'+#p0")
    public OrderItem get(int id){
        return  orderItemDao.getOne(id);
    }
//    @CacheEvict(allEntries = true)
    public void delete(int id){
        orderItemDao.delete(id);
    }
}
