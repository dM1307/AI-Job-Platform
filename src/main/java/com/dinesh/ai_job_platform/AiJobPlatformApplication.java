package com.dinesh.ai_job_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.dinesh.ai_job_platform.repository")
@EnableAsync
public class AiJobPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiJobPlatformApplication.class, args);
    }
}