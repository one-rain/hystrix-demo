package com.tomato.hystrix.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class HystrixServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixServiceApplication.class, args);
	}
}
