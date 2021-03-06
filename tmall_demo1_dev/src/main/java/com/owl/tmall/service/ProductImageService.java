package com.owl.tmall.service;

import com.owl.tmall.dao.ProductImageDao;
import com.owl.tmall.pojo.OrderItem;
import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    public static final String type_single = "single";
    public static final String type_detail = "detail";

    @Autowired
    ProductImageDao productImageDAO;
    @Autowired ProductService productService;

    public void add(ProductImage bean) {
        productImageDAO.save(bean);

    }
    public void delete(int id) {
        productImageDAO.delete(id);
    }

    public ProductImage get(int id) {
        return productImageDAO.findOne(id);
    }
    public List<ProductImage> listSingleImage(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_single);
    }
    public List<ProductImage> listDetailImage(Product product){
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product,type_detail);
    }
    public void setFirstProductImage(Product product){
        List<ProductImage> SingleImage = listSingleImage(product);
        if(!SingleImage.isEmpty())
            product.setFirstProductImage(SingleImage.get(0));
        else
            product.setFirstProductImage(new ProductImage()); //这样做是考虑到产品还没有来得及设置图片，但是在订单后台管理里查看订单项的对应产品图片。
    }
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProductImage(product);
    }
    //    增加如下方法
    public void setFirstProductImagesOnOrderItems(List<OrderItem> ois) {
        for (OrderItem orderItem:ois) {
            setFirstProductImage(orderItem.getProduct());
        }
    }


}
