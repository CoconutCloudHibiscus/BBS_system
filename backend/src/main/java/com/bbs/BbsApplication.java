package com.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BBS论坛系统启动类
 * <p>启用定时任务调度，用于Token黑名单的定时清理</p>
 */
@SpringBootApplication
@EnableScheduling
public class BbsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BbsApplication.class, args);
    }
}
