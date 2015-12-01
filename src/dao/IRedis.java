package dao;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cwell on 2015-10-12.
 */
public abstract class IRedis {
	RedisClient client;
    Message<String> retMessage;
    boolean autoConClose = true;

    public IRedis(RedisClient client, Message<String> retMessage) {
        this.client = client;
        this.retMessage = retMessage;
    }

    public IRedis(RedisClient client, Message<String> retMessage, boolean autoConClose) {
        this.client = client;
        this.retMessage = retMessage;
        this.autoConClose = autoConClose;
    }

    public abstract void success(Object result);

    void fail(String msg){
    	JsonObject jo = new JsonObject();
    	
        jo.put("result_code", -1);
        jo.put("result_msg", msg);
    	reply(jo);
        
        System.out.println("Redis Failed: "+ msg);
    };

    public void reply(JsonObject msg) {
        if(retMessage == null) return;
        retMessage.reply(msg);
    }

    public void get(String key){
		client.get(key, new Handler<AsyncResult<String>>() {
			
			@Override
			public void handle(AsyncResult<String> res) {
				
				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
			}
		});
	}

	public void set(String key, String value){
		System.out.println("set key : "+key + ", value : " + value);
		client.set(key, value, new Handler<AsyncResult<Void>>() {

			@Override
			public void handle(AsyncResult<Void> res) {

				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
				
			}
		});
	}
    
	public void sadd(String key, String value){
		System.out.println("sadd key : "+ key +", value : " + value);
		client.sadd(key, value, new Handler<AsyncResult<Long>>() {

			@Override
			public void handle(AsyncResult<Long> res) {

				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
				
			}
		});
	}
	
	public void srem(String key, String value){
		client.srem(key, value, new Handler<AsyncResult<Long>>() {

			@Override
			public void handle(AsyncResult<Long> res) {

				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
				
			}
		});
	}
	
	public void smembers(String key){
		client.smembers(key, new Handler<AsyncResult<JsonArray>>() {

			@Override
			public void handle(AsyncResult<JsonArray> res) {

				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
				
			}
		});
	}
	public void keys(String key){
		
		client.keys(key, new Handler<AsyncResult<JsonArray>>() {

			@Override
			public void handle(AsyncResult<JsonArray> res) {
				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
			}
		});
	}
	  public void mgetMany(List<String> keys){
			client.mgetMany(keys, new Handler<AsyncResult<JsonArray>>() {
				
				@Override
				public void handle(AsyncResult<JsonArray> res) {

					if(res.succeeded()){
						success(res.result());
					}else{
						fail(res.cause().getMessage());
					}
				}
			});
		}
	  
	 public void mget(String key){
			client.mget(key, new Handler<AsyncResult<JsonArray>>() {
				
				@Override
				public void handle(AsyncResult<JsonArray> res) {

					if(res.succeeded()){
						success(res.result());
					}else{
						fail(res.cause().getMessage());
					}
				}
			});
	}

	public void del(String key){
		List<String> list = new ArrayList<String>();
		
		list.add(key);
				
		client.del(key, new Handler<AsyncResult<Long>>() {
			
			@Override
			public void handle(AsyncResult<Long> res) {
				if(res.succeeded()){
					success(res.result());
				}else{
					fail(res.cause().getMessage());
				}
				
			}
		});
	}
}
