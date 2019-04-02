package com.owl.tmall.service;

import com.owl.tmall.dao.CategoryDao;
import com.owl.tmall.pojo.Category;
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
}
