package com.owl.tmall.service;

import com.owl.tmall.dao.ProductDao;
import com.owl.tmall.es.ProductEsDao;
import com.owl.tmall.pojo.Category;
import com.owl.tmall.pojo.Product;
import com.owl.tmall.pojo.Review;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import util.Page4Navigator;
import util.SpringContextUtil;

import java.util.ArrayList;
import java.util.List;

@Service
//@Cacheable(cacheNames = "products")
public class ProductService  {
    @Autowired
    ProductDao productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ProductEsDao productEsDao;

//    @CacheEvict(allEntries = true)
    public void add(Product bean) {
        productDAO.save(bean);
        productEsDao.save(bean);
    }
//    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.delete(id);
        productEsDao.delete(id);
    }
//    @Cacheable(key = "'products-one-'+#p0" )
    public Product get(int id) {
        return productDAO.findOne(id);
    }
//    @CacheEvict(allEntries = true)
    public void update(Product bean) {
        productDAO.save(bean);
        productEsDao.save(bean);
    }
//    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort=new Sort(Sort.Direction.DESC,"id");
        Pageable pageable =new PageRequest(start,size,sort);
        Page<Product> byCategory = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<Product>(byCategory,navigatePages);
    }

    // 1. 为分类填充产品集合
    public void fill(Category category){
//       因为这里要使用redis缓存 所以要使用
//
//        ProductService productService = SpringContextUtil.getBean(ProductService.class);
//        List<Product> productList = productService.listByCategory(category);
//        productImageService.setFirstProdutImages(productList);
//        category.setProducts(productList);

        List<Product> byCategoryOrderById = productDAO.findByCategoryOrderById(category);
        productImageService.setFirstProdutImages(byCategoryOrderById);
        category.setProducts(byCategoryOrderById);
    }
    //2. 为多个分类填充产品集合
    public void fill(List<Category> categorys){
        for (Category category:categorys) {
            fill(category);
        }
    }
    //3.为多个分类填充推荐产品集合，即把分类下的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
    public void fillByRow(List<Category> categorys){
//        int ProductNumberEachRow=8;
//        for (Category category:categorys)
//        {
//            List<Product> productList = category.getProducts();
//            List<List<Product>> subProductList = new ArrayList<>();
//            for (int i=0 ;i<productList.size();i+=ProductNumberEachRow) {
//                int size=i+=ProductNumberEachRow;
//                size=size>productList.size()?productList.size():size;
//                List<Product> sliceProductList = productList.subList(i,size);
//                subProductList.add(sliceProductList);
//            }
//            category.setProductsByRow(subProductList);
//        }
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }
    //4. 查询某个分类下的所有产品
    @Cacheable(key = "'products-cid-'+#p0")
    public List<Product> listByCategory(Category category){
       return  productDAO.findByCategoryOrderById(category);
    }
    // 设置产品设置销量和评价数量的方法：
    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);//销量
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);// 评论的数量
        product.setReviewCount(reviewCount);

    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }




    /**
     * //        Sort sort = new Sort(Sort.Direction.DESC, "id");
     * //        Pageable pageable = new PageRequest(start, size, sort);
     * //        List<Product> byNameLike = productDAO.findByNameLike("%"+keyword+"%", pageable);
     * //        return byNameLike;
     * @param keyword
     * @param start
     * @param size
     * @return
     */
    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", keyword),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .scoreMode("sum")
                .setMinScore(10);
        Sort sort  = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start, size,sort);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();

        Page<Product> page = productEsDao.search(searchQuery);
        return page.getContent();
    }

    private void initDatabase2ES() {
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page =productEsDao.findAll(pageable);
        if(page.getContent().isEmpty()) {
            List<Product> products= productDAO.findAll();
            for (Product product : products) {
                productEsDao.save(product);
            }
        }
    }
}
