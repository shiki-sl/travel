package cn.itcast.travel.service;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;

import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 12:52
 */
public interface RouteService {
    /**
     * 根据用户选定页面的id查询数据库
     *
     * @param cid
     * @param rname
     * @param currentPageString
     * @param pageSizeString
     * @return
     */
    PageBean findQuery(String cid, String rname, String currentPageString,String pageSizeString);

    /**
     * 根据rid 查询旅游线路详细信息
     *
     * @param rid
     * @param uid
     * @return
     */
    List<Object> findOne(String rid, int uid);

    /**
     * 更改收藏
     *
     * @param ridString
     * @param uid
     * @param flag      "true"添加,"false"删除
     * @return
     */
    double updateFavorite(String ridString, int uid, String flag);

    /**
     * 分页查询收藏排行榜
     * @param rname 搜索字符
     * @param minString 金币
     * @param maxString 金币
     * @param currentPageString 当前页
     * @param pageSizeString 每页最大数量
     * @return
     */
    List<Route> rankingList(String rname, String minString, String maxString, String currentPageString,String pageSizeString);

    /**
     * 按最新时间排序,找出四条旅行线路
     * @return
     */
    List<Route> newTour();

    /**
     * 主题旅游
     * @return
     */
    List<Route> theme();

    /**
     * 查询收藏人数
     * @param ridString
     * @return
     */
    double queryFavoriteScore(String ridString);

    /**
     * 像前端返回数据库总条数
     * @return
     */
    int totalCount();
}
