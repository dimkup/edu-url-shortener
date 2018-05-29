package app.config;

/**
 * Full configuration provider interface
 */
public interface ConfigProvider {
    ConfigDB db();
    ConfigShortening shortening();
    ConfigNetwork network();
}
