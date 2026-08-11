package com.zmr.blogbackend;

import com.zmr.blogbackend.config.DatabaseBootstrapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zmr.blogbackend.mapper")
@SpringBootApplication
public class BlogBackendApplication {

    public static void main(String[] args) {
        DatabaseBootstrapper.ensureDatabaseExists();
        SpringApplication.run(BlogBackendApplication.class, args);
    }

}
