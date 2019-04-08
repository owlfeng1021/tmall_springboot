package com.owl.tmall.Controller;

import com.owl.tmall.pojo.User;
import com.owl.tmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import util.Page4Navigator;

@RestController
public class UserContorller {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Page4Navigator<User> list(

    @RequestParam(name = "start" ,defaultValue ="0" )int start,
    @RequestParam(name = "size" ,defaultValue = "5")int size)
            throws Exception
    {
       start=start<0?0:start;
       return userService.list(start,size,5);
    }
}
