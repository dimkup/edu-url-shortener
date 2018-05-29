package util;

import app.config.ConfigDB;
import app.config.ConfigNetwork;
import app.config.ConfigProvider;
import app.config.ConfigShortening;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.net.MalformedURLException;
import java.net.URL;

public class ConfigRule implements TestRule {
    private static final String BASE_URL = "http://localhost:7001/";
    private ConfigProvider config;

    public ConfigProvider getConfig() {return config;};

    private void setUp() {
        config = new ConfigProvider() {
            @Override
            public ConfigDB db() {
                return new ConfigDB() {
                    @Override
                    public String connectionString() {
                        return "mongodb://localhost:12345";
                    }

                    @Override
                    public String databaseName() {
                        return "inttest";
                    }
                };
            }

            @Override
            public ConfigShortening shortening() {
                return new ConfigShortening() {
                    @Override
                    public URL baseUrl() {
                        try {
                            return new URL(BASE_URL);
                        } catch (MalformedURLException e) {
                            Assert.fail("Can't construct URL");
                        }
                        return null;
                    }

                    @Override
                    public Integer hashLen() {
                        return 6;
                    }
                };
            }

            @Override
            public ConfigNetwork network() {
                return new ConfigNetwork() {
                    @Override
                    public int port() {
                        return 7001;
                    }
                };
            }
        };
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setUp();
                base.evaluate();
            }
        };
    }
}
