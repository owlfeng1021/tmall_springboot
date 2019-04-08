package com.owl.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "propertyvalue")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"}) //在此标记不生成json对象的属性
public class PropertyValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "pid")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "ptid")
    private Property property;

    private String value;

    public PropertyValue(Product product, Property property, String value) {
        this.product = product;
        this.property = property;
        this.value = value;
    }

    public PropertyValue() {
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

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ProductValue{" +
                "id=" + id +
                ", product=" + product +
                ", property=" + property +
                ", value='" + value + '\'' +
                '}';
    }
}
