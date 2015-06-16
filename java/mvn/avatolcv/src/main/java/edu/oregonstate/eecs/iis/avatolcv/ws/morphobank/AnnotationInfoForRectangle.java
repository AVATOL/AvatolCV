package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;


public class AnnotationInfoForRectangle {
    
     //    {"ok":true,"annotations":[{"type":"rectangle","points":{"x":"61.58982285141206","y":"54.05519039672426"},"w":"1.3974566456743513","h":"2.312840627811468"}]}   
     private String ok;
     private List<MBAnnotationWithRectangle> annotations;
     
     public void setOk(String s){
         this.ok = s;
     }
     public String getOk(){
         return this.ok;
     }
     public void setAnnotations(List<MBAnnotationWithRectangle> s){
         this.annotations = s;
     }
     public List<MBAnnotationWithRectangle> getAnnotations(){
         return this.annotations;
     }
     
     public static class MBAnnotationWithRectangle {
         //{"type":"rectangle","points":{"x":"61.58982285141206","y":"54.05519039672426"},"w":"1.3974566456743513","h":"2.312840627811468"}
         private String type;
         private RectangleAnnotation points;
         
         public void setType(String s){
             this.type = s;
         }
         public String getType(){
             return this.type;
         }
         
         public void setPoints(RectangleAnnotation s){
             this.points = s;
         }
         public RectangleAnnotation getPoints(){
             return this.points;
         }
     }
     
     public static class RectangleAnnotation {
         //{"x":"61.58982285141206","y":"54.05519039672426"},"w":"1.3974566456743513","h":"2.312840627811468"
         // which has been converted to this:
         //"x":"61.58982285141206","y":"54.05519039672426","w":"1.3974566456743513","h":"2.312840627811468"
         private double x;
         private double y;
         private double w;
         private double h;
         
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
         public void setW(double s){
             this.w = s;
         }
         public double getW(){
             return this.w;
         }
         public void setH(double s){
             this.h = s;
         }
         public double getH(){
             return this.h;
         }
     }
     public static boolean isTypeRectangle(String s){
         // look for "type":"rectangle"
         String matchString = "\"type\":\"" + MBAnnotation.RECTANGLE + "\"";
         if (s.contains(matchString)){
             return true;
         }
         return false;
     }
     public static String hackAwayCurlyBracesThatComplicateMapping(String s){
         //convert this:   {"ok":true,"annotations":[{"type":"rectangle","points":{"x":"61.58982285141206","y":"54.05519039672426"},"w":"1.3974566456743513","h":"2.312840627811468"}]}   
         // ...to this:  {"ok":true,"annotations":[{"type":"rectangle","points":"x":"61.58982285141206","y":"54.05519039672426","w":"1.3974566456743513","h":"2.312840627811468"}]}   
         String firstMatch = "points\":{\"x\"";
         String firstReplace = "points\":\"x\"";
         String result1 = s.replace(firstMatch, firstReplace);
         String secondMatch = "},\"w\":";
         String secondReplace = ",\"w\":";
         String result2 = s.replace(secondMatch, secondReplace);
         return result2;
     }
}   