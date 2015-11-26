import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.SocketHandler;

import static java.util.Arrays.asList;

/**
 * Created by jiyoungpark on 15. 10. 6..
 */
public class test extends AbstractVerticle {


    private final CopyOnWriteArrayList<JsonObject> messages = new CopyOnWriteArrayList<>();
    Vertx vertx;

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
//        Runner.runExample(Server.class);

        test test = new test();
        test.init();
    }

    public test() {
        vertx = Vertx.vertx();
    }

    public void init() {
        vertx.deployVerticle(this);
    }
//a["{\"type\":\"rec\",\"b\":\"chat.to.client\",\"body\":\"15. 10. 30 \uc624\ud6c4 9:03:42: testse\"}"]
    @Override
    public void start() throws Exception {

        Map<String, Object> serverConfig = new HashMap<>();
        Router router = Router.router(vertx);

        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("to.server.channel"))
                .addOutboundPermitted(new PermittedOptions().setAddress("to.channel.channel_id"));

        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);

        router.route().handler(StaticHandler.create()); // 정적파일을 내려주는 핸들러 staticHandler
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        EventBus eb = vertx.eventBus();

        // Register to listen for messages coming IN to the server
        eb.consumer("to.server.channel").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the message back out to all clients with the timestamp prepended.
            eb.publish("to.channel.channel_id",message.body());
            System.out.print(message.body());
        });


//
//        vertx.eventBus().consumer("/chat", new Handler<Message<Object>>() {
//            @Override
//            public void handle(Message<Object> message) {
//                messages.add((JsonObject) message.body());
//                System.out.println(message.body());
//                vertx.eventBus().publish("/chat", message.body());
//            }
//        });
//
//
//
//
////
//        HttpServer httpServer = vertx.createHttpServer().requestHandler(new Handler<HttpServerRequest>() {
//            @Override
//            public void handle(HttpServerRequest req) {
//                System.out.println(req.method() + " " + req.path());
//                req.response().putHeader("Content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
//
//            }
//        }).listen(8080);
//
//                req -> {
//            req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from vert.x!</h1></body></html>");
//        }).listen(8080);
    }
}