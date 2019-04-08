package com.owl.tmall.service;

import com.owl.tmall.dao.PropertyValueDao;
import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.Property;
import com.owl.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueService {
    @Autowired
    private PropertyValueDao propertyValueDao;
    @Autowired
    private PropertyService propertyService;
    public void update(PropertyValue bean) {
        propertyValueDao.save(bean);
    }
    public void init(Product product) {
        // 从产品里面 找出 categoryid
        List<Property> byCategory = propertyService.findByCategory(product.getCategory());
        // 找到所有的property 然后把所有的 value都初始化一遍都为空
        for (Property property: byCategory) {
            // 先找一下之前没有声明过的value 如果为空就把基本的 product和property属性放到 数据库里面去
            PropertyValue propertyValue = getPropertyValue(product, property);
            if (null==propertyValue){
                PropertyValue temp = new PropertyValue();
                temp.setProduct(product);
                temp.setProperty(property);
                propertyValueDao.save(temp);
            }

        }
    }
    //DAO类，除了继承JpaRepository 还提供根据产品查询：
    public List<PropertyValue> list(Product product){
        return propertyValueDao.findByProductOrderByIdDesc(product);
    }
    //    和根据产品和属性获取PropertyValue对象
    public PropertyValue getPropertyValue(Product product, Property property){
        return  propertyValueDao.findByProductAndProperty(product,property);
    }
}
