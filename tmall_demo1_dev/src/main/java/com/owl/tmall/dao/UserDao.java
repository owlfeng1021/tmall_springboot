package com.owl.tmall.dao;

import com.owl.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Integer> {
        public User findByName(String name);
        public User findByNameAndPassword(String name,String Password);

}
