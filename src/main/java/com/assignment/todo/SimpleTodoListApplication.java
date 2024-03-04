package com.assignment.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SimpleTodoListApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleTodoListApplication.class, args);
	}

}
