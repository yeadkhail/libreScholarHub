package com.ynm.usermanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UserManagementServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }
}