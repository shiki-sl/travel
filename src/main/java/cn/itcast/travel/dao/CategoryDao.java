package cn.itcast.travel.dao;

import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * @Author: shiki
 * @Date: 2019/1/8 21:03
 */
public interface CategoryDao {
    /**
     * 查询数据库tab_category表的全部内容
     *
     * @return 返回查询结果Set集合
     */
    Set<Tuple> findAll();
}
