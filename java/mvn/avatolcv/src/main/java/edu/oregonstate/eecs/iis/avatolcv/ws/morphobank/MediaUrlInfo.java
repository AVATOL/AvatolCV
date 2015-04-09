package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

public class MediaUrlInfo {
	//{"ok":true,"media":"http:\/\/www.morphobank.org\/media\/morphobank3\/images\/2\/8\/4\/0\/53727_media_files_media_284045_thumbnail.jpg"}
    private String ok;
    private String media;
    
    public void setOk(String s){
		 this.ok = s;
	 }
	 public String getOk(){
		 return this.ok;
	 }
	 public void setMedia(String s){
		 this.media = s;
	 }
	 public String getMedia(){
		 return this.media;
	 }
}
