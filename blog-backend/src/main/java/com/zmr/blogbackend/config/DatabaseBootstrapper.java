package com.zmr.blogbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * 启动引导：确保 MySQL 数据库存在（不存在则创建）。
 *
 * <p><b>为什么用 Spring {@link Environment} 而不是直接读 application.yaml：</b>
 * Docker / K8s 部署时通常通过环境变量覆盖 JDBC 地址（如
 * {@code SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/personal_blog}），Environment
 * 已合并 yaml 与系统环境变量，能拿到真实连接串，避免容器内仍直连 localhost 而失败。</p>
 *
 * <p><b>为什么要重试：</b>Docker Compose 只能保证 MySQL 容器启动、无法保证其内部服务就绪，
 * 若此处单次直连失败，应用会直接启动失败并陷入 restart 循环。这里采用
 * 「有限时间 + 指数退避」等待 MySQL 就绪，超时后才抛出异常。</p>
 */
public final class DatabaseBootstrapper {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBootstrapper.class);

    /** 默认等待 MySQL 就绪的超时时间（毫秒），可用 app.db.init-timeout-seconds 覆盖 */
    private static final long DEFAULT_INIT_TIMEOUT_MILLIS = 120_000L;
    /** 退避重试的最大间隔（毫秒），避免日志刷屏 */
    private static final long MAX_BACKOFF_MILLIS = 5_000L;

    private static final Pattern MYSQL_DATABASE_NAME = Pattern.compile("[A-Za-z0-9_]+");

    private DatabaseBootstrapper() {
    }

    /**
     * 确保目标数据库存在（不存在则创建）。连接 MySQL 服务器时会重试等待其就绪。
     *
     * @param environment Spring Environment（已合并 application.yaml 与系统环境变量）
     */
    public static void ensureDatabaseExists(Environment environment) {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password", "");

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

        long timeoutMillis = timeoutMillis(environment);
        long started = System.currentTimeMillis();
        int attempt = 0;

        while (true) {
            attempt++;
            try (Connection connection = DriverManager.getConnection(mysqlJdbcUrl.serverUrl(), username, password)) {
                if (!databaseExists(connection, mysqlJdbcUrl.databaseName())) {
                    createDatabase(connection, mysqlJdbcUrl.databaseName());
                }
                log.info("MySQL 连接成功（第 {} 次尝试，耗时 {} ms），数据库 {} 已就绪",
                        attempt, System.currentTimeMillis() - started, mysqlJdbcUrl.databaseName());
                return;
            } catch (SQLException exception) {
                long elapsed = System.currentTimeMillis() - started;
                if (elapsed >= timeoutMillis) {
                    throw new IllegalStateException(
                            "MySQL 在 " + timeoutMillis / 1000 + " 秒内未就绪（共尝试 " + attempt + " 次）: "
                                    + mysqlJdbcUrl.serverUrl(),
                            exception);
                }
                long backoff = Math.min((long) attempt * 1_000L, MAX_BACKOFF_MILLIS);
                log.warn("MySQL 尚未就绪（第 {} 次尝试失败，已耗时 {} ms，{} 秒后重试）：{}",
                        attempt, elapsed, backoff / 1000, exception.getMessage());
                sleep(backoff);
            }
        }
    }

    /** 通过环境变量 app.db.init-timeout-seconds 可调整等待时长（单位秒） */
    private static long timeoutMillis(Environment environment) {
        Long seconds = environment.getProperty("app.db.init-timeout-seconds", Long.class);
        if (seconds == null || seconds <= 0) {
            return DEFAULT_INIT_TIMEOUT_MILLIS;
        }
        return seconds * 1000L;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("等待 MySQL 就绪时被中断", e);
        }
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
