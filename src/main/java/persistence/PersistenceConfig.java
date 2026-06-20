package persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceConfig {
    public static final String PU_NAME = "uno";
    public static final String DEFAULT_URL = "jdbc:h2:file:./data/uno";

    private PersistenceConfig() {
    }

    public static EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PU_NAME, buildProperties(null));
    }

    public static EntityManagerFactory createEntityManagerFactory(String jdbcUrl) {
        Map<String, String> overrides = new HashMap<String, String>();
        overrides.put("jakarta.persistence.jdbc.url", jdbcUrl);
        return Persistence.createEntityManagerFactory(PU_NAME, buildProperties(overrides));
    }

    private static Map<String, String> buildProperties(Map<String, String> overrides) {
        Map<String, String> props = new HashMap<String, String>();
        props.put("jakarta.persistence.jdbc.url", envOrDefault("UNO_DB_URL", DEFAULT_URL));
        props.put("jakarta.persistence.jdbc.user", envOrDefault("UNO_DB_USER", "sa"));
        props.put("jakarta.persistence.jdbc.password", envOrDefault("UNO_DB_PASSWORD", ""));
        if (overrides != null) {
            props.putAll(overrides);
        }
        return props;
    }

    private static String envOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
