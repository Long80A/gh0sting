package com.github.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class LogUtil {

    private static final String DATA_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";


    public static void info(String msg) {
        StringBuilder builder = new StringBuilder();
        builder.append(dataString());
        builder.append(" [INFO] ");
        builder.append(msg);
        System.out.println(builder);
    }

    public static void info(String msg, Throwable t) {
        info(msg);
        t.printStackTrace();
    }

    private static String dataString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATA_FORMAT);
        return sdf.format(new Date());
    }

}
