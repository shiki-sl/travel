package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.JedisFavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.impl.JedisFavoriteDaoImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.domain.*;
import cn.itcast.travel.service.RouteService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 13:29
 */
public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = new RouteDaoImpl();
    private JedisFavoriteDao jedisFavoriteDao = new JedisFavoriteDaoImpl();

    @Override
    public PageBean findQuery(String cidString, String rname, String currentPageString, String pageSizeString) {
        PageBean pageBean = new PageBean();
        //当currentPage中有数据时,使用传递的数据,如果没有使用默认值

        if (!("".equals(currentPageString) || isNull(currentPageString))) {
            int currentPage = Integer.parseInt(currentPageString);
            pageBean.setCurrentPage(currentPage);
        }
        if (!("".equals(pageSizeString) || isNull(pageSizeString))) {
            int pageSize = Integer.parseInt(pageSizeString);
            pageBean.setPageSize(pageSize);
        }

        //cidString无值为0,有值解析
        int cid = 0;
        if (!("".equals(cidString) || isNull(cidString))) {
            cid = Integer.parseInt(cidString);
        }

        //rname无值置空
        if ("".equals(rname) || isNull(rname)) {
            rname = "";
        }

        findCount(pageBean, cid, rname);

        int currentPage = pageBean.getCurrentPage() - 1 < 0 ? 0 : pageBean.getCurrentPage() - 1;
        pageBean.setCurrentPage(currentPage);
        List<Route> list = routeDao.findQuery(cid, pageBean, rname);
        pageBean.setList(list);
        return pageBean;
    }

    /**
     * 辅助查询分页 对findQuery提供countPage和totalPage属性
     *
     * @param pageBean findQuery中要复制的对象
     * @param cid
     * @param rname
     */
    private void findCount(PageBean pageBean, int cid, String rname) {
        int totalCount = routeDao.findCount(cid, rname);
        pageBean.setTotalCount(totalCount);

        int pageSize = pageBean.getPageSize();
        int totalPage = totalCount / pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize) + 1;
        pageBean.setTotalPage(totalPage);
    }

    @Override
    public List<Object> findOne(String ridString, int uid) {
        String mark = "true";
        if (uid == -1) {
            mark = "false";
        }
        if (isNull(ridString)) {
            ridString = "";
        }
        int rid = Integer.parseInt(ridString);

        Route route = routeDao.findRouteOne(rid);
        List<RouteImg> routeImgList = routeDao.routeImgList(route.getRid());
        Seller seller = routeDao.routeSeller(route.getSid());
        Category category = routeDao.routeCategory(route.getCid());

        route.setRouteImgList(routeImgList);
        route.setSeller(seller);
        route.setCategory(category);

        String flag = "true";
        String favoriteDate = routeDao.favoriteDate(rid, uid);
        if (favoriteDate != null) {
            flag = "false";
        }

        List<Object> list = new ArrayList<Object>(2);
        list.add(route);
        list.add(flag);
        list.add(mark);
        return list;
    }

    @Override
    public double updateFavorite(String ridString, int uid, String flag) {
        if ("".equals(ridString) || isNull(ridString)) {
            return 0;
        }
        if ("".equals(flag) || isNull(flag)) {
            return 0;
        }
        String isStringFalse = "false";
        int score = 1;
        if (isStringFalse.equals(flag)) {
            score = -1;
        }

        jedisFavoriteDao.updateFavorite(Integer.parseInt(ridString), score);
        if (score > 0) {
            routeDao.addFavorite(Integer.parseInt(ridString), uid);
        } else {
            routeDao.delFavorite(Integer.parseInt(ridString), uid);
        }
        return jedisFavoriteDao.queryFavoriteScore(Integer.parseInt(ridString));
    }

    @Override
    public double queryFavoriteScore(String ridString) {
        if ("".equals(ridString) || isNull(ridString)) {
            return 0;
        }
        return jedisFavoriteDao.queryFavoriteScore(Integer.parseInt(ridString));
    }

    @Override
    public int totalCount() {
        return routeDao.totalCount();
    }

    @Override
    public List<Route> rankingList(String rname, String minString, String maxString, String currentPageString, String pageSizeString) {
        if (isNull(rname)) {
            rname = "";
        }

        double min = 0;
        if (!(isNull(minString) || "".equals(minString))) {
            min = Double.parseDouble(minString);
        }
        double max = Double.MAX_VALUE;
        if (!(isNull(maxString) || "".equals(maxString))) {
            max = Double.parseDouble(maxString);
        }

        int currentPage = new PageBean().getCurrentPage();
        if (!(isNull(currentPageString) || "".equals(currentPageString))) {
            currentPage = Integer.parseInt(currentPageString);
        }

        int pageSize = new PageBean().getPageSize();
        if (!(isNull(pageSizeString) || "".equals(pageSizeString))) {
            pageSize = Integer.parseInt(pageSizeString);
        }

        return routeDao.rankingList(rname, min, max, currentPage, pageSize);
    }

    @Override
    public List<Route> newTour() {
        return routeDao.newTour();
    }

    @Override
    public List<Route> theme() {
        return routeDao.theme();
    }

    /**
     * 非空和非"null"校检
     * 为null或"null"返回ture;
     *
     * @param str
     * @return
     */
    private boolean isNull(String str) {
        return str == null || "null".equals(str);
    }
}
