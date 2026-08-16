package com.store.aladdin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.store.aladdin.repository")
@EnableAsync
public class AladdinApplication {

	public static void main(String[] args) {
		// Setting this in application.yml alone is too late: devtools' restart
		// mechanism initializes before Spring reads the config file, which is
		// why it was still kicking in a "restartedMain" thread despite
		// spring.devtools.restart.enabled=false being set below. Setting the
		// system property here, before SpringApplication.run(), is what
		// actually disables it — and with it goes the SilentExitException
		// that was previously getting logged as a fake "Error during startup".
		System.setProperty("spring.devtools.restart.enabled", "false");

		// Real startup confirmation (server/DB/Redis) is logged by
		// StartupHealthLogger once the app context is actually ready.
		SpringApplication.run(AladdinApplication.class, args);
	}

}
