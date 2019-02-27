package cn.itcast.travel.service;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/5 13:07
 */
public interface UserService {
    /**
     * 查询用户名是否存在
     *
     * @param newUsername 新提交的用户名
     * @return 用户名是否重复 true为不重复可用
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
     * @return 验证结果
     */
    User loginCheck(String username, String password);

    /**
     * 邮箱验证
     *
     * @param code 激活码
     * @return 返回ResultInfo对象
     */
    ResultInfo activate(String code);

    /**
     * 查询单个用户收藏商品(包含分页的)的封装类集合
     *
     * @param uidString
     * @param currentPage
     * @param pageSize
     * @param rname
     * @throws  UnsupportedEncodingException
     * @return
     */
    List<Object> query4UserFavorite(String uidString, String currentPage, String pageSize, String rname) throws UnsupportedEncodingException;
}
