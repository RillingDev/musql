package org.felixrilling.musql;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

@SpringBootTest
@ContextConfiguration(initializers = {BaseIT.H2DataSourceInitializer.class})
abstract class BaseIT {

	static class H2DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		@Override
		public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
			TestPropertyValues.of(Map.ofEntries(Map.entry("spring.datasource.url", "jdbc:h2:mem:musql"),
				Map.entry("spring.datasource.username", "sa"),
				Map.entry("spring.datasource.password", ""))).applyTo(applicationContext.getEnvironment());
		}
	}
}
