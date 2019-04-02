package com.owl.tmall.Controller;

import com.owl.tmall.pojo.Category;
import com.owl.tmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.ImageUtil;
import util.Page4Navigator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * RESTFUL
 * 1. 资源名称用复数，而非单数。
 * 即使用 /categories 而不是用 /category
 *
 * 2. CRUD 分别对应：
 * 增加： post
 * 删除： delete
 * 修改： put
 * 查询： get
 *
 * 3. id 参数的传递都用 /id方式。
 * 如编辑和修改：
 * /categories/123
 *
 * 4. 其他参数采用?name=value的形式
 * 如分页参数 /categories?start=5
 *
 * 5. 返回数据
 * 查询多个返回 json 数组
 * 增加，查询一个，修改 都返回当前 json 数组
 * 删除 返回空
 */
@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public  Page4Navigator<Category> list(
            @RequestParam(value = "start",defaultValue="0") int start , // 这里出现了错误 多了一个空格
            @RequestParam(value = "size",defaultValue = "5") int size
    ) throws Exception
    {
        start=start<0?0:start; //判断start是否为非法数字
        Page4Navigator<Category> categoryPage = categoryService.getCategoryPage(start, size, 5);
        return categoryPage;
    }
    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable int id,HttpServletRequest request){
        categoryService.delete(id);
        // 删除保存图片
        //1 找到图片父路径
        File file = new File(request.getServletContext().getRealPath("/img/category")); //从request里面找到基本路径然后进行拼接
        //2 和id拼接成完整的图片路径
        File delFile = new File(file, id + ".jpg");
        //3 删除图片
        delFile.delete();
        return  null;
    }
    @GetMapping("/categories/{id}")
    public Category getById(@PathVariable int id){
        return categoryService.get(id);
    }
    @PutMapping("/categories/{id}")
    public Object update(Category bean,MultipartFile image,HttpServletRequest request) throws  Exception{
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
        if (image!=null){
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.save(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }
    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request)
            throws IOException {
        File imageFolder= new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder,bean.getId()+".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

}
