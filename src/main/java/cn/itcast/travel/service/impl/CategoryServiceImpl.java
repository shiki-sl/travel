package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: shiki
 * @Date: 2019/1/8 20:59
 */
public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao = new CategoryDaoImpl();

    @Override
    public List<Category> findAll() {
        Set<Tuple> all = categoryDao.findAll();
        if (all != null && all.size() != 0) {
            List<Category> list = new ArrayList<Category>(all.size());
            for (Tuple tuple : all) {
                list.add(new Category((int) tuple.getScore(), tuple.getElement()));
            }
            return list;
        } else {
            return null;
        }
    }
}
