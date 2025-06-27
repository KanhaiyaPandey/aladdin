package com.store.aladdin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;



@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.store.aladdin.repository")
public class AladdinApplication {

	private static final Logger logger = LoggerFactory.getLogger(AladdinApplication.class);
	public static void main(String[] args) {

	try {
		SpringApplication.run(AladdinApplication.class, args);
		System.out.println("Server is running. No compilation errors!");
		} catch (Exception e) {
			logger.error("Error during startup: ", e);
		}
	}

	@Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            logger.info("Server has started successfully!");
        };
    }

}
