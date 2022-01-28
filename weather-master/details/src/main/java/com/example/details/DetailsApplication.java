package com.example.details;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker

@ComponentScan(basePackages = {"com.example.common", "com.example.details"})
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableHistrixDashboard
@EnableCaching
public class DetailsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DetailsApplication.class, args);
    }

}
