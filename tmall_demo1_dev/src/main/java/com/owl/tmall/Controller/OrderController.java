package com.owl.tmall.Controller;

import com.owl.tmall.pojo.Order;
import com.owl.tmall.service.OrderItemService;
import com.owl.tmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import util.Page4Navigator;
import util.Result;

import java.io.IOException;
import java.util.Date;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;
    @GetMapping("/orders")
    public Page4Navigator<Order> list(
            @RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception
    {
        start=start<0?0:start;
        Page4Navigator<Order> list = orderService.list(start, size, 5);
        // 这里做了一些操作
        orderItemService.fill(list.getContent());
        orderService.removeOrderFromOrderItem(list.getContent());
        return  list;
    }
    @PutMapping("deliveryOrder/{oid}")
    /**
     *  修改状态
     */
    public Object deliveryOrder(@PathVariable int oid) throws IOException {
        Order order = orderService.get(oid);
        order.setDeliveryDate(new Date());
        order.setStatus(OrderService.waitConfirm);
        orderService.update(order);
        return Result.success();
    }

}
