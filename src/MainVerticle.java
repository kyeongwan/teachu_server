import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;

/**
 * Created by lk on 15. 11. 23..
 */
public class MainVerticle extends AbstractVerticle {

    public void createVertx() {
        vertx.deployVerticle(new GCMVerticle());
        vertx.deployVerticle(new ChatVerticle());
        vertx.deployVerticle(new RestVerticle());
        vertx.deployVerticle(new DBVerticle());
    }

    @Override
    public void start() throws Exception {
        super.start();
        createVertx();
    }

}
