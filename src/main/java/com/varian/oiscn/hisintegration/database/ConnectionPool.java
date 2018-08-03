package com.varian.oiscn.hisintegration.database;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionPool {
    private static DataSource basicDataSource;

    private ConnectionPool() {
    }

    public static void init() {
        Properties properties = new Properties();
        properties.setProperty("driverClassName", nullOrEmpty2DefaultValue(ConnectionParam.getDRIVER(), "org.postgresql.Driver"));

        try {
            properties.setProperty("username", nullOrEmpty2DefaultValue(ConnectionParam.getUSER(), "varian"));
        } catch (Exception e) {
            log.error("[Database Configuration] - Username is in bad format ! [{}]", e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            properties.setProperty("password", nullOrEmpty2DefaultValue(ConnectionParam.getPASSWORD(), "V@rian01"));
        } catch (Exception e) {
            log.error("[Database Configuration] - Password is in bad format ! [{}]", e.getMessage());
            throw new RuntimeException(e);
        }
        properties.setProperty("url", nullOrEmpty2DefaultValue(ConnectionParam.getURL(), "jdbc:postgresql://localhost:5432/Qin"));
        properties.setProperty("maxTotal", nullOrEmpty2DefaultValue(ConnectionParam.getMAXTOTAL(), "8"));
        properties.setProperty("initialSize", nullOrEmpty2DefaultValue(ConnectionParam.getINITIALSIZE(), "8"));
        properties.setProperty("maxIdle", nullOrEmpty2DefaultValue(ConnectionParam.getMAXIDLE(), "8"));
        properties.setProperty("minIdle", nullOrEmpty2DefaultValue(ConnectionParam.getMINIDLE(), "8"));
        properties.setProperty("maxWaitMillis", nullOrEmpty2DefaultValue(ConnectionParam.getMAXWAITMILLIS(), "5000"));
        properties.setProperty("timeBetweenEvictionRunsMillis", nullOrEmpty2DefaultValue(ConnectionParam.getTIMEBETWEENEVICTIONRUNSMILLIS(), "600000"));
        properties.setProperty("minEvictableIdleTimeMillis", nullOrEmpty2DefaultValue(ConnectionParam.getMINEVICTABLEIDLETIMEMILLIS(), "1800000"));
        try {
            basicDataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            log.error("BasicDataSourceFactory.createDataSource Exception: {} ", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (basicDataSource == null) {
            init();
        }
        try {
            return basicDataSource.getConnection();
        } catch (SQLException e) {
            log.error("SQLException getConnection=[{}]", e.getMessage());
            throw e;
        }
    }

    private static String nullOrEmpty2DefaultValue(String value, String defaultValue) {
        String tmp = StringUtils.trimToEmpty(value);
        if (StringUtils.isEmpty(tmp)) {
            return defaultValue;
        }
        return tmp;
    }
}
