package app.config;

public interface ConfigProvider {
    ConfigDB db();
    ConfigShortening shortening();
    ConfigNetwork network();
}
