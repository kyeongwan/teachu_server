package api;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

/**
 * Created by lk on 15. 11. 25..
 */

public class LoginAPI extends BaseAPI{
    String token;
    public LoginAPI(){ }

    @Override
    public void execute(Vertx vertx, HttpServerRequest request){
        init(vertx, request);

        if(params.isEmpty() || checkValidation(params).getInteger("result_code")==-1){
            request.response().end(checkValidation(params).toString());
            return;
        }

        String query = String.format("SELECT * FROM user WHERE user_id='%s' and user_pw='%s' ",
                params.getString("user_id"), params.getString("user_pw"));
        selectCustomQuery(this, Config.getUser, query);

    }

    @Override
    public void onExecute(int what, JsonObject resultJO) {

        if(resultJO.containsKey("result_code") && resultJO.getInteger("result_code")==-1){
            request.response().end(resultJO.toString());
            return;
        }

        JsonObject rs = new JsonObject();

        switch (what) {
            case Config.getUser:
                if(resultJO.getJsonArray("results").size()>0){

                    if(!params.containsKey("gcm_id"))
                        params.put("gcm_id",resultJO.getJsonArray("results").getJsonObject(0).getString("gcm_id"));

                    if(params.containsKey("device_id")){
                        String query = String.format("UPDATE user SET gcm_id='%s', device_id='%s' WHERE user_id='%s'",
                                params.getString("gcm_id"), params.getString("device_id"), params.getString("user_id"));
                        insertCustomQuery(this, Config.updateUserInfo, query);

                    }else{
                        token = Util.getToken(params.getString("user_id"));
                        JsonObject table = new JsonObject();
                        table.put("key", token);
                        System.out.println("login redis : "+params.getString("user_id")+","+params.getString("gcm_id")+","+System.currentTimeMillis()+","+request.localAddress() );
                        table.put("value", params.getString("user_id")+","+params.getString("gcm_id")+","+System.currentTimeMillis()+","+request.localAddress());
                        setRedis(this, Config.setSession, table);
                        Util.getCache().put(params.getString("user_id"), token);
                    }

                }else{
                    rs.put("result_code", -1);
                    rs.put("result_msg", "로그인 정보가 일치하지 않습니다.");
                    request.response().end(rs.toString());
                }
                break;
            case Config.updateUserInfo:

                token = Util.getToken(params.getString("user_id"));
                JsonObject table = new JsonObject();
                table.put("key", token);
                System.out.println("login redis : "+params.getString("user_id")+","+params.getString("gcm_id")+","+System.currentTimeMillis()+","+request.localAddress() );
                table.put("value", params.getString("user_id")+","+params.getString("gcm_id")+","+System.currentTimeMillis()+","+request.localAddress());
                setRedis(this, Config.setSession, table);
                Util.getCache().put(params.getString("user_id"), token);

                break;
            case Config.setSession:
                rs.put("result_code", 0);
                rs.put("result_msg", "로그인되었습니다.");
                rs.put("token", token);
                request.response().end(rs.toString());

                break;
            default:
                break;
        }

    }

    @Override
    public JsonObject checkValidation(JsonObject params) {
        JsonObject res = new JsonObject();
        if (!params.containsKey("user_id") || params.getString("user_id").isEmpty() ||
                params.getString("user_id").equals("") || params.getString("user_id").indexOf("@")<0){

            res.put("result_code",-1);
            res.put("result_msg", "이메일 아이디를 정확히 입력하세요.");
            return res;
        }
        if (!params.containsKey("user_pw") || params.getString("user_pw").isEmpty() || params.getString("user_pw").equals("")){
            res.put("result_code",-1);
            res.put("result_msg", "비밀번호를 정확히 입력하세요.");
            return res;
        }
//		if (!params.containsKey("gcm_id") || params.getString("gcm_id").isEmpty() || params.getString("gcm_id").equals("")){
//			res.put("result_code",-1);
//			res.put("result_msg", "gcm id가 전달되지 않았습니다.");
//			return res;
//		}
//		if (!params.containsKey("device_id") || params.getString("device_id").isEmpty() || params.getString("device_id").equals("")){
//			res.put("result_code",-1);
//			res.put("result_msg", "device id가 전달되지 않았습니다.");
//			return res;
//		}
        res.put("result_code", 0);
        return res;
    }

}