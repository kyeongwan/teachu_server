import io.vertx.core.Verticle;

/**
 * Created by lk on 15. 11. 23..
 */
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.AsyncResult;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;

public class GCMVerticle extends AbstractVerticle{

    HttpClient client;
    String gcm_url = "http://133.130.113.101/gcm.php";
    //	String gcm_url = "http://133.130.113.101:7010/user/join?user_id=hbyullee&user_pw=1234&gcm_id=12345&device_id=123219";
//	String gcm_url = "http://www.naver.com";
    int gcm_port = 80 ;
    String apiKey = "AIzaSyBDEMveuRDedB0cA4jeeMvSBcEVkjBl-tg" ;
    URI uri ;

    @Override
    public void start() throws Exception {
        super.start();
        client = vertx.createHttpClient();


        vertx.eventBus().consumer("to.GCMVerticle.chat", new Handler<Message<String>>() {

            @Override
            public void handle(Message<String> objectMessage) {
                sendGCMMessage(objectMessage.body());
                JsonObject result = new JsonObject();
                result.put("result_code",0);
                objectMessage.reply(result);
            }
        });

        vertx.eventBus().consumer("to.GCMVerticle.notice", new Handler<Message<String>>() {

            @Override
            public void handle(Message<String> objectMessage) {
                JsonObject msgJO = new JsonObject(objectMessage.body());

                if("app".equals(msgJO.getString("to_what"))){
                    String query = String.format("SELECT user_id FROM app_user_list WHERE app_id='%s'", msgJO.getString("app_id"));
                    vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query, new Handler<AsyncResult<Message<JsonObject>>>() {

                        @Override
                        public void handle(AsyncResult<Message<JsonObject>> res) {
                            JsonArray user_list = res.result().body().getJsonArray("results");
                            StringBuilder sb = new StringBuilder();
                            for(int i=0; i<user_list.size(); i++){
                                if(i != 0)
                                    sb.append(" AND ");
                                else
                                    sb.append(" WHERE ");
                                sb.append("user_id =");
                                sb.append("'");
                                sb.append(user_list.getJsonObject(i).getString("user_id"));
                                sb.append("'");
                            }


                            String query2= String.format("SELECT gcm_id FROM user %s", sb.toString());
                            vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query2, new Handler<AsyncResult<Message<JsonObject>>>(){

                                @Override
                                public void handle(AsyncResult<Message<JsonObject>> res) {
                                    JsonArray gcm_list = res.result().body().getJsonArray("results");
//									JsonArray registration_ids = new JsonArray();
//
//									for(int i=0; i<gcm_list.size(); i++){
//											registration_ids.add(gcm_list.getJsonObject(i).getString("gcm_id"));
//									}
                                    String registration_ids="";
                                    StringBuilder sb = new StringBuilder();
                                    for(int i=0; i<gcm_list.size(); i++){
                                        if(i!=0)
                                            sb.append(",");
                                        sb.append(gcm_list.getJsonObject(i).getString("gcm_id"));
//											registration_ids.add(gcm_list.getJsonObject(i).getString("gcm_id"));
                                    }
                                    registration_ids = sb.toString();

                                    JsonObject notification = new JsonObject();
                                    notification.put("title", "껨톡");
                                    notification.put("text", msgJO.getString("msg"));

                                    JsonObject body = new JsonObject();
                                    body.put("notification", notification);
                                    body.put("registration_ids", registration_ids);
                                    body.put("data", msgJO);
                                    body.put("msg",  msgJO.getString("msg"));
                                    body.put("sender_id",  "admin");
                                    sendGCMMessage(body.toString());

                                    JsonObject resultJO = new JsonObject();
                                    resultJO.put("result_code", 0);
                                    resultJO.put("result_msg", "GCM Push 발송을 완료하였습니다.");
                                    objectMessage.reply(resultJO);
                                }

                            });
                        }
                    });
                }else if("channel".equals(msgJO.getString("to_what"))){
                    String query = String.format("SELECT user_id FROM channel_user_list WHERE channel_id = '%s'", msgJO.getString("channel_id"));
                    vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query, new Handler<AsyncResult<Message<JsonObject>>>() {

                        @Override
                        public void handle(AsyncResult<Message<JsonObject>> res) {
                            JsonArray ja = res.result().body().getJsonArray("results");
                            StringBuilder setQuery=new StringBuilder();

                            for(int j = 0; j < ja.size(); j++){
                                if(j!=0)
                                    setQuery.append("and ");
                                else
                                    setQuery.append("WHERE ");
                                setQuery.append("user_id");
                                setQuery.append("='");
                                setQuery.append(ja.getJsonObject(j).getString("user_id"));
                                setQuery.append("'");
                            }
                            setQuery.deleteCharAt(setQuery.length()-1);


                            String query2 = String.format("SELECT gcm_id FROM user %s", setQuery);
                            vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query2, new Handler<AsyncResult<Message<JsonObject>>>() {

                                @Override
                                public void handle(AsyncResult<Message<JsonObject>> res) {
                                    JsonArray gcm_list = new JsonArray();
//									JsonArray ja_list = res.result().body().getJsonArray("results");
//									for (int i = 0; i < ja_list.size(); i++) {
//										registration_ids.add(ja_list.getJsonObject(i).getString("gcm_id"));
//									}
                                    String registration_ids="";
                                    StringBuilder sb = new StringBuilder();
                                    for(int i=0; i<gcm_list.size(); i++){
                                        if(i!=0)
                                            sb.append(",");
                                        sb.append(gcm_list.getJsonObject(i).getString("gcm_id"));
//											registration_ids.add(gcm_list.getJsonObject(i).getString("gcm_id"));
                                    }
                                    registration_ids = sb.toString();

                                    JsonObject notification = new JsonObject();
                                    notification.put("title", "껨톡");
                                    notification.put("text", msgJO.getString("msg"));

                                    JsonObject body = new JsonObject();
                                    body.put("notification", notification);
                                    body.put("registration_ids", registration_ids);
                                    body.put("data", msgJO);
                                    body.put("msg",  msgJO.getString("msg"));
                                    body.put("sender_id",  "admin");
                                    sendGCMMessage(body.toString());


                                }
                            });


                        }
                    });
                }else{

                    String query3= String.format("SELECT gcm_id FROM user ");
                    vertx.eventBus().send("to.DBVerticle.selectCustomQuery", query3, new Handler<AsyncResult<Message<JsonObject>>>(){

                        @Override
                        public void handle(AsyncResult<Message<JsonObject>> res) {
                            JsonArray gcm_list = res.result().body().getJsonArray("results");
//							JsonArray registration_ids = new JsonArray();
                            String registration_ids="";
                            StringBuilder sb = new StringBuilder();
                            for(int i=0; i<gcm_list.size(); i++){
                                if(i!=0)
                                    sb.append(",");
                                sb.append(gcm_list.getJsonObject(i).getString("gcm_id"));
//									registration_ids.add(gcm_list.getJsonObject(i).getString("gcm_id"));
                            }
                            registration_ids = sb.toString();
                            JsonObject notification = new JsonObject();
                            notification.put("title", "껨톡");
                            notification.put("text", msgJO.getString("msg"));
//			        		registration_ids.add("f41Au7_ZU78:APA91bGghnqc7dN1EwykjrS3xAZMWlpTGPr7MQGBbVtmWQ7e_2824JaUUydTHzNw2NxCZGiVbkqbV0Z44NR2VvN49lT0QdYVasErneKiUfY5O8kZbOK9i_hdqRBISx__r-QB6PIjjX1X");

                            JsonObject body = new JsonObject();
                            body.put("notification", notification);
                            body.put("registration_ids", registration_ids);
                            body.put("data", msgJO);
                            body.put("msg",  msgJO.getString("msg"));
                            body.put("sender_id",  "admin");
                            sendGCMMessage(body.toString());

                            JsonObject resultJO3 = new JsonObject();
                            resultJO3.put("result_code", 0);
                            resultJO3.put("result_msg", "GCM Push 발송을 완료하였습니다.");
                            objectMessage.reply(resultJO3);
                        }

                    });

                }




            }
        });
        uri = new URI(gcm_url);

    }

    public void sendGCMMessage(String msg){
        System.out.println("gcmq responsse : "+ uri.getHost()+uri.getPath());

        JsonObject body = new JsonObject(msg);

        HttpClientRequest request = client.post(gcm_port, uri.getHost(), uri.getPath(), new Handler<HttpClientResponse>() {

            @Override
            public void handle(HttpClientResponse response) {
                System.out.println("gcm responsse : "+ response.statusMessage()+ Integer.toString(response.statusCode()));
                response.bodyHandler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer arg0) {
                        System.out.println("response: "+ new String(arg0.getBytes()));
                    }
                });
                response.exceptionHandler(new Handler<Throwable>() {

                    @Override
                    public void handle(Throwable arg0) {
                        System.out.println("gcm responsse : "+ arg0.getMessage());

                    }
                });
            }

        });
        String msg2="";
        String sender_id="";
        try {

            msg2= URLEncoder.encode(body.getString("msg"),"UTF-8");

            if(body.containsKey("sender_id"))
                sender_id= URLEncoder.encode(body.getString("sender_id"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ids ="ids="+ body.getString("registration_ids")+"&msg="+msg2+"&sender_id="+sender_id;
        request.putHeader( "Authorization", "key=" + apiKey )
                .putHeader( "Content-Type", "application/x-www-form-urlencoded" )
                .putHeader( "Content-Length", String.valueOf(ids.length()));
        System.out.println(request.headers().toString());

        Iterator it = request.headers().iterator();

        while(it.hasNext()){
            System.out.println("aa:"+it.next());
        };
        request.write(ids).end();
        System.out.println("gcm발송완료 : "+body.getString("registration_ids"));

    }



}