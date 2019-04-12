package com.owl.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "orderitem")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// identty 一致
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "oid")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    private int number;

    public OrderItem() {
    }

    public OrderItem(Product product, Order order, User user, int number) {
        this.product = product;
        this.order = order;
        this.user = user;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", product=" + product +
                ", order=" + order +
                ", user=" + user +
                ", number=" + number +
                '}';
    }
}
