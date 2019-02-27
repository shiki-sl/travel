package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.JedisFavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.domain.*;
import cn.itcast.travel.util.JdbcUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 10:22
 */
public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(JdbcUtils.getDataSource());
    private JedisFavoriteDao jedisFavoriteDao = new JedisFavoriteDaoImpl();

    @Override
    public List<Route> findQuery(int cid, PageBean pageBean, String rname) {
        StringBuilder sb = new StringBuilder("select * from tab_route where 1 = 1 ");
        ArrayList<Object> list = new ArrayList<Object>();
        if (cid != 0) {
            sb.append(" and cid = ? ");
            list.add(cid);
        }
        if (!"".equals(rname)) {
            sb.append(" and rname like ? ");
            list.add("%" + rname + "%");
        }
        sb.append(" limit ?,? ");
        list.add(pageBean.getCurrentPage() * pageBean.getPageSize());
        list.add(pageBean.getPageSize());
        return jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Route>(Route.class), list.toArray());
    }

    @Override
    public int findCount(int cid, String rname) {
        StringBuilder sb = new StringBuilder("select count(*) from tab_route where 1=1");
        ArrayList<Object> list = new ArrayList<Object>(2);
        if (cid != 0) {
            sb.append(" and cid = ? ");
            list.add(cid);
        }
        if (!"".equals(rname)) {
            sb.append(" and rname like ? ");
            list.add("%" + rname + "%");
        }

        return jdbcTemplate.queryForObject(sb.toString(), Integer.class, list.toArray());
    }

    @Override
    public Route findRouteOne(int rid) {
        String sql = "select * from tab_route where rid = ? ";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Route>(Route.class), rid);
    }

    @Override
    public List<RouteImg> routeImgList(int rid) {
        String sql = "select * from tab_route_img where rid = ? ";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<RouteImg>(RouteImg.class), rid);
    }

    @Override
    public Seller routeSeller(int sid) {
        String sql = "select * from tab_seller where sid = ? ";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Seller>(Seller.class), sid);
    }

    @Override
    public Category routeCategory(int cid) {
        String sql = "select * from tab_category where cid = ? ";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Category>(Category.class), cid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public String favoriteDate(int rid, int uid) {
        try {
            String sqlDate = "select date from tab_favorite where rid = ? and uid = ?";
            return jdbcTemplate.queryForObject(sqlDate, String.class, rid, uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public User favoriteUser(int uid) {
        try {
            String sqlUser = "select * from tab_user where uid = ? ";
            return jdbcTemplate.queryForObject(sqlUser, new BeanPropertyRowMapper<User>(User.class), uid);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Favorite> queryUserFavorite(int uid) {
        try {
            String sql = "select tab_route.*,tab_favorite.date,tab_user.* from tab_favorite " +
                    "left join tab_user on ? = tab_favorite.uid " +
                    "left join tab_route on tab_favorite.rid = tab_route.rid";
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Favorite>(Favorite.class), uid);
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Override
    public void addFavorite(int rid, int uid) {
        String date = "" + System.currentTimeMillis();
        String sql = "insert into tab_favorite(rid, date, uid) VALUE (?,?,?)";
        try {
            jdbcTemplate.update(sql, rid, date, uid);
        } catch (DataAccessException ignored) {
        }
    }

    @Override
    public void delFavorite(int rid, int uid) {
        String sql = "delete from tab_favorite where rid=? and uid=?";
        try {
            jdbcTemplate.update(sql, rid, uid);
        } catch (DataAccessException ignored) {
        }
    }

    @Override
    public List<Route> rankingList(int start, int end) {
        jedisFavoriteDao.rankingList(start, end);
        return null;
    }

    @Override
    public List<Route> rankingList(String rname, double min, double max, int currentPage, int pageSize) {
        StringBuilder sb = new StringBuilder("SELECT  tab_route.rid,\n" +
                "        tab_route.rname,\n" +
                "        tab_route.price,\n" +
                "        tab_route.routeIntroduce,\n" +
                "        tab_route.rflag,\n" +
                "        tab_route.rdate,\n" +
                "        tab_route.isThemeTour,\n" +
                "    count(tab_favorite.`uid`) as count,\n" +
                "        tab_route.cid,\n" +
                "        tab_route.rimage,\n" +
                "        tab_route.sid,\n" +
                "        tab_route.sourceId\n" +
                "   from tab_route\n" +
                "       left join tab_favorite on tab_route.rid = tab_favorite.rid\n" +
                "where 1=1 ");
        ArrayList<Object> list = new ArrayList<Object>(16);

        if (!(min == 0 && max == 0)) {
            sb.append(" and price between ? and ? ");
            list.add(min);
            list.add(max);
        }

        if (!"".equals(rname)) {
            sb.append(" and (rname like ? or routeIntroduce like ?) ");
            list.add("%" + rname + "%");
            list.add("%" + rname + "%");
        }

        sb.append(" group by tab_route.rid order by count desc limit ?,? ");
        list.add(currentPage * pageSize);
        list.add(pageSize);
        return jdbcTemplate.query(sb.toString(), new BeanPropertyRowMapper<Route>(Route.class), list.toArray());
    }

    @Override
    public List<Route> newTour() {
        String sql = "SELECT * from tab_route order by rdate desc limit 0,4";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
    }

    @Override
    public List<Route> theme() {
        String sql = "SELECT * from tab_route where isThemeTour = 1 limit 0,4";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<Route>(Route.class));
    }

    @Override
    public int totalCount() {
        String sql = "select * from tab_route";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

}