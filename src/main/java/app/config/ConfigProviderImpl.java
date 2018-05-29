package app.config;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

/**
 * Configuration provider implementation
 */
public class ConfigProviderImpl implements ConfigProvider {

    private ConfigurationSource source;
    private ConfigurationProvider provider;

    /**
     * reads config from the file pointed by the env variable or reads default
     * if the variable is not set
     * @param varName - environment variable to check
     * @return - instance of the ConfigProvider
     */
    public static ConfigProvider createFormFileInEnvVarOrDefault(String varName) {
        String propVarName = varName.replace('_','.');

        EnvironmentVariablesConfigurationSource source = new EnvironmentVariablesConfigurationSource();
        source.init();
        Properties envConfig = source.getConfiguration(new DefaultEnvironment());
        String configFilePath = envConfig.getProperty(propVarName);

        if (configFilePath!=null&&!configFilePath.isEmpty()) {
            return createFromFile(Paths.get(configFilePath));
        } else return createFromFile(null);

    }

    /**
     * reads config from the configFile or reads default if configFile is null
     * @param configFile
     * @return - instance of the ConfigProvider
     */
    public static ConfigProvider createFromFile(Path configFile) {
        return new ConfigProviderImpl(configFile);
    }

    public ConfigProviderImpl(Path configFile) {

        if (configFile == null) {
            ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(Paths.get("config.yaml"));
            source = new ClasspathConfigurationSource(configFilesProvider);
        } else {
            ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(configFile);
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
