import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by lk on 15. 11. 23..
 */
public class Main {

    public static Vertx vertx;

    public static void main(String arg[]){
        createVertx();
    }

    private static void createVertx() {
        vertx = Vertx.vertx();

        VertxOptions options = new VertxOptions();
        options.setMaxEventLoopExecuteTime(Long.MAX_VALUE);
        vertx = Vertx.vertx(options);

        vertx.deployVerticle(new MainVerticle());
    }
}
