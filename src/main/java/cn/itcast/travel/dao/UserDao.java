package cn.itcast.travel.dao;

import cn.itcast.travel.domain.*;

import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/5 13:10
 */
public interface UserDao {
    /**
     * 查询用户名是否存在
     *
     * @param newUsername 新提交的用户名
     * @return 用户名是否存在 true为不存在可用
     */
    boolean checkNewUsername(String newUsername);

    /**
     * 添加一名新用户
     *
     * @param loginUser 新用户
     * @return 是否添加成功
     */
    boolean addUser(User loginUser);

    /**
     * 登录验证
     *
     * @param username
     * @param password
     * @return user不为null表明登录成功
     */
    User loginCheck(String username, String password);

    /**
     * 邮箱验证
     *
     * @param code 激活码
     * @return 是否成功
     */
    ResultInfo activate(String code);

    /**
     * 查询用户搜索指定内容的收藏总条数
     *
     * @param uid
     * @param rname
     * @return
     */
    Integer totalCount(int uid, String rname);

    /**
     * 分页查询收藏信息
     * @param uid
     * @param pageBean
     * @param rname
     * @return
     */
    List<Route> queryRoute(int uid,PageBean pageBean, String rname);

//    /**
//     * 根据uid查询收藏的商品信息的集合
//     * @param uid
//     * @return
//     */
//    List<Route> routeList(int uid);
//
//    /**
//     * 根据uid查询收藏的商品rid和收藏时间的封装类集合
//     * @param uid
//     * @return
//     */
//    List<FavoriteBase> favoriteBaseList(int uid);
//
//    /**
//     * 返回分页数据
//     * @param pageBean
//     * @param rname
//     * @return
//     */
//    List<Route> favoritePageBean(int uid,PageBean pageBean, String rname);
}
