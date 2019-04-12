package com.owl.tmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com.owl.tmall.es")
@EnableJpaRepositories(basePackages = {"com.owl.tmall.dao", "com.owl.tmall.pojo"})
public class TmallApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmallApplication.class,args);
    }
}
