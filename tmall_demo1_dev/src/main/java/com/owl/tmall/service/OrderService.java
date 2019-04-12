package com.owl.tmall.service;

import com.owl.tmall.dao.OrderDao;
import com.owl.tmall.pojo.Order;
import com.owl.tmall.pojo.OrderItem;
import com.owl.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import util.Page4Navigator;

import java.util.List;

/**
 * OrderService,提供分页查询。
 * 还提供了 订单状态的常量，Order.java 的 getStatusDesc 会用到。
 * 另外还提供了一个奇怪的方法，removeOrderFromOrderItem，它的作用是把订单里的订单项的订单属性设置为空。。。
 * 听上去绕口吧。 再用代码说一下，比如有个 order, 拿到它的 orderItems， 然后再把这些orderItems的order属性，设置为空。
 * 为什么要做这个事情呢？ 因为SpingMVC ( springboot 里内置的mvc框架是 这个东西)的 RESTFUL 注解，在把一个Order转换为json的同时，会把其对应的 orderItems 转换为 json数组， 而 orderItem对象上有 order属性， 这个order 属性又会被转换为 json对象，然后这个 order 下又有 orderItems 。。。
 * 就这样就会产生无穷递归，系统就会报错了。
 * 所以这里采用 removeOrderFromOrderItem 把 OrderItem的order设置为空就可以了。
 * 那么为什么不用 @JsonIgnoreProperties 来标记这个字段呢？ 因为后续我们要整合Redis，如果标记成了 @JsonIgnoreProperties 会在和 Redis 整合的时候有 Bug, 所以还是采用这种方式比较好。 这些都是站长掉进去的坑~
 */
@Service
//@Cacheable(cacheNames = "orders")
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemService orderItemService;
//    @Cacheable(key = "'orders-page-'+#p0+'-'+#p1")
    public Page4Navigator<Order> list(int start,int size,int navigatePages){
//         sort
        Sort sort = new Sort(Sort.Direction.DESC, "id");
//       pagerequest
        Pageable pageable = new PageRequest(start, size, sort);
//        dao的find
        Page<Order> all = orderDao.findAll(pageable);
//        返回
        return  new Page4Navigator<Order>(all,navigatePages);
    }

    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem oi:orderItems)
        {
            oi.setOrder(null);
        }
    }
    public void removeOrderFromOrderItem(List<Order> orderList) {
        for (Order o: orderList) {
            removeOrderFromOrderItem(o);
        }
    }
//    @CacheEvict(allEntries = true)
    public void add(Order order){
        orderDao.save(order);
    }
//    @Cacheable(key = "'orders-page-'+#p0")
    public Order get(int id){
        return  orderDao.getOne(id);
    }
//    @CacheEvict(allEntries = true)
    public void update(Order order){
         orderDao.save(order);
    }
//    增加 add(Order o, List<OrderItem> ois)方法，该方法通过注解进行事务管理
@Transactional(propagation= Propagation.REQUIRED,rollbackForClassName="Exception")
public float add(Order order, List<OrderItem> ois) {
    float total = 0;
    add(order);

    if(false)
        throw new RuntimeException();

    for (OrderItem oi: ois) {
        oi.setOrder(order);
        orderItemService.update(oi);
        total+=oi.getProduct().getPromotePrice()*oi.getNumber();
    }
    return total;
}
    public List<Order> listByUserWithoutDelete(User user) {
        List<Order> orders = listByUserAndNotDeleted(user);
        orderItemService.fill(orders);
        return orders;
    }
    public List<Order> listByUserAndNotDeleted(User user) {
        return orderDao.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }
    // 用于计算订单总金额  订单的金额 orderitem .getproduct .price * ord
    public void calc(Order o) {
        // 通过 order 找到 orderitem
        List<OrderItem> orderItems = o.getOrderItems();
        float total = 0;
        for (OrderItem orderItem:orderItems) {
            total+=orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
        }
        o.setTotal(total);
    }
}

