package cn.itcast.travel.web.filter;

import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: shiki
 * @Date: 2019/1/4 23:29
 */
public class LoginFilter implements Filter {
    /**
     * USER_SERVICE UserService对象
     */
    private UserService userService = new UserServiceImpl();
    /**
     * OBJECT_MAPPER
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        User user = (User) request.getSession().getAttribute("user");
        //自动登录

        //用户是否选中自动登录
        String autoLoginCookie = "autoLoginCookie";
        Object attribute = request.getSession().getAttribute(autoLoginCookie);
        if (user == null && attribute == null) {
            join(request, response);
        }

        chain.doFilter(req, resp);
        resp.setCharacterEncoding("utf-8");
    }

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    /**
     * 自动登录
     *
     * @param req
     * @param resp
     */
    private void join(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();
        if (cookies == null || cookies.length == 0) {
            return;
        }

        String userCookie = null;
        for (Cookie cookie : cookies) {
            if ("userCookie".equals(cookie.getName())) {
                userCookie = cookie.getValue();
            }
        }

        //用户没有userCookie
        if (userCookie == null) {
            return;
        }

        String[] split = userCookie.split("-");
        //用户未选中自动登录
        String s = "false";
        int autoLogin = 2;
        if (s.equals(split[autoLogin])) {
            return;
        }

        //选中自动登录则username与password必定保存在cookie中
        User user = userService.loginCheck(split[0], split[1]);
        if (user != null) {
            //登陆成功保存用户登录状态
            req.getSession().setAttribute("user", user);
        }
    }
}
