package unit;

import app.config.ConfigProvider;
import app.config.ConfigProviderImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import java.io.File;

public class TestConfigProvider {

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Test
    public void testLoadDefaultConfig() {
        ConfigProvider provider = new ConfigProviderImpl(null);

        Assert.assertEquals("mongodb://localhost:27017",provider.db().connectionString());
        Assert.assertEquals("eduurlshortener",provider.db().databaseName());

        Assert.assertEquals(7000,provider.network().port());

        Assert.assertEquals("http://localhost:7000",provider.shortening().baseUrl().toString());
        Assert.assertEquals(6,(long)provider.shortening().hashLen());



    }

    @Test
    public void testLoadConfigFromFile() {

        File file = new File(getClass().getResource("/test.yaml").getFile());

        ConfigProvider provider = ConfigProviderImpl.createFromFile(file.toPath());

        Assert.assertEquals("mongodb://localhost:27018",provider.db().connectionString());
        Assert.assertEquals("eduurlshortener",provider.db().databaseName());

        Assert.assertEquals(7001,provider.network().port());

        Assert.assertEquals("http://localhost:7001",provider.shortening().baseUrl().toString());
        Assert.assertEquals(7,(long)provider.shortening().hashLen());



    }

    @Test
    public void testLoadConfigFromFileInEnvVar() {
        final String envVarName = "TEST_URL_SHORTENER_CONFIG";

        File file = new File(getClass().getResource("/test.yaml").getFile());

        environmentVariables.set(envVarName, file.getPath());

        try {

            ConfigProvider provider = ConfigProviderImpl.createFormFileInEnvVarOrDefault(envVarName);

            Assert.assertEquals("mongodb://localhost:27018",provider.db().connectionString());
            Assert.assertEquals("eduurlshortener",provider.db().databaseName());

            Assert.assertEquals(7001,provider.network().port());

            Assert.assertEquals("http://localhost:7001",provider.shortening().baseUrl().toString());
            Assert.assertEquals(7,(long)provider.shortening().hashLen());

        } finally {
            environmentVariables.clear(envVarName);
        }

    }

    @Test
    public void testLoadConfigDefaultWhenEnvVarEmpty() {
        final String envVarName = "TEST_URL_SHORTENER_CONFIG";

        File file = new File(getClass().getResource("/test.yaml").getFile());

        environmentVariables.clear(envVarName);


        ConfigProvider provider = ConfigProviderImpl.createFormFileInEnvVarOrDefault(envVarName);

        Assert.assertEquals("mongodb://localhost:27017",provider.db().connectionString());
        Assert.assertEquals("eduurlshortener",provider.db().databaseName());

        Assert.assertEquals(7000,provider.network().port());

        Assert.assertEquals("http://localhost:7000",provider.shortening().baseUrl().toString());
        Assert.assertEquals(6,(long)provider.shortening().hashLen());



    }
}
