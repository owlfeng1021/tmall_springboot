package com.owl.tmall.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class) //异常控制器
    public String defaultErrorHandler(HttpServletRequest request,Exception e) throws Exception{
        e.printStackTrace();
        Class constraintViolationException = Class.forName("org.hibernate.exception.ConstraintViolationException");
        if(null!=e.getCause()  && constraintViolationException==e.getCause().getClass()) { //对比 如果是这个错误就是 外键约束的问题
            return "违反了约束，多半是外键约束";
        }
        return e.getMessage();
    }
}
