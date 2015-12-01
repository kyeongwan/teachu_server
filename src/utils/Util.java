package utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Util {
	public static Logger logger = LoggerFactory.getLogger("RestVerticle");

	private static HashMap<String, String> cache;
	
	private static HashMap<String, long[]> trafficCache = new HashMap<String,long[]>();
	
	static Calendar calendar;
	static SimpleDateFormat dateFormat = new SimpleDateFormat("HH");
	static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMddHH");
	
	public static void countConsumer(String consumerName, EventBus eb){
		calendar = Calendar.getInstance();
		long[] a= trafficCache.get(consumerName);
		System.out.println("countConsumer : "+ consumerName +"//"+ Long.parseLong(dateFormat.format(calendar.getTime()))+"//");
		if(a==null){
			System.out.println("countConsumer1 : "+ consumerName +"//"+ Long.parseLong(dateFormat.format(calendar.getTime()))+"//");
			
			long[] b = { 1, Long.parseLong(dateFormat.format(calendar.getTime()))};
			trafficCache.put(consumerName,b );
			return;
		}
		
		Long count = a[0];
		Long time = a[1];
		System.out.println("countConsumer2 : "+ consumerName +"//"+ Long.parseLong(dateFormat.format(calendar.getTime()))+"//"+ Long.toString(time)+"//"+ Long.toString(count));
		
		//a[1] 은 시간 a[0] 은 카운
		if(time!= Long.parseLong(dateFormat.format(calendar.getTime()))){
			//Redis저장 후 시간 및 카운트 갱신
			System.out.println("traffic counting!!!! ");
		
			eb.send("to.RedisVerticle.get", "traffic:"+consumerName, new Handler<AsyncResult<Message<JsonObject>>>() {

				@Override
				public void handle(AsyncResult<Message<JsonObject>> res) {
					JsonArray ja;
					if(!res.result().body().containsKey("result")||res.result().body().getString("result")==null||res.result().body().getString("result").length()<5)	
						ja = new JsonArray();
					else
						ja = new JsonArray(res.result().body().getString("result"));
					JsonObject jo = new JsonObject();
					jo.put("date", dateFormat2.format(calendar.getTime()));
					jo.put("count", count);
					ja.add(jo);

					JsonObject table = new JsonObject();
					table.put("key", "traffic:"+consumerName);
					table.put("value", ja.toString());
					
					eb.send("to.RedisVerticle.set", table.toString(), new Handler<AsyncResult<Message<JsonObject>>>() {

						@Override
						public void handle(AsyncResult<Message<JsonObject>> res) { 
						
						}
					});
					
				}
			});
			
			a[1]= Long.parseLong(dateFormat.format(calendar.getTime()));
			a[0]=1;
			
		}else{
			a[0]++;
		}
		
	}
	
	public static HashMap<String, String> getCache(){
		if(cache==null)
			cache = new HashMap<String, String>();
		
		return cache;
	}
	
	public static void logDebug(String text){
	
		logger.debug(text);
	
	}

	public static String getUserId(String token){
		
		String[] user_id = token.split(":");
		
		return user_id[1];
	}
	
	public static String getChannelId(String channel_name){
		
		String a = channel_name + Long.toString(System.currentTimeMillis());
		String SHA = "";
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(a.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
	
	public static String getToken(String user_id){
		String a = user_id+ "ggamtalk" + Long.toString(System.currentTimeMillis());
		String SHA = "";
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(a.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		SHA += ":"+user_id;
		return SHA;
	}
}

