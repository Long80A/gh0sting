package com.github.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;


public class ConfigFileReader {
    private static final Properties config = new Properties();

    /**
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T property(String key) {
        if (config.isEmpty()) {
            try {
                readConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) config.get(key);
    }


    /**
     * @return
     * @throws Exception
     */
    public static void readConfig() throws Exception {
        String path = ConfigFileReader.class.getClassLoader().getResource("config.properties").getFile();
        readConfig(path);
    }

    /**
     * @param path
     * @return
     * @throws Exception
     */
    public static void readConfig(String path) throws Exception {
        if (null == path || "".equals(path.trim())) {
            throw new IllegalArgumentException("unknown config path");
        }
        try (InputStream in = new FileInputStream(path)) {
            config.load(in);
        }
    }


}
