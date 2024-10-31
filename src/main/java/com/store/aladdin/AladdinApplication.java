package com.store.aladdin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.store.aladdin.repository")
public class AladdinApplication {

	public static void main(String[] args) {
		SpringApplication.run(AladdinApplication.class, args);
	}

}
