package com.owl.tmall.es;

import com.owl.tmall.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductEsDao extends ElasticsearchRepository<Product,Integer> {
}
