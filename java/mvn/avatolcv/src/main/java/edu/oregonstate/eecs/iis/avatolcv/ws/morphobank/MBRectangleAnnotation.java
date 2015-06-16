package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;

public class MBRectangleAnnotation {
  //{"type":"rectangle","points":{"x":"61.58982285141206","y":"54.05519039672426"},"w":"1.3974566456743513","h":"2.312840627811468"}
    private String type;
    private MBAnnotationPoint points;
    private double w;
    private double h;
    
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

    public static boolean isTypeRectangle(String s){
        // look for "type":"rectangle"
        String matchString = "\"type\":\"" + MBAnnotation.RECTANGLE + "\"";
        if (s.contains(matchString)){
            return true;
        }
        return false;
    }
}
