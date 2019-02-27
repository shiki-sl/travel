package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.Category;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/8 20:55
 */
@WebServlet("/categoryServlet/*")
public class CategoryServlet extends BaseServlet {
    public void findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Category> list = categoryService.findAll();
        writeValue(response,list);
    }
}
