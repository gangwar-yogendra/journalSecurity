package com.example.journalApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
// To enable the @Transactional annotation for managing transactions in the application,
// we use @EnableTransactionManagement.
@EnableTransactionManagement
// To enable the scheduling for cron job
@EnableScheduling
public class journalMongoSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(journalMongoSecurityApplication.class, args);
	}

	// Once this bean exists, you can use @Transactional in service methods
	@Bean
	public PlatformTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		// Return a transaction manager for MongoDB
		return new org.springframework.data.mongodb.MongoTransactionManager(dbFactory);
	}
}
