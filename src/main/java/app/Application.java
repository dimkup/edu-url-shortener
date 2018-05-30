package app;

import app.api.v1.ErrorResponse;
import app.api.v1.url.ShortenedUrlController;
import app.config.ConfigDB;
import app.config.ConfigProvider;
import app.config.ConfigProviderImpl;
import app.redirect.RedirectController;
import app.services.shortening.ShorteningService;
import app.services.shortening.exceptions.ShortenedUrlNotFoundException;
import app.util.Path;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import io.javalin.Javalin;
import io.javalin.event.EventType;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletionException;

import static io.javalin.ApiBuilder.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Main application class
 * Contains routes and exceptions handling config
 */
public class Application {
    private Javalin app;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private ShorteningService shorteningService;

    public Application(ConfigProvider config) throws NoSuchAlgorithmException {
        app = Javalin.create()
                .port(config.network().port())
                .defaultContentType("application/json")
                //Close DB connection on shutdown
                .event(EventType.SERVER_STOPPING, e -> mongoClient.close());

        setupDB(config.db());
        shorteningService = new ShorteningService(database, config.shortening());
        setupRoutes();
        setupExceptions();
    }

    /**
     * Starts the application
     */
    public void start() {
        app.start();
    }

    /**
     * Stops the application
     */
    public void stop() {
        app.stop();
    }

    /**
     * Creates database client
     * @param config database configuration instance
     */
    private void setupDB(ConfigDB config) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoClient = MongoClients.create(MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(config.connectionString()))
                .build());
        database = mongoClient.getDatabase(config.databaseName());
    }

    /**
     * Maps paths to handlers
     */
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
                        get(shortenedUrlController::getShortenedUrl);
                    });
                });
            });

        });



    }

    /**
     * Maps exceptions to handlers
     */
    private void setupExceptions() {

        app.exception(MalformedURLException.class,(e, ctx)->{ //Bad URL in the request
            ctx.status(400).json(new ErrorResponse("Can't parse the URL"));
        });

        app.exception(CompletionException.class, (e, ctx) -> {
            if (e.getCause() instanceof ShortenedUrlNotFoundException) {//ShortUrl is not found
                ctx.status(404).json(new ErrorResponse("ShortUrl is not found"));

            } else if (e.getCause() instanceof MalformedURLException) { //we have a bad URL from the DB, let say 500
                ctx.status(500).json(new ErrorResponse("Inconsistent DB"));

            } else if (e.getCause() instanceof MongoWriteException) { //Could be hash collision - fix it!
                ctx.status(503).json(new ErrorResponse("Please try again later"));

            } else { //Default for unhandled async exceptions
                ctx.status(500).json(new ErrorResponse("Internal server error"));
            }
        });
    }


    /**
     * Main entry point
     * @param args - command line args
     * @throws NoSuchAlgorithmException
     */
    public static void main(String[] args) throws NoSuchAlgorithmException {

        //Load config from the file pointed by the environment variable or load defaults
        ConfigProvider config = ConfigProviderImpl.createFormFileInEnvVarOrDefault("URL_SHORTENER_CONFIG");

        //Create an application instance
        Application app = new Application(config);

        //Stop on SIGINT/SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        //Start the application
        app.start();
    }
}
