package app.config;

/**
 * Database configuration provider interface
 */
public interface ConfigDB {
    /**
     *
     * @return database connection string
     */
    String connectionString();

    /**
     *
     * @return database name
     */
    String databaseName();
}
