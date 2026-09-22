package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@Order(2)
public class MySpringConsoleHelloWorldApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(MySpringConsoleHelloWorldApplication.class);

	public static void main(String[] args) {
		logger.info("Begin of main");
		SpringApplication.run(MySpringConsoleHelloWorldApplication.class, args);
		logger.info("End of main"); 
		
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello Artem!");
	}

}

	
