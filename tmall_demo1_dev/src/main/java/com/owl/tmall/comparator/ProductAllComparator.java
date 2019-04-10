package com.owl.tmall.comparator;

import com.owl.tmall.pojo.Product;

import java.util.Comparator;

public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getReviewCount()*o2.getSaleCount()-o1.getReviewCount()*o1.getSaleCount();
    }
}
