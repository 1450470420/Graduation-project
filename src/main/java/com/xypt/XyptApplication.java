package com.xypt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 校园跑腿管理系统 - SpringBoot启动类
 */
@SpringBootApplication
@EnableTransactionManagement // 开启事务管理
public class XyptApplication {

    public static void main(String[] args) {
        SpringApplication.run(XyptApplication.class, args);
    }

}
