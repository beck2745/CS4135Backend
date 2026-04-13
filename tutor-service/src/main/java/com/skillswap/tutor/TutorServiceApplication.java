package com.skillswap.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.skillswap.tutor.client")
public class TutorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutorServiceApplication.class, args);
    }
}
