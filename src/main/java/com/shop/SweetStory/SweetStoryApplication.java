package com.shop.SweetStory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the SweetStory Spring Boot application.
 * The {@link SpringBootApplication} annotation combines application configuration,
 * Spring Boot auto-configuration, and component scanning under {@code com.shop.SweetStory} and its subpackages.
 */
@SpringBootApplication
public class SweetStoryApplication {

	/**
	 * Starts the embedded server and initializes the Spring Boot application context.
	 *
	 * @param args command-line arguments passed at startup
	 */
	public static void main(String[] args) {
		// Initialize and run the SweetStory application
		SpringApplication.run(SweetStoryApplication.class, args);
	}

}