package com.tencent.memory;

import com.tencent.memory.service.UploadService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan(basePackages = "com.tencent.memory.dao")
public class MemoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UploadService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
