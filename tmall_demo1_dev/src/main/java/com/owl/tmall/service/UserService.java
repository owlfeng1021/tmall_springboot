package com.owl.tmall.service;

import com.owl.tmall.dao.UserDao;
import com.owl.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import util.Page4Navigator;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public Page4Navigator<User> list(int start ,int size ,int navigatePage){
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start ,size,sort);
        Page<User> all = userDao.findAll(pageable);
        return new Page4Navigator<User>(all,navigatePage);
    }
}
