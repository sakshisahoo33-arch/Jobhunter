package com.jobhunter.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for obtaining JDBC connections using settings from db.properties.
 * <p>
 * Loads properties via the context ClassLoader and initializes the JDBC driver.
 */
public final class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties PROPS = new Properties();

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("Database properties file not found on classpath: " + PROPERTIES_FILE);
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database properties from " + PROPERTIES_FILE, e);
        }

        URL = PROPS.getProperty("db.url");
        USERNAME = PROPS.getProperty("db.username");
        PASSWORD = PROPS.getProperty("db.password");
        String driver = PROPS.getProperty("db.driver");

        if (URL == null || USERNAME == null || PASSWORD == null || driver == null) {
            throw new IllegalStateException("One or more required database properties are missing in " + PROPERTIES_FILE);
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Database driver class not found: " + driver, e);
        }
    }

    private DBConnection() {
        // utility
    }

    /**
     * Obtain a new JDBC connection using properties from the classpath resource.
     *
     * @return established {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Close a {@link ResultSet} quietly.
     */
    public static void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to close ResultSet", e);
            }
        }
    }

    /**
     * Close a {@link Statement} quietly.
     */
    public static void closeQuietly(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to close Statement", e);
            }
        }
    }

    /**
     * Close a {@link Connection} quietly.
     */
    public static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to close Connection", e);
            }
        }
    }
}
