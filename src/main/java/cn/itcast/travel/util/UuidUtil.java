package cn.itcast.travel.util;

import java.util.UUID;

/**
 * @author shiki
 * <p>
 * 产生UUID随机字符串工具类
 */
public final class UuidUtil {
    private UuidUtil() {
    }

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
