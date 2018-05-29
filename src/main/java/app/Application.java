package app;

import app.api.v1.url.ShortenedUrlController;
import app.config.ConfigDB;
import app.config.ConfigProvider;
import app.config.ConfigProviderImpl;
import app.redirect.RedirectController;
import app.services.shortening.ShorteningService;
import app.util.Path;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import io.javalin.Javalin;
import io.javalin.event.EventType;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource;

import java.security.NoSuchAlgorithmException;

import static io.javalin.ApiBuilder.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Application {
    private Javalin app;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private ShorteningService shorteningService;

    public Application(ConfigProvider config) throws NoSuchAlgorithmException {
        app = Javalin.create()
                .port(config.network().port())
                .defaultContentType("application/json")
                .event(EventType.SERVER_STOPPING, e -> { mongoClient.close();});
        setupDB(config.db());
        shorteningService = new ShorteningService(database, config.shortening());
        setupRoutes();
    }

    public void start() {
        app.start();
    }

    public void stop() {
        app.stop();
    }

    private void setupDB(ConfigDB config) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoClient = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(config.connectionString()))
                .build());
        database = mongoClient.getDatabase(config.databaseName());
    }

    private void setupRoutes() {

        ShortenedUrlController shortenedUrlController = new ShortenedUrlController(shorteningService);
        RedirectController redirectController = new RedirectController(shorteningService);
        //Define routes
        app.routes(() -> {
            //before(Filters.);
            get(Path.Web.REDIRECT,redirectController::redirect); //Redirect route
            path(Path.Web.API,()->{         // /api
                path(Path.Web.V1,()->{      // /api/v1
                    path(Path.Web.URL,()->{ // /api/v1/url
                        post(shortenedUrlController::createShortenedUrl);
                        get(ctx->{});
                    });
                });
            });

        });

        app.error(404,ctx->ctx.result("")); //Default route
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

        //Load config from the file pointed by the environment variable or load defaults
        ConfigProvider config = ConfigProviderImpl.createFormFileInEnvVarOrDefault("URL_SHORTENER_CONFIG");

        //Create an application instance
        Application app = new Application(config);

        //Stop on SIGINT/SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> app.stop()));

        //Start the application
        app.start();
    }
}
