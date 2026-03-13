package com.yuezupai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.yuezupai.mapper")   // 扫描Mapper接口
@EnableScheduling                       // 开启定时任务
public class YuezupaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuezupaiApplication.class, args);
        System.out.println("========================================");
        System.out.println("   悦租派后端启动成功！");
        System.out.println("========================================");
    }
}