package com.zmr.blogbackend;

import com.zmr.blogbackend.config.DatabaseBootstrapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@MapperScan("com.zmr.blogbackend.mapper")
@SpringBootApplication
public class BlogBackendApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BlogBackendApplication.class);
        // 在 Spring 容器初始化（HikariCP / schema.sql / data.sql）之前确保数据库存在。
        // 用 ApplicationContextInitializer 而非 main() 内直接调用：
        //   1. 能拿到已合并 yaml + 环境变量的 Environment，兼容 Docker 里的 SPRING_DATASOURCE_* 覆盖；
        //   2. 仍比数据源与 sql.init 更早执行，同时内置重试等待 MySQL 就绪。
        application.addInitializers((ApplicationContextInitializer<ConfigurableApplicationContext>) context ->
                DatabaseBootstrapper.ensureDatabaseExists(context.getEnvironment()));
        application.run(args);
    }

}
