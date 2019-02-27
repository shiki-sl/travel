package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.UserService;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/5 13:09
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();
    private RouteService routeService = new RouteServiceImpl();
    /**
     * 值为"null"的字符串,在前端只发送的变量名没有发送数据时出现;
     */
    private String isStringNull = "null";

    @Override
    public boolean checkNewUsername(String newUsername) {
        return userDao.checkNewUsername(newUsername);
    }

    @Override
    public boolean addUser(User loginUser) {
        //用户如不输入生日,则默认为null
        if ("".equals(loginUser.getBirthday())) {
            loginUser.setBirthday(null);
        }
        return userDao.addUser(loginUser);
    }

    @Override
    public User loginCheck(String username, String password) {
        return userDao.loginCheck(username, password);
    }

    @Override
    public ResultInfo activate(String code) {
        return userDao.activate(code);
    }

    /**
     * 查询单个用户收藏商品的封装类集合
     *
     * @param uidString
     * @return
     */
    @Override
    public List<Object> query4UserFavorite(String uidString, String currentPageString, String pageSizeString, String rname) throws UnsupportedEncodingException {
        if (uidString == null || "".equals(uidString) || isStringNull.equals(uidString)) {
            return null;
        }
        int uid = Integer.parseInt(uidString);

        PageBean pageBean = new PageBean();
        if (!(currentPageString == null || "".equals(currentPageString) || isStringNull.equals(currentPageString))) {
            pageBean.setCurrentPage(Integer.parseInt(currentPageString));
        }
        if (!(pageSizeString == null || "".equals(pageSizeString) || isStringNull.equals(pageSizeString))) {
            pageBean.setPageSize(Integer.parseInt(pageSizeString));
        }
        if (rname == null || isStringNull.equals(rname)) {
            rname = "";
        }
        rname = new String(rname.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        Integer totalCount = userDao.totalCount(uid, rname);
        if (totalCount != null) {
            pageBean.setTotalCount(totalCount);

            int pageSize = pageBean.getPageSize();
            int totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : (totalCount / pageSize) + 1;
            pageBean.setTotalPage(totalPage);
        }
        //获取用户收藏商品封装的集合,并对其按商品Rid进行自然排序
        List<Route> routes = userDao.queryRoute(uid, pageBean, rname);
//        routes.sort(Comparator.comparingInt(Route::getRid));

        //获取用户收藏商品时间的集合,并对其按商品Rid进行自然排序
        /// FIXME: 2019/1/11   查询用户收藏商品时间   2019/1/12前
        /// List<FavoriteBase> date = userDao.queryFavoriteDate(uid, pageBean, rname);
        ///        date.sort(Comparator.comparingInt(FavoriteBase::getRid));

        ArrayList<Object> list = new ArrayList<Object>(2);
        list.add(routes);
        list.add(pageBean);
        return list;
    }
//    private List<Favorite> favoriteList(String uidString, String currentPage, String pageSize, String rname) {
//        if (uidString == null || "".equals(uidString) || isStringNull.equals(uidString)) {
//            return null;
//        }
//
//        PageBean pageBean = new PageBean();
//        if (!(currentPage == null || "".equals(currentPage) || isStringNull.equals(currentPage))) {
//            pageBean.setCurrentPage(Integer.parseInt(currentPage));
//        }
//        if (!(pageSize == null || "".equals(pageSize) || isStringNull.equals(pageSize))) {
//            pageBean.setCurrentPage(Integer.parseInt(pageSize));
//        }
//
//        int uid = Integer.parseInt(uidString);
//        //uid和rid是收藏表的联合主键
//
//        //获取用户收藏商品封装的集合,并对其按商品Rid进行自然排序
//        List<Route> routes = userDao.routeList(uid, pageBean, rname);
//        routes.sort(Comparator.comparingInt(Route::getRid));
//
//        //获取用户收藏商品时间的集合,并对其按商品Rid进行自然排序
//        List<FavoriteBase> date = userDao.favoriteBaseList(uid, pageBean, rname);
//        date.sort(Comparator.comparingInt(FavoriteBase::getRid));
//
//        //初始化收藏类的大小
//        int size = routes.size();
//        List<Favorite> favoriteList = new ArrayList<Favorite>(size);
//        for (int i = 0; i < size; i++) {
//            Favorite f = new Favorite();
//            f.setRoute(routes.get(i));
//            f.setDate(date.get(i).getDate());
//            favoriteList.add(f);
//        }
//
//        return favoriteList;
//    }
}
