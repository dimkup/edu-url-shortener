package util;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoRule implements TestRule {

    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private MongoClient _mongo;

    public MongoClient getMongo() {
        return _mongo;
    }

    private void setUp() throws Exception {
        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net("localhost", 12345, Network.localhostIsIPv6()))
                .build());
        _mongod = _mongodExe.start();


        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        _mongo = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString("mongodb://localhost:12345"))
                .build());
    }

    private void tearDown() {
        _mongo.close();
        _mongod.stop();
        _mongodExe.stop();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setUp();
                try {
                    base.evaluate();
                } finally {
                    tearDown();
                }
            }
        };
    }
}
