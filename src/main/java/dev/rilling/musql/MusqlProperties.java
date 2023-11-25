package dev.rilling.musql;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "musql")
public class MusqlProperties {

	/**
	 * Mapping of Tika metadata keys to values suitable for the database.
	 * <p>
	 * If a key is not listed, it is used as is.
	 * The mapped value may be blank to omit the key from the output.
	 */
	private Map<String, String> keyMapping = new HashMap<>();

	public Map<String, String> getKeyMapping() {
		return keyMapping;
	}

	public void setKeyMapping(Map<String, String> keyMapping) {
		this.keyMapping = keyMapping;
	}
}
