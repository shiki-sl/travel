package cn.itcast.travel.domain;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * @Author: shiki
 * @Date: 2019/1/9 15:28
 */
public class JavaBeanObject implements Serializable {
    static Properties prop = new Properties();
    static{
        InputStream is = JavaBeanObject.class.getClassLoader().getResourceAsStream("javabean.properties");
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
