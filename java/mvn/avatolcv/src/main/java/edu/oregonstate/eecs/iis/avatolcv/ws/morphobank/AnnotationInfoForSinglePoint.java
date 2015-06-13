package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;

public class AnnotationInfoForSinglePoint {		
// NOTE THAT points IS A SINGLE POINT RATHER THAN AN ARRAY
//{"ok":true,"annotations":[{"type":"point","points":{"x":"18.8283475783476","y":"47.4198717948718"}}]}	 private String ok;
	private String ok; 
	private List<MBAnnotationWithSinglePoint> annotations;
	 
	 public void setOk(String s){
		 this.ok = s;
	 }
	 public String getOk(){
		 return this.ok;
	 }
	 public void setAnnotations(List<MBAnnotationWithSinglePoint> s){
		 this.annotations = s;
	 }
	 public List<MBAnnotationWithSinglePoint> getAnnotations(){
		 return this.annotations;
	 }
	 
	 public static class MBAnnotationWithSinglePoint {
		 public static final String POLYGON = "polygon";
		 public static final String RECTANGLE = "rectangle";
		 public static final String POINT = "point";
		 //{"type":"point","points":{"x":"18.8283475783476","y":"47.4198717948718"}}
		 private String type;
		 private MBAnnotationPoint points;
		 
		 public void setType(String s){
			 this.type = s;
		 }
		 public String getType(){
			 return this.type;
		 }
		 
		 public void setPoints(MBAnnotationPoint s){
			 this.points = s;
		 }
		 public MBAnnotationPoint getPoints(){
			 return this.points;
		 }
	 }
	 public static boolean isTypePoint(String s){
		 // look for "type":"point"
		 String matchString = "\"type\":\"point\"";
		 if (s.contains(matchString)){
			 return true;
		 }
		 return false;
	 }
	 /*
	 public static class MBAnnotationPoint {
		 //{"x":"31.891597158772733","y":"19.44466304661473"}
		 private double x;
		 private double y;
		 
		 public void setX(double s){
			 this.x = s;
		 }
		 public double getX(){
			 return this.x;
		 }
		 public void setY(double s){
			 this.y = s;
		 }
		 public double getY(){
			 return this.y;
		 }
	 }
	 */
}   

