package com.owl.tmall.Controller;

import com.owl.tmall.comparator.*;
import com.owl.tmall.pojo.*;
import com.owl.tmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;
import util.Result;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        user.setPassword(password);

        userService.add(user);

        return Result.success();
    }
    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name =  userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user =userService.get(name,userParam.getPassword());
        if(null==user){
            String message ="账号密码错误";
            return Result.fail(message);
        }
        else{
            session.setAttribute("user", user); // 这里是使用cooking 来进行验证 这个只是最普通的验证方式
            return Result.success();
        }
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
        if(null!=user){
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
}
