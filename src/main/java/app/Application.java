package app;

import app.util.Path;
import com.mongodb.client.model.Filters;
import io.javalin.Javalin;

import static io.javalin.ApiBuilder.*;

public class Application {

    public static void main(String[] args) {
        Javalin app = Javalin.start(7000);

        //Define routes
        app.routes(() -> {
                    //before(Filters.);
                    get(Path.Web.REDIRECT,ctx -> ctx.result("Hello World")); //Redirect route
                });

        app.error(404,ctx->ctx.result("")); //Default route
    }
}
