package com.owl.tmall.Controller;

import com.owl.tmall.pojo.Category;
import com.owl.tmall.service.CategoryService;
import com.owl.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

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
        categoryService.removeCategoryFromProduct(cs);
        return cs;
    }

}
