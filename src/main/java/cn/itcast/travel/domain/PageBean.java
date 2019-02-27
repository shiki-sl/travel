package cn.itcast.travel.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: shiki
 * @Date: 2019/1/9 14:30
 */
public class PageBean extends JavaBeanObject implements Serializable {
    /**
     * 总记录
     */
    private int totalCount;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 当前页
     */
    private int currentPage = Integer.parseInt(prop.getProperty("currentPage"));
    /**
     * 当前页条数
     */
    private int pageSize = Integer.parseInt(prop.getProperty("pageSize"));

    /**
     * 保存当前页信息的list集合
     */
    private List<Route> list;

    public PageBean() {
    }

    public PageBean(int totalCount, int totalPage, int currentPage, int pageSize,List<Route> list) {
        this.totalCount = totalCount;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Route> getList() {
        return list;
    }

    public void setList(List<Route> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", list=" + list +
                '}';
    }
}
