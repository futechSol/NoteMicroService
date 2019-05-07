package com.bridgelabz.noteMicroService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
public class NoteMicroServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteMicroServiceApplication.class, args);
	}

}
