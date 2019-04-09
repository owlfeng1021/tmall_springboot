package com.owl.tmall.service;

import com.owl.tmall.dao.CategoryDao;
import com.owl.tmall.pojo.Category;
import com.owl.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import util.Page4Navigator;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    public CategoryDao categoryDao;
    public List<Category> getCategory(){
        //
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDao.findAll(sort);
    }
    public Page4Navigator<Category> getCategoryPage(int start,int size ,int navigatePage){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort); // 2.0 使用PageRequest.of
        Page pageFromJpa= categoryDao.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa,navigatePage); // 放到了pojo方法里面生成

    }
    public void save(Category category){
        categoryDao.save(category);
    }
    public  void  delete(int id){
        categoryDao.delete(id);
    }
    public Category get(int id)
    {
    return categoryDao.getOne(id);
    }
    public void update(Category category){
        categoryDao.save(category);
    }
    public void  removeCategoryFromProduct(List<Category> categoryList)
    {
        for (Category category: categoryList)
        {
            removeCategoryFromProduct(category);
        }
    }
    public void  removeCategoryFromProduct(Category category){
        List<Product> products = category.getProducts();
        for (Product p:products) {
            if (null !=p)
            {
                p.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null!=productsByRow){
            for (List<Product> productList:  productsByRow) {
                for (Product product: productList) {
                    product.setCategory(null);
                }
            }
        }

    }
}

