package com.example.simpleonlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:/backend.properties")
public class SimpleonlinestoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleonlinestoreApplication.class, args);
	}
}
