package com.owl.tmall.Controller;

import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.PropertyValue;
import com.owl.tmall.service.ProductService;
import com.owl.tmall.service.PropertyService;
import com.owl.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ProductService productService;
     @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable("pid") int pid) throws Exception {
         Product product = productService.get(pid);
         propertyValueService.init(product);
         List<PropertyValue> list = propertyValueService.list(product);

         return list;

     }
    @PutMapping("/propertyValues")
    public Object update(@RequestBody PropertyValue bean) throws Exception {
         propertyValueService.update(bean);
         return bean;
    }

}
