package com.owl.tmall.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {
    @GetMapping("/")
    public String admin(){
        // 转到
        return "redirect:admin_category_list";
    }
    @GetMapping(value="/admin_category_list")
    public String listCategory(){
        return "admin/listCategory";
    }
    @GetMapping(value="/admin_category_edit")
    public String editCategory(){
        return "admin/editCategory";
    }

}
