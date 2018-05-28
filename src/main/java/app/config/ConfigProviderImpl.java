package app.config;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ConfigProviderImpl implements ConfigProvider {

    private ConfigurationSource source;
    private ConfigurationProvider provider;

    public ConfigProviderImpl(Path configFile) {

        if (configFile == null) {
            ConfigFilesProvider configFilesProvider = () -> Arrays.asList(Paths.get("config.yaml"));
            source = new ClasspathConfigurationSource(configFilesProvider);
        } else {
            ConfigFilesProvider configFilesProvider = () -> Arrays.asList(configFile);
            source = new FilesConfigurationSource(configFilesProvider);
        }

        provider = new ConfigurationProviderBuilder().withConfigurationSource(source).build();
    }

    @Override
    public ConfigDB db() {
        return provider.bind("db",ConfigDB.class);
    }

    @Override
    public ConfigShortening shortening() {
        return provider.bind("shortening",ConfigShortening.class);
    }

    @Override
    public ConfigNetwork network() {
        return provider.bind("network",ConfigNetwork.class);
    }
}