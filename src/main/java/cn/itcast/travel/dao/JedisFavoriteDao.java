package cn.itcast.travel.dao;

import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * @Author: shiki
 * @Date: 2019/1/12 9:55
 */
public interface JedisFavoriteDao {
    /**
     * 更改收藏数量
     *
     * @param score 原有收藏权重+score;score为正增加,为负减少
     * @param rid   以商品的rid做为key
     */
    void updateFavorite(int rid, int score);

    /**
     * 查询收藏人数
     *
     * @param rid
     * @return
     */
    double queryFavoriteScore(int rid);

    /**
     * 基于jedis zrevrangeWithScores(key,start,end)实现
     *
     * @param start
     * @param end
     * @return 返回商家的id(收藏数量降序排列)
     */
    Set<Tuple> rankingList(int start, int end);


}
