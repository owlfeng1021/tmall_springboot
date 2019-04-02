package com.owl.tmall.Controller;

import com.owl.tmall.pojo.Category;
import com.owl.tmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import util.Page4Navigator;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public  Page4Navigator<Category> list(
            @RequestParam(value = "start",defaultValue="0") int start , // 这里出现了错误 多了一个空格
            @RequestParam(value = "size",defaultValue = "5") int size
    ) throws Exception
    {
        start=start<0?0:start; //判断start是否为非法数字
        Page4Navigator<Category> categoryPage = categoryService.getCategoryPage(start, size, 5);
        return categoryPage;
    }
}
