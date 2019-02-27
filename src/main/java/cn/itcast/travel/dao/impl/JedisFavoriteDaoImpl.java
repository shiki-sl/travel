package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.JedisFavoriteDao;
import cn.itcast.travel.util.JdbcUtils;
import cn.itcast.travel.util.JedisUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: shiki
 * @Date: 2019/1/12 9:16
 */
public class JedisFavoriteDaoImpl implements JedisFavoriteDao {
    private Jedis jedis = JedisUtil.getJedis();
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());

    static {
        new JedisFavoriteDaoImpl().init();
    }

    private void init() {
        List<Map<String, Object>> list = null;
        try {
            String sql = "select rid,count(*)value from tab_favorite group by rid";
            list = jdbcTemplate.queryForList(sql);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() != 0) {
            for (Map<String, Object> map : list) {
                jedis.zadd("favorite", Integer.parseInt(map.get("value").toString()), "" + map.get("rid"));
            }
        }
    }

    @Override
    public void updateFavorite(int rid, int score) {
        jedis.zincrby("favorite", score, "" + rid);
    }

    @Override
    public double queryFavoriteScore(int rid) {
        jedis.zincrby("favorite", 0, "" + rid);
        return jedis.zscore("favorite", "" + rid);
    }

@Override
    public Set<Tuple> rankingList(int start, int end) {
        return jedis.zrevrangeWithScores("favorite", start, end);
    }

}
