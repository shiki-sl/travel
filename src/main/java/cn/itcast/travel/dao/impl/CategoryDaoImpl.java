package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.util.JdbcUtils;
import cn.itcast.travel.util.JedisUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

/**
 * @Author: shiki
 * @Date: 2019/1/8 21:03
 */
public class CategoryDaoImpl implements CategoryDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
    private CategoryJedisDao categoryJedisDao = new CategoryJedisDao();

    /**
     * 查询redis数据内部类
     */
    private final static class CategoryJedisDao {
        Jedis jedis = JedisUtil.getJedis();

        static {
            new CategoryJedisDao().init();
        }

        CategoryJedisDao() {
        }

        /**
         * 查询mysql tab_category表,将所有用户全部信息存入redis
         */
        @PostConstruct
        void init() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
            List<Category> categoryList = jdbcTemplate.query("select * from tab_category",
                    new BeanPropertyRowMapper<Category>(Category.class));

            if (categoryList != null && categoryList.size() != 0) {
                for (Category category : categoryList) {
                    jedis.zadd("category", category.getCid(), category.getCname());
                }
            }
            jedis.close();
        }

        Set<Tuple> findAll() {
            return jedis.zrangeWithScores("category", 0, -1);
        }
    }

    @Override
    public Set<Tuple> findAll() {
        return categoryJedisDao.findAll();
    }
}
