package com.owl.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "category")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" }) // handler 和 hibernateLazyInitializer
// JsonIgnoreProperties 可以忽略字段 让他们不去序列化，反序列化 也可以忽略未知字段
// implements Serializable 因为jpa的工作机制 是代理类来代理 所以不需要加 Serializable
public class Category  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//strategy 战略  identity 身份
    @Column(name = "id")
    int id;
    String name;
    @Transient
    List<Product> products;
    @Transient
    List<List<Product>> productsByRow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }

}
