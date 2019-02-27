package cn.itcast.travel.domain;

import java.io.Serializable;

/**
 * @Author: shiki
 * @Date: 2019/1/11 16:20
 */
public class FavoriteBase implements Serializable {
    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 用户收藏日期
     */
    private String date;
    /**
     * 商品id
     */
    private Integer rid;

    public FavoriteBase() {
    }

    public FavoriteBase(Integer uid, String date, Integer rid) {
        this.uid = uid;
        this.date = date;
        this.rid = rid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    @Override
    public String toString() {
        return "FavoriteBase{" +
                "uid=" + uid +
                ", date='" + date + '\'' +
                ", rid=" + rid +
                '}';
    }
}
