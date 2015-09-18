package edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting;

public class JsonUtils {
	  /**
     * Change a string like 
     * {"results":{"sunrise":"6:42:25 AM","sunset":"6:15:48 PM"},"status":"OK"}
     * to
     * {"sunrise":"6:42:25 AM","sunset":"6:15:48 PM"}

     * @param json
     * @return
     */
    public static String stripOffJsonContainerLayer(String json){
    	json = json.substring(1,json.length() - 1);
    	System.out.println(json);
    	while (!json.substring(0, 1).equals("{") && json.length() > 1){
    		json = json.substring(1,json.length());
    		//System.out.println(json);
    	}
    	while (!json.substring(json.length()-1, json.length()).equals("}") && json.length() > 1){
    		json = json.substring(0,json.length()-1);
    		//System.out.println(json);
    	}
    	return json;
    }
}
