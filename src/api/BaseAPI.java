package api;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public abstract class BaseAPI {
    Vertx vertx;
    HttpServerRequest request;
    JsonObject params;

    public void init(Vertx vertx, HttpServerRequest request){
        this.vertx = vertx;
        this.request = request;
        MultiMap param = request.params();
        params = new JsonObject();
        param.forEach(entry -> params.put(entry.getKey(), entry.getValue()));

    }


    public void sendNoticeGCM(BaseAPI api, int what, String msg_data){
        vertx.eventBus().send("to.GCMVerticle.notice", msg_data, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
            }
        });
    }

    public void sendNoticeChat(BaseAPI api, int what, String msg_data){
        vertx.eventBus().send("to.ChatVerticle.notice", msg_data, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
            }
        });

    }

    public void setPermission(BaseAPI api, int what,  String channel_id){
        System.out.println(what+" setRedis execute");

        vertx.eventBus().send("to.ChatVerticle.permit", channel_id, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void mgetRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" mgetRedis execute");

        vertx.eventBus().send("to.RedisVerticle.mget", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }
    public void keysRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" keysRedis execute");

        vertx.eventBus().send("to.RedisVerticle.keys", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }


    public void mgetManyRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" mgetManyRedis execute");

        vertx.eventBus().send("to.RedisVerticle.mgetMany", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void delRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" delRedis execute");

        vertx.eventBus().send("to.RedisVerticle.del", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void setRedis(BaseAPI api, int what,  JsonObject table){
        System.out.println(what+" setRedis execute");
        System.out.println(api.getClass().getName() + table.toString());
        vertx.eventBus().send("to.RedisVerticle.set", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what, res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void getRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" getRedis execute");

        vertx.eventBus().send("to.RedisVerticle.get", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                if(res!=null)
                    api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }
    public void sremRedis(BaseAPI api, int what,  JsonObject table){
       vertx.eventBus().send("to.RedisVerticle.srem", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void saddRedis(BaseAPI api, int what,  JsonObject table){
        System.out.println(what+" saddRedis execute");

        vertx.eventBus().send("to.RedisVerticle.sadd", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void smembersRedis(BaseAPI api, int what,  String key){
        System.out.println(what+" smembersRedis execute");

        vertx.eventBus().send("to.RedisVerticle.smembers", key, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                if(res!=null)
                    api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }
    public void insertQuery(BaseAPI api, int what,  JsonObject table){
        System.out.println(what+" insertQuery execute");

        vertx.eventBus().send("to.DBVerticle.insertQuery", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void insertCustomQuery(BaseAPI api, int what,  String query){
        System.out.println(what+" insertQuery execute");

        vertx.eventBus().send("to.DBVerticle.insertCustomQuery", query, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void selectQuery(BaseAPI api, int what,  JsonObject table){
        System.out.println(what+" selectQuery execute");

        vertx.eventBus().send("to.DBVerticle.selectQuery", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void selectCustomQuery(BaseAPI api, int what,  String query){
        System.out.println(what+" selectQuery execute");

        vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public void selectCustomQuery2(BaseAPI api, int what,  String query){
        System.out.println(what+" selectQuery execute");

        vertx.eventBus().send("to.DBVerticle.selectCustomQuery2", query, new Handler<AsyncResult<Message<JsonObject>>>() {

            @Override
            public void handle(AsyncResult<Message<JsonObject>> res) {
                api.onExecute(what,  res.result().body());
                System.out.println(api.getClass().getName() + " onExecute : " + res.result().body().toString() );

            }
        });
    }

    public abstract JsonObject checkValidation( JsonObject Params );

    public abstract void execute(Vertx vertx, HttpServerRequest params);

    public abstract void onExecute(int what, JsonObject resultJO);

}