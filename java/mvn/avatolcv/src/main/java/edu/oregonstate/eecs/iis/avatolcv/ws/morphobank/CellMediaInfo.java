package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

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
	}
}
