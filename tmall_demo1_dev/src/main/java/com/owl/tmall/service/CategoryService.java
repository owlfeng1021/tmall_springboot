package com.owl.tmall.service;

import com.owl.tmall.dao.CategoryDao;
import com.owl.tmall.pojo.Category;
import com.owl.tmall.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import util.Page4Navigator;

import java.util.List;

@Service
//@CacheConfig(cacheNames = "categories")
public class CategoryService {
    @Autowired
    public CategoryDao categoryDao;

//    @Cacheable(key="'categories-all'")
    public List<Category> getCategory() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDao.findAll(sort);
    }

//    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> getCategoryPage(int start, int size, int navigatePage) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort); // 2.0 使用PageRequest.of
        Page pageFromJpa = categoryDao.findAll(pageable);
        return new Page4Navigator<>(pageFromJpa, navigatePage); // 放到了pojo方法里面生成

    }

//    @CacheEvict(allEntries = true)
    public void save(Category category) {
        categoryDao.save(category);
    }

//    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDao.delete(id);
    }

//    @Cacheable(key="'categories-one-'+ #p0") // 这个p是param 0是第0个参数的意思
    public Category get(int id) {
        return categoryDao.getOne(id);
    }

//    @CacheEvict(allEntries = true)
    public void update(Category category) {
        categoryDao.save(category);
    }

    public void removeCategoryFromProduct(List<Category> categoryList) {
        for (Category category : categoryList) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        for (Product p : products) {
            if (null != p) {
                p.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> productList : productsByRow) {
                for (Product product : productList) {
                    product.setCategory(null);
                }
            }
        }

    }
}

