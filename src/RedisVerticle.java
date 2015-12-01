import com.soma.ggamtalk.dao.IRedis;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;

import java.util.ArrayList;
import java.util.List;

public class RedisVerticle extends AbstractVerticle {

	RedisClient redisClient;
	
	public void init(){
		
		JsonObject config = new JsonObject();
		config.put("host", "127.0.0.1");
		config.put("auth", "1234567890");
		redisClient = RedisClient.create(vertx, config);
	}
	
	@Override
    public void start() throws Exception {
        init();

        EventBus eb = vertx.eventBus();
        eb.consumer("to.RedisVerticle.keys", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
            	
            	keys(objectMessage);
            }
        });
        eb.consumer("to.RedisVerticle.mgetMany", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
            	
            	mgetMany(objectMessage);
            }
        });
        eb.consumer("to.RedisVerticle.mget", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                mget(objectMessage);
            }
        });
      
        eb.consumer("to.RedisVerticle.del", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                del(objectMessage);
            }
        });
        
        eb.consumer("to.RedisVerticle.set", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                set(objectMessage);
            }
        });

        eb.consumer("to.RedisVerticle.get", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                get(objectMessage);
              }
        });
        
        eb.consumer("to.RedisVerticle.sadd", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
                sadd(objectMessage);
            }
        });

        eb.consumer("to.RedisVerticle.smembers", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
            	System.out.println("요기1");
            	smembers(objectMessage);
              }
        });
        eb.consumer("to.RedisVerticle.srem", new Handler<Message<String>>() {
            @Override
            public void handle(Message<String> objectMessage) {
            	srem(objectMessage);
              }
        });
        
//        eb.consumer("to.RedisVerticle.sismembers", new Handler<Message<String>>() {
//            @Override
//            public void handle(Message<String> objectMessage) {
//            	sismembers(objectMessage);
//              }
//        });
    }
	
	public void mgetMany(Message<String> msg){
		System.out.println("디벅 "+msg.body());
		JsonArray jsonArray = new JsonArray(msg.body());
    	List<String> list = new ArrayList<String>();
    	for (int i=0; i<jsonArray.size(); i++) {
    	    list.add( jsonArray.getString(i) );
    	}
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                    jo.put("result_code", 0);
                    jo.put("result_msg", "mgetMany success");
                    JsonArray ja = (JsonArray)result;
                    if(result!=null && ja.getString(0)!=null && ja.getString(0)!=null)
                    jo.put("result", (JsonArray)result);
                    reply(jo);
			}
		};
		iRedis.mgetMany(list);
	}

	public void mget(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                    jo.put("result_code", 0);
                    jo.put("result_msg", "mget success");
                    jo.put("result", (JsonArray)result);
                    reply(jo);
			}
		};
		iRedis.mget(msg.body());
	}
	public void keys(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                    jo.put("result_code", 0);
                    jo.put("result_msg", "keys success");
                    jo.put("result", (JsonArray)result);
                    reply(jo);
			}
		};
		iRedis.keys(msg.body());
	}
	
	public void del(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                    jo.put("result_code", 0);
                    jo.put("result_msg", "del success");
                    reply(jo);
			}
		};
		iRedis.del(msg.body());
	}
	
	public void set(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                    jo.put("result_code", 0);
                    jo.put("result_msg", "set success");
                    reply(jo);
			}
		};
		JsonObject jo = new JsonObject(msg.body());
		System.out.println("set : "+ msg.body());
		iRedis.set(jo.getString("key"), jo.getString("value"));
	}
	
	public void get(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                if(result == null) {
                	jo.put("result_code", 0);
                	jo.put("result_msg", "해당하는 키에 대한 값이 존재하지 않습니다.");
                    reply(jo);
                }
                else {
                    jo.put("result_code", 0);
                    jo.put("result_msg", "get success");
                    jo.put("result", (String)result);
                    reply(jo);
                }
			}
		};
		iRedis.get(msg.body());
	}
	public void srem(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                if(result == null) {
                	jo.put("result_code", -1);
                	jo.put("result_msg", "smembers fail");
                    reply(jo);
                }
                else {
                    jo.put("result_code", 0);
                    jo.put("result_msg", "smembers success");
                    jo.put("result", (Long)result);
                    reply(jo);
                }
			}
		};
		JsonObject jo = new JsonObject(msg.body());
		iRedis.sadd(jo.getString("key"), jo.getString("value"));
	}
	public void sadd(Message<String> msg){
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				JsonObject jo = new JsonObject();
                if(result == null) {
                	jo.put("result_code", -1);
                	jo.put("result_msg", "sadd fail");
                    reply(jo);
                }
                else {
                    jo.put("result_code", 0);
                    jo.put("result_msg", "sadd success");
                    jo.put("result", (Long)result);
                    reply(jo);
                }
			}
		};
		JsonObject jo = new JsonObject(msg.body());
		System.out.println("sadd key : "+jo.getString("key")+ ", value : " +jo.getString("value"));
		iRedis.sadd(jo.getString("key"), jo.getString("value"));
	}
	
	public void smembers(Message<String> msg){
		System.out.println("요기2");
		IRedis iRedis = new IRedis(redisClient, msg) {
			
			@Override
			public void success(Object result) {
				System.out.println("요기3");
				JsonObject jo = new JsonObject();
                if(result == null) {
                	jo.put("result_code", -1);
                	jo.put("result_msg", "smembers fail");
                    reply(jo);
                }
                else {
                    jo.put("result_code", 0);
                    jo.put("result_msg", "smembers success");
                    jo.put("result", (JsonArray)result);
                    reply(jo);
                }
			}
		};
		iRedis.smembers(msg.body());
	}
	

//	public void sismember(Message<String> msg){
//		
//		IRedis iRedis = new IRedis(redisClient, msg) {
//			
//			@Override
//			public void success(Object result) {
//				JsonObject jo = new JsonObject();
//                if(result == null) {
//                	jo.put("result_code", -1);
//                	jo.put("result_msg", "smembers fail");
//                    reply(jo);
//                }
//                else {
//                    jo.put("result_code", 0);
//                    jo.put("result_msg", "smembers success");
//                    jo.put("result", (JsonArray)result);
//                    reply(jo);
//                }
//			}
//		};
//		JsonObject jo = new JsonObject(msg.body());
//		iRedis.sismembers(jo.getString("key"), jo.getString("value"));
//	}
	
//	public void hmset(String key, String value, RedisCallback redisCallback){
//		
//		
//	}
//	
//	public void hget(String key ){
//		redisClient.hget(, arg1, arg2)
//		
//	}
	
//	public void append(String key, String appendedValue, redisCallback queryCallback){
//		
//		redisClient.append(key, appendedValue, new Handler<AsyncResult<Long>>() {
//
//			@Override
//			public void handle(AsyncResult<Long> res) {
//				
//				if(res.succeeded()){
//					queryCallback.endEvent(res.result());
//				}else{
//					queryCallback.endEvent(res.result());
//				}
//				
//			}
//			
//		});
//	}
	
	
	
	
}
