package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.ArrayList;
import java.util.List;

public class CellMediaInfo {
// {"ok":true,"media":[{"mediaID":"284045","viewID":"6282"}]}
	private String ok;
	private List<MBMediaInfo> media;
	
	public void setOk(String s){
		this.ok = s;
	}
	public String getOk(){
		return this.ok;
	}
	public void setMedia(List<MBMediaInfo> s){
		this.media = s;
	}
	public List<MBMediaInfo> getMedia(){
		return this.media;
	}
	
	public static class MBMediaInfo {
	    public static String IMAGE_SIZE_THUMBNAIL = "thumbnail";
	    public static String IMAGE_SIZE_SMALL    = "small";
	    public static String IMAGE_SIZE_LARGE     = "large";
		private String mediaID;
		private String viewID;
		
		public void setMediaID(String s){
			this.mediaID = s;
		}
		public String getMediaID(){
			return this.mediaID;
		}
		public void setViewID(String s){
			this.viewID = s;
		}
		public String getViewID(){
			return this.viewID;
		}
		public static List<String> getMediaTypes(){
		    List<String> list = new ArrayList<String>();
		    list.add(IMAGE_SIZE_THUMBNAIL);
            list.add(IMAGE_SIZE_SMALL);
            list.add(IMAGE_SIZE_LARGE);
		    return list;    
		}
		public String getName(){
		    return ""; // morphobank images don't have names with them
		}
	}
}
