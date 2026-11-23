package com.shop.SweetStory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test verifying that the {@code SweetStory} application context loads properly.
 *
 * <p>Uses {@link SpringBootTest} to start the full Spring IoC container in a test
 * environment, confirming configuration validity, component scanning, and bean dependency
 * resolution.
 */
@SpringBootTest
class SweetStoryApplicationTests {

	/**
	 * Verifies that the Spring Boot context starts without initialization errors.
	 */
	@Test
	void contextLoads() {
	}

}