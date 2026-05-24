package com.mall.employeeoperations;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMongock
@SpringBootApplication
public class EmployeeOperationsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeOperationsServiceApplication.class, args);
    }
}
