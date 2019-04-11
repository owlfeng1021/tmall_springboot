package com.owl.tmall.Controller;

import com.owl.tmall.comparator.*;
import com.owl.tmall.pojo.*;
import com.owl.tmall.service.*;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import util.Result;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;


    @GetMapping("/forehome")
    public Object home() {
//        List<Category> category = categoryService.getCategory();
//        productService.fill(category);
//        productService.fillByRow(category);
//        // 把category内部的product中的category变成null
//        categoryService.removeCategoryFromProduct(category);
//        return category;
        List<Category> cs= categoryService.getCategory();
        productService.fill(cs);
        productService.fillByRow(cs);
//      测试那个注解 JsonIgnoreProperties()
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name =  user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        boolean exist = userService.isExist(name);

        if(exist){
            String message ="用户名已经被使用,不能使用";
            return Result.fail(message);
        }
//      shiro 内置的
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        userService.add(user);

        return Result.success();
//        String name =  user.getName();
//        String password = user.getPassword();
//        name = HtmlUtils.htmlEscape(name);
//        user.setName(name);
//        boolean exist = userService.isExist(name);
//
//        if(exist){
//            String message ="用户名已经被使用,不能使用";
//            return Result.fail(message);
//        }
//
//        user.setPassword(password);
//
//        userService.add(user);
//
//        return Result.success();
    }
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {

        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());
        try {
            subject.login(token);
            User byName = userService.getByName(name);
            session.setAttribute("user",byName);
            return Result.success();
        }catch (AuthenticationException e){
            String message ="账号密码错误";
            return Result.fail(message);
        }
//        String name =  userParam.getName();
//        name = HtmlUtils.htmlEscape(name);
//
//        User user =userService.get(name,userParam.getPassword());
//        if(null==user){
//            String message ="账号密码错误";
//            return Result.fail(message);
//        }
//        else{
//            session.setAttribute("user", user); // 这里是使用cooking 来进行验证 这个只是最普通的验证方式
//            return Result.success();
//        }
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);
        // 获得单张图片和多张图片
        List<ProductImage> productSingleImages = productImageService.listSingleImage(product);
        List<ProductImage> productDetailImages = productImageService.listDetailImage(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);
        // 这是产品属性
        List<PropertyValue> pvs = propertyValueService.list(product);
        // 这是评论
        List<Review> reviews = reviewService.list(product);
        // 下面两个分别设置价格和库存数
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String,Object> map= new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    @GetMapping("forecheckLogin")
    public Object checkLogin( HttpSession session) {
        User user = (User) session.getAttribute("user");
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            return  Result.success();
        }
        return Result.fail("未登录");
    }

    /**
     *  就是使用comparator 来进行排序 自定义规则然后排序
     * @param cid
     * @param sort
     * @return
     */
    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid,String sort){
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        if (null!=sort)
        {
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;
                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;
                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;
                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        return category;
    }
    // 分类的搜索界面
    @PostMapping("foresearch")
    public Object search(String keyword,
                         @RequestParam(value = "start",defaultValue = "0")int start,
                         @RequestParam(value = "size",defaultValue = "20")int size){
        if (null==keyword)
        {
            keyword="";
        }
//      查找操作
        List<Product> ps=productService.search(keyword,start,size);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return  ps;
    }
    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session){
       return  buyoneAndAddCart(pid,num,session);
    }

    /**
     * 接下来就是新增订单项OrderItem， 新增订单项要考虑两个情况
     * a. 如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
     * a.1 基于用户对象user，查询没有生成订单的订单项集合
     * a.2 遍历这个集合
     * a.3 如果产品是一样的话，就进行数量追加
     * a.4 获取这个订单项的 id
     *
     * b. 如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
     * b.1 生成新的订单项
     * b.2 设置数量，用户和产品
     * b.3 插入到数据库
     * b.4 获取这个订单项的 id
     *
     * 5.返回当前订单项id
     * @param pid
     * @param num
     * @param session
     * @return
     */
    private Object buyoneAndAddCart(int pid, int num, HttpSession session){
        Product product = productService.get(pid);
        int oiid = 0;

        User user =(User)  session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            //先按照user查出orderitem 进行对比是添加的重复product 如果是就把他们的数量相加  如果没有就执行下面的代码
            if(oi.getProduct().getId()==product.getId()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return oiid;
    }
    // 购买 且把相同类型的商品重合
    @GetMapping("forebuy")
    public Object foreBuy(String[] oiid,HttpSession session){
        List<OrderItem> orderItemList=new ArrayList<>();
        float total=0;
        // 先把产品的总数相加
        for (String temp:oiid){
            int id=Integer.parseInt(temp);
            OrderItem oi = orderItemService.get(id);
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
            orderItemList.add(oi);
        }
        productImageService.setFirstProductImagesOnOrderItems(orderItemList);

        session.setAttribute("ois", orderItemList);

        Map<String,Object> map = new HashMap<>();
        map.put("orderItems", orderItemList);
        map.put("total", total);
        return Result.success(map);
    }
    // 添加购物车 **
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid,num,session);
        return Result.success();
    }
    // 进入购物车 **
    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProductImagesOnOrderItems(ois);
        return ois;
    }
    //修改订单 **
    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId()==pid){
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }
    // 删除订单 **
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session,int oiid){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }
    // 提交创建订单 *
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order,HttpSession session){
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois= (List<OrderItem>)  session.getAttribute("ois");

        float total =orderService.add(order,ois);

        Map<String,Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }
    // 支付完成
    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }
    // 订单页
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user =(User)  session.getAttribute("user");
        if(null==user)
            return Result.fail("未登录");
        List<Order> os= orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }
    // 确定收货
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid){
//        Order order = orderService.get(oid);
//        orderItemService.fill(order);
//        orderService.calc(order);
//        // 因为order和orderItem 没有写对应的关系 所以就手动删除 orderItem 对order的关系
//        orderService.removeOrderFromOrderItem(order);
//        return order;
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.calc(o);
        orderService.removeOrderFromOrderItem(o);
        return o;
    }
    // 确定收货 成功
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        Order o = orderService.get(oid);
        // 修改属性 变为 waitReview (等待评论的状态)
        o.setStatus(OrderService.waitReview);
        // 增加提交时间
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }

    // 删除order
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    // 评论页面
    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p);
        productService.setSaleAndReviewNumber(p);
        Map<String,Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }
    // 提交评论
    @PostMapping("foredoreview")
    public Object doreview( HttpSession session,int oid,int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }
}
