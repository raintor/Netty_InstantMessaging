package com.nuaa.hchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: raintor
 * @Date: 2019/6/22 14:56
 * @Description:
 */
@SpringBootApplication
@MapperScan("com.nuaa.hchat.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
