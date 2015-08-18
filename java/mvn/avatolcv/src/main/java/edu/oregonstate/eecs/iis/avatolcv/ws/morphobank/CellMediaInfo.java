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
	    public static final String VIEW_ID_NOT_SPECIFIED = "viewIdNotSpecified";
	    public static String IMAGE_SIZE_THUMBNAIL = "thumbnail";
	    public static String IMAGE_SIZE_SMALL    = "small";
	    public static String IMAGE_SIZE_LARGE     = "large";
		private String mediaID = null;
		private String viewID = null;
		
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
		public boolean isValid(){
		    if (null == this.viewID){
		        return false;
		    }
		    if (null == this.mediaID){
		        return false;
		    }
		    return true;
		}
		public String getReasonForBeingInvalid(){
		    if (null == this.viewID){
                return "view not set";
            }
            if (null == this.mediaID){
                return "media not set";
            }
            return "";
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
