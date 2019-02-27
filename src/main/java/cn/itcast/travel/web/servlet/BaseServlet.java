package cn.itcast.travel.web.servlet;

import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.CategoryServiceImpl;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: shiki
 * @Date: 2019/1/8 9:24
 */

public class BaseServlet extends HttpServlet {
    /**
     * 查询用户的UserService对象
     */
    UserService userService = new UserServiceImpl();

    /**
     * 查询旅游路线的CategoryService对象
     */
    CategoryService categoryService = new CategoryServiceImpl();

    /**
     * 方法分发
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        //不验证.html静态资源
        String html = "html";
        if (uri.contains(html)) {
            return;
        }

        String methodName = uri.substring(uri.lastIndexOf('/') + 1);

        try {
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * json对象转化为字符串表示
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public String writeValueAsString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    /**
     * ajax发送请求
     *
     * @param response
     * @param obj      ajax发送对象
     * @throws IOException
     */
    void writeValue(HttpServletResponse response, Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), obj);
    }

    @Override
    public void destroy() {
    }
}
