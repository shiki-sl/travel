package cn.itcast.travel.service;

import cn.itcast.travel.domain.Category;

import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/8 20:56
 */
public interface CategoryService {

    /**
     * 查询数据库tab_category表的全部内容
     *
     * @return 返回查询结果list集合
     */
    List<Category> findAll();
}
