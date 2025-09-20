package com.store.aladdin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.store.aladdin.repository")
@Slf4j
@EnableAsync
public class AladdinApplication {


	public static void main(String[] args) {
	try {
		SpringApplication.run(AladdinApplication.class, args);
		log.info("✅ Server is running on port 8080. No compilation errors!");
		} catch (Exception e) {
			log.error("Error during startup: ", e);
		}
	}
	@Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("Server has started successfully!");
        };
    }

}
