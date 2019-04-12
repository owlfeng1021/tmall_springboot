package com.owl.tmall.config;

import com.owl.tmall.interception.IoginInterception;
import com.owl.tmall.interception.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfiguraion extends WebMvcConfigurerAdapter {

    @Bean
    public IoginInterception getLoginIntercepter() {
        return new IoginInterception();
    }
    @Bean
    public OtherInterceptor getOtherInterceptor(){
        return  new OtherInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginIntercepter())
                .addPathPatterns("/**");
        registry.addInterceptor(getOtherInterceptor())
                .addPathPatterns("/**");

    }



}
