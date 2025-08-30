package com.ynm.researchpaperservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ResearchPaperServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResearchPaperServiceApplication.class, args);
    }

}
