package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.util.JdbcUtils;
import cn.itcast.travel.util.JedisUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/5 13:12
 */
public class UserDaoImpl implements UserDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
    private UserJedisDao userJedisDao = new UserJedisDao();
    private RouteDao routeDao = new RouteDaoImpl();

    /**
     * 查询redis数据内部类
     */
    private final static class UserJedisDao {
        Jedis jedis = JedisUtil.getJedis();
        ObjectMapper objectMapper = new ObjectMapper();

        private UserJedisDao() {
        }

        static {
            new UserJedisDao().init();
        }

        /**
         * 查询mysql user表,将所有用户全部信息存入redis
         */
        @PostConstruct
        void init() {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
            List<User> userList = jdbcTemplate.query("select uid,username,md5(password)password," +
                            "name, birthday,sex,telephone from tab_user",
                    new BeanPropertyRowMapper<User>(User.class));
            //以user和每个用户名作为键,user的json的字符串表示作为值
            if (userList == null) {
                return;
            }
            for (User user : userList) {
                try {
                    jedis.hset("user", user.getUsername(), objectMapper.writeValueAsString(user));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            jedis.close();
        }

        /**
         * 添加新用户时,将新用户信息保存到jedis中,减少sql查询,便于登陆
         *
         * @param loginUser
         * @return
         */
        void addUser(User loginUser) {
            String code = loginUser.getCode();
            jedis.setex(code, 60 * 60 * 24 * 3, "");
            try {
                jedis.hset("user", loginUser.getUsername(), objectMapper.writeValueAsString(loginUser));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        /**
         * 查询用户输入的用户名是否存在
         *
         * @param newUsername
         * @return
         */
        boolean checkNewUsername(String newUsername) {
            if (newUsername == null) {
                return false;
            }
            //当前用户名不存在时,返回true;
            return jedis.hget("user", newUsername) == null;
        }

        /**
         * 校检登录状态
         *
         * @param username
         * @param password
         * @return
         */
        boolean loginCheck(String username, String password) {
            User user = new User();
            String userData = jedis.hget("user", username);
            if (userData != null) {
                try {
                    user = objectMapper.readValue(userData, user.getClass());
                    return password != null && password.equals(user.getPassword());
                } catch (IOException e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        /**
         * 在jedis中查询是否存在code
         *
         * @param code 用户点击链接带来的激活码
         * @return 存在code
         */
        boolean activate(String code) {
            return jedis.get(code) != null;
        }

        /**
         * 成功激活删除jedis中对应的激活码
         *
         * @param code 激活码
         */
        void delectCode(String code) {
            jedis.del(code);
        }

    }

    /**
     * @param newUsername 新提交的用户名
     * @return 该用户名是否可用
     */
    @Override
    public boolean checkNewUsername(String newUsername) {
        return userJedisDao.checkNewUsername(newUsername);
    }

    /**
     * 向数据库添加新用户
     *
     * @param loginUser 新用户
     * @return 是否添加成功
     */
    @Override
    public boolean addUser(User loginUser) {
        String sql = "insert into tab_user(username,password,name,birthday,sex,telephone,email,status,code)" +
                "value (?,?,?,?,?,?,?,?,?)";
        int user = 0;
        try {
            user = jdbcTemplate.update(sql, loginUser.getUsername(),
                    loginUser.getPassword(), loginUser.getName(), loginUser.getBirthday(), loginUser.getSex(),
                    loginUser.getTelephone(), loginUser.getEmail(), loginUser.getStatus(), loginUser.getCode());
        } catch (DataAccessException ignored) {
        }
        //更新redis中user数据
        userJedisDao.addUser(loginUser);
        return user != 0;
    }

    /**
     * 登陆校检
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public User loginCheck(String username, String password) {
        String sql = "select * from tab_user where username = ? and md5(password) = ? ";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<User>(User.class), username, password);
    }

    /**
     * 激活用户
     *
     * @param code 激活码
     * @return
     */
    @Override
    public ResultInfo activate(String code) {
        ResultInfo resultInfo = new ResultInfo();
        if (userJedisDao.activate(code)) {
            try {
                String sql = "update tab_user set status='Y' where code=?";
                int update = jdbcTemplate.update(sql, code);
                if (update != 0) {
                    resultInfo.setFlag(true);
                    resultInfo.setErrorMsg("验证成功");
                    userJedisDao.delectCode(code);
                } else {
                    resultInfo.setFlag(false);
                    resultInfo.setErrorMsg("验证失败");
                }
                return resultInfo;
            } catch (DataAccessException e) {
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("验证失败");
                return resultInfo;
            }
        } else {
            String sql = "delete from tab_user where code=?";
            jdbcTemplate.update(sql, code);
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码过期");
            return resultInfo;
        }
    }

    //
//    @Override
//    public List<Route> routeList(int uid) {
//        String sql = "select tab_route.* from tab_favorite" +
//                "                          left join tab_user on tab_user.uid = tab_favorite.uid" +
//                "                          left join tab_route on tab_favorite.rid = tab_route.rid" +
//                "                          where tab_user.uid=? ";
//        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class), uid);
//    }
//
//    @Override
//    public List<FavoriteBase> favoriteBaseList(int uid) {
//        String sql = "select * from tab_favorite where tab_favorite.uid = ?";
//        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<FavoriteBase>(FavoriteBase.class), uid);
//    }
//
//    @Override
//    public List<Route> favoritePageBean(int uid,PageBean pageBean, String rname) {
//        StringBuilder sb = new StringBuilder("select * from tab_route where 1 = 1 ");
//        ArrayList<Object> list = new ArrayList<Object>();
//        if (uid != 0) {
//            sb.append(" and uid = ? ");
//            list.add(uid);
//        }
//        if (!"".equals(rname)) {
//            sb.append(" and rname like ? ");
//            list.add("%" + rname + "%");
//        }
//        sb.append(" limit ?,? ");
//        list.add(pageBean.getCurrentPage() * pageBean.getPageSize());
//        list.add(pageBean.getPageSize());
//        return routeDao.findQuery(0, pageBean, rname);
//    }

    @Override
    public Integer totalCount(int uid, String rname) {
        StringBuilder sql = new StringBuilder("select count(*) from tab_favorite " +
                "left join tab_route on tab_favorite.rid = tab_route.rid " +
                "where 1 = 1 ");
        List<Object> list = new ArrayList<Object>(2);
        if (uid != 0) {
            sql.append(" and uid = ? ");
            list.add(uid);
        }
        if (!"".equals(rname)) {
            sql.append(" and tab_route.rname like ? ");
            list.add('%' + rname + '%');
        }
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, list.toArray());
    }

    @Override
    public List<Route> queryRoute(int uid, PageBean pageBean, String rname) {
        StringBuilder sql = new StringBuilder("select tab_route.* from tab_favorite " +
                "left join tab_route on tab_favorite.rid = tab_route.rid " +
                "where 1 = 1 ");
        List<Object> list = new ArrayList<Object>(2);
        if (uid != 0) {
            sql.append(" and uid = ? ");
            list.add(uid);
        }
        if (!"".equals(rname)) {
            sql.append(" and tab_route.rname like ? ");
            list.add('%' + rname + '%');
        }
        sql.append(" limit ?,? ");
        list.add(pageBean.getCurrentPage() * pageBean.getPageSize());
        list.add(pageBean.getPageSize());
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<Route>(Route.class), list.toArray());
    }
}