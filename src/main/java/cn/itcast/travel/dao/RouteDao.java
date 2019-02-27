package cn.itcast.travel.dao;

import cn.itcast.travel.domain.*;

import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 10:22
 */
public interface RouteDao {
    /**
     * 查询用户选定列表id的内容
     *
     * @param cid
     * @param pageBean
     * @param rname    模糊检索
     * @return
     */
    List<Route> findQuery(int cid, PageBean pageBean, String rname);

    /**
     * 分页查询
     *
     * @param cid   前端未传递cid值为零
     * @param rname 前端未传递rname值为空字符串"";
     * @return 分页的包装对象
     */
    int findCount(int cid, String rname);

    /**
     * 根据rid 查询旅游线路详细信息
     *
     * @param rid
     * @return
     */
    Route findRouteOne(int rid);

    /**
     * 根据rid查询当前线路的图片信息
     *
     * @param rid
     * @return 图片信息封装为list集合
     */
    List<RouteImg> routeImgList(int rid);

    /**
     * 根据sid查询商家信息
     *
     * @param sid
     * @return 返回商家信息封装类
     */
    Seller routeSeller(int sid);

    /**
     * 返回旅游路线实体类
     *
     * @param cid
     * @return
     */
    Category routeCategory(int cid);

    /**
     * 根据商品id和用户id查询用户收藏日期
     *
     * @param rid
     * @param uid
     * @return 返回date
     */
    String favoriteDate(int rid, int uid);

    /**
     * 根据uid查询用户详细信息
     *
     * @param uid
     * @return 封装好的user对象
     */
    User favoriteUser(int uid);

    /**
     * 根据uid查询单个用户全部的收藏
     *
     * @param uid
     * @return
     */
    List<Favorite> queryUserFavorite(int uid);

    /**
     * 添加一个指定收藏
     *
     * @param rid
     * @param uid
     */
    void addFavorite(int rid, int uid);

    /**
     * 删除指定收藏
     *
     * @param rid
     * @param uid
     */
    void delFavorite(int rid, int uid);

// FIXME: 2019/1/12 使用jedis处理收藏排行榜
///    @Deprecated()
    /**
     * 基于jedis zrevrangeWithScores(key,start,end)实现
     *
     * @param start
     * @param end
     * @return
     */
    List<Route> rankingList(int start, int end);

    /**
     * 分页搜索排行榜
     *
     * @param rname
     * @param min
     * @param max
     * @param currentPage
     * @param pageSize
     * @return
     */
    List<Route> rankingList(String rname, double min, double max, int currentPage, int pageSize);

    /**
     * 搜索上线时间最近的4条旅行线路
     * @return
     */
    List<Route> newTour();

    /**
     * 主题旅游
     * @return
     */
    List<Route> theme();

    /**
     * 查询数据库中全部的商家信息
     * @return
     */
    int totalCount();
}



