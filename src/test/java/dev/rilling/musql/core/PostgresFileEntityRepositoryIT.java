package dev.rilling.musql.core;

import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@JdbcTest(excludeAutoConfiguration = {TestDatabaseAutoConfiguration.class}) // Disable default embedded test database
@ContextConfiguration(classes = {FileEntityRepository.class})
public class PostgresFileEntityRepositoryIT extends AbstractFileEntityRepositoryIT {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:16");


}
