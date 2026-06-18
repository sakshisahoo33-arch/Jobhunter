package com.jobhunter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBPropertiesLoader {
    private static final String DB_PROPERTIES = "db.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = DBPropertiesLoader.class.getClassLoader().getResourceAsStream(DB_PROPERTIES)) {
            if (inputStream == null) {
                throw new IllegalStateException("Database properties file not found: " + DB_PROPERTIES);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load database properties", e);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url");
    }

    public static String getUsername() {
        return properties.getProperty("db.username");
    }

    public static String getPassword() {
        return properties.getProperty("db.password");
    }

    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
}
