package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 10:21
 */
@WebServlet("/routeServlet/*")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();

    /**
     * 分页查询
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cid = request.getParameter("cid");
        String currentPageString = request.getParameter("currentPage");
        String rname = request.getParameter("rname");

        User user = (User) request.getSession().getAttribute("user");

        //cid 用户选择的条目
        PageBean pb = routeService.findQuery(cid, rname, currentPageString, null);
        writeValue(response, pb);
    }

    /**
     * 查询选中页详细信息
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("user");
        int uid = -1;
        if (user != null) {
            uid = user.getUid();
        }
        List<Object> list = routeService.findOne(rid, uid);
        writeValue(response, list);
    }

    /**
     * 更改收藏状态
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void updateFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ridString = request.getParameter("rid");
        String flag = request.getParameter("flag");
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            int uid = user.getUid();
            double favorite = routeService.updateFavorite(ridString, uid, flag);
            writeValue(response, favorite);
        } else {
            writeValue(response, "");
        }

    }

    /**
     * 搜索分页查询排行榜
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void rankingList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rname = request.getParameter("rname");
        String minString = request.getParameter("min");
        String maxString = request.getParameter("max");
        String currentPage = request.getParameter("currentPage");
        String pageSize = request.getParameter("pageSize");
        List<Route> list = routeService.rankingList(rname, minString, maxString, currentPage, pageSize);
        writeValue(response, list);
    }

    /**
     * 人气旅游
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void hotRankingList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rname = null;
        String minString = null;
        String maxString = null;
        String currentPage = "0";
        String pageSize = "4";
        List<Route> list = routeService.rankingList(rname, minString, maxString, currentPage, pageSize);
        writeValue(response, list);
    }

    /**
     * 最新旅游
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void newTour(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Route> list = routeService.newTour();
        writeValue(response, list);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void theme(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Route> list = routeService.theme();
        writeValue(response, list);
    }

    public void queryFavoriteScore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ridString = request.getParameter("rid");
        double score = routeService.queryFavoriteScore(ridString);
        writeValue(response, score);
    }

    public void totalCount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int totalCount = routeService.totalCount();
        writeValue(response, totalCount);
    }

}
