package api;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by lk on 15. 11. 26..
 */
public class Util {

    private static HashMap<String, String> cache;

    public static HashMap<String, String> getCache(){
        if(cache==null)
            cache = new HashMap<String, String>();

        return cache;
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
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
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
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
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
