package com.microservice.Employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for the Employee microservice.
 * This class bootstraps and launches the Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
public class EmployeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeApplication.class, args);
	}

}
