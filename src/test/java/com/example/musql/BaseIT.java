package com.example.musql;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ContextConfiguration(initializers = {BaseIT.PostgreDataSourceInitializer.class})
@Testcontainers
abstract class BaseIT {

	@Container
	static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:14").withDatabaseName(
		"integration-tests-db").withUsername("sa").withPassword("sa");


	static class PostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
			TestPropertyValues.of("spring.datasource.url=" + POSTGRE_SQL_CONTAINER.getJdbcUrl(),
					"spring.datasource.username=" + POSTGRE_SQL_CONTAINER.getUsername(),
					"spring.datasource.password=" + POSTGRE_SQL_CONTAINER.getPassword())
				.applyTo(applicationContext.getEnvironment());
		}
	}
}
