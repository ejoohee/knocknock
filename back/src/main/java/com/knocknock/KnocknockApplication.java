package com.knocknock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KnocknockApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnocknockApplication.class, args);
	}

}
