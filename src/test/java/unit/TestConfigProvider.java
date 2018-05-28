package unit;

import app.config.ConfigProvider;
import app.config.ConfigProviderImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

public class TestConfigProvider {

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

        ConfigProvider provider = new ConfigProviderImpl(file.toPath());

        Assert.assertEquals("mongodb://localhost:27017",provider.db().connectionString());
        Assert.assertEquals("eduurlshortener",provider.db().databaseName());

        Assert.assertEquals(7000,provider.network().port());

        Assert.assertEquals("http://localhost:7000",provider.shortening().baseUrl().toString());
        Assert.assertEquals(6,(long)provider.shortening().hashLen());



    }
}
