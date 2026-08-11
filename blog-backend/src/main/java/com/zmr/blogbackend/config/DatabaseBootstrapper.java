package com.zmr.blogbackend.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

public final class DatabaseBootstrapper {

    private static final Pattern MYSQL_DATABASE_NAME = Pattern.compile("[A-Za-z0-9_]+");

    private DatabaseBootstrapper() {
    }

    public static void ensureDatabaseExists() {
        Properties properties = loadApplicationProperties();
        String datasourceUrl = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password", "");

        if (datasourceUrl == null || datasourceUrl.isBlank()) {
            return;
        }

        MysqlJdbcUrl mysqlJdbcUrl = MysqlJdbcUrl.parse(datasourceUrl);
        if (mysqlJdbcUrl == null || mysqlJdbcUrl.databaseName().isBlank()) {
            return;
        }

        if (!MYSQL_DATABASE_NAME.matcher(mysqlJdbcUrl.databaseName()).matches()) {
            throw new IllegalStateException("Invalid MySQL database name: " + mysqlJdbcUrl.databaseName());
        }

        try (Connection connection = DriverManager.getConnection(mysqlJdbcUrl.serverUrl(), username, password)) {
            if (!databaseExists(connection, mysqlJdbcUrl.databaseName())) {
                createDatabase(connection, mysqlJdbcUrl.databaseName());
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to create MySQL database: " + mysqlJdbcUrl.databaseName(), exception);
        }
    }

    private static Properties loadApplicationProperties() {
        YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
        yamlFactory.setResources(new ClassPathResource("application.yaml"));
        Properties properties = yamlFactory.getObject();
        return properties == null ? new Properties() : properties;
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        String querySql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(querySql)) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void createDatabase(Connection connection, String databaseName) throws SQLException {
        String createDatabaseSql = "CREATE DATABASE `" + databaseName
                + "` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Statement statement = connection.createStatement()) {
            statement.execute(createDatabaseSql);
        }
    }

    private record MysqlJdbcUrl(String serverUrl, String databaseName) {

        private static MysqlJdbcUrl parse(String datasourceUrl) {
            if (!datasourceUrl.startsWith("jdbc:mysql://")) {
                return null;
            }

            int queryStart = datasourceUrl.indexOf('?');
            String baseUrl = queryStart >= 0 ? datasourceUrl.substring(0, queryStart) : datasourceUrl;
            String query = queryStart >= 0 ? datasourceUrl.substring(queryStart) : "";
            int databaseStart = baseUrl.lastIndexOf('/');

            if (databaseStart < 0 || databaseStart == baseUrl.length() - 1) {
                return null;
            }

            String serverUrl = baseUrl.substring(0, databaseStart + 1) + query;
            String databaseName = baseUrl.substring(databaseStart + 1);
            return new MysqlJdbcUrl(serverUrl, databaseName);
        }
    }
}
