package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.Md5Util;
import cn.itcast.travel.util.UuidUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @Author: shiki
 * @Date: 2019/1/8 9:11
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {

    /**
     * 登录功能&自动登录(对密码进行MD5加密)
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo resultInfo = new ResultInfo();
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (!check(request, response)) {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误");
            writeValue(response, resultInfo);
            return;
        }

        try {
            password = Md5Util.encodeByMd5(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = userService.loginCheck(username, password);

        if (user != null) {
            request.getSession().setAttribute("userCookie", user);

            resultInfo.setFlag(true);
            resultInfo.setErrorMsg(user.getUid()+":"+user.getUsername());

            String auto_login = request.getParameter("auto_login");
            //选中自动登录取得的值应为true
            String value = "true";
            if (value.equals(auto_login)) {
                try {
                    //保存cookie,下次自动登录
                    Cookie userCookie = new Cookie("userCookie", username+"-"+password+"-"+true);
                    userCookie.setMaxAge(60 * 60 * 24 * 180);
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                    writeValue(response, resultInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!value.equals(auto_login)) {
                Cookie userCookie = new Cookie("userCookie", username+"-"+password+"-"+false);
                userCookie.setMaxAge(60 * 60 * 24 * 180);
                userCookie.setPath("/");
                response.addCookie(userCookie);
            }
        } else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户名或密码错误");
            writeValue(response, resultInfo);
        }
    }

    /**
     * 注册功能
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //封装user对象
        User user = new User();
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            BeanUtils.populate(user, parameterMap);
            user.setCode(UuidUtil.getUuid());
            user.setStatus("N");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        boolean add = userService.addUser(user);
        if (add) {
            response.sendRedirect(request.getContextPath() + "/register_ok.html?email=" + user.getEmail());
            try {
                MailUtils.sendMail(user.getEmail(),
                        "<br><a href=http://localhost:80/travel/user/activate?code=" + user.getCode() +
                                ">点击链接进行激活账号3日内激活有效</a>", "激活邮件");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            writeValue(response, false);
        }
    }

    /**
     * ajax验证验证码
     */
    public void repeatCheck(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean check = check(request, response);
        writeValue(response, check);
    }

    /**
     * 验证校检码
     *
     * @param request
     * @param response
     * @return
     */
    private boolean check(HttpServletRequest request, HttpServletResponse response) {
        String check = request.getParameter("check");
        String checkCodeServer = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
        //当验证码为空(用户回退页面)或验证码不匹配,则向html返回错误信息
        if (checkCodeServer == null) {
            return false;
        }
        if (!checkCodeServer.equalsIgnoreCase(check)) {
            return false;
        } else {
            //验证成功后删除验证码
            request.getSession().removeAttribute("CHECKCODE_SERVER");
            return true;
        }
    }

    /**
     * 验证注册username是否存在
     */
    public void repeatName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //判断username是否存在
        String username = request.getParameter("username");
        if (username != null) {
            writeValue(response, userService.checkNewUsername(username));
            return;
        }
    }

    /**
     * 邮箱验证
     */
    public void activate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        ResultInfo activate = userService.activate(code);
        response.setContentType("text/html;charset=utf-8");
        if (activate.isFlag()) {
            response.getWriter().write(activate.getErrorMsg() + "<a href = " + request.getContextPath() + "/login.html>登录</a>");
        } else {
            response.getWriter().write(activate.getErrorMsg() + "<a href = " + request.getContextPath() + "/index.html>回主页</a>");
        }
    }

    /**
     * 显示用户名
     */
    public void username(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        ResultInfo resultInfo = new ResultInfo();
        boolean flag = user != null;
        resultInfo.setFlag(flag);
        if (flag) {
            resultInfo.setErrorMsg(user.getUid()+":"+user.getUsername());
        }
        writeValue(response, resultInfo);
    }

    /**
     * 退出登录
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().invalidate();
        Cookie user = new Cookie("userCookie", "");
        //cookie有所属的域,必须将所属的域选中替换
        user.setPath("/");
        user.setMaxAge(0);
        response.addCookie(user);

        //向浏览器回写数据
        writeValue(response, "exit");
    }

    /**
     * 根据uid查询单个用户收藏的商品信息的集合
     */
    public void query4UserFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String uidString = request.getParameter("uid");
        String currentPage = request.getParameter("currentPage");
        String pageSize = request.getParameter("pageSize");
        String rname = request.getParameter("rname");
        List<Object> list = userService.query4UserFavorite(uidString,currentPage,pageSize,rname);
        writeValue(response,list);
    }
}


