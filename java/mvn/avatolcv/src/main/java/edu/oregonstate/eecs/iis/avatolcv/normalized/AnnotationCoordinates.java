package edu.oregonstate.eecs.iis.avatolcv.normalized;

import java.util.ArrayList;
import java.util.List;

public class AnnotationCoordinates {
	public enum AnnotationType {
		POINT,
		BOX,
		POLYGON,
		NONE
	}
	private List<PointAsPercent> annotationPoints = new ArrayList<PointAsPercent>();
    public AnnotationCoordinates(String coordString){
    	if (null == coordString){
    		return;
    	}
    	if ("".equals(coordString)){
    		return;
    	}
    	String[] parts = coordString.split(";");
    	for (int i = 0; i < parts.length; i++){
    		String pair = parts[i];
    		String[] pairParts = pair.split(",");
    		String x = pairParts[0];
    		String y = pairParts[1];
    		double xDouble = new Double(x).doubleValue();
    		double yDouble = new Double(y).doubleValue();
    		PointAsPercent p = new PointAsPercent(xDouble, yDouble);
    		annotationPoints.add(p);
    	}
    }
    public List<PointAsPercent> getPoints(){
    	ArrayList<PointAsPercent> result = new ArrayList<PointAsPercent>();
    	result.addAll(this.annotationPoints);
    	return result;
    }
    public int getPointCount(){
    	return annotationPoints.size();
    }
    public AnnotationType getType(){
    	int pointCount = getPointCount();
    	if (pointCount == 0){
    		return AnnotationType.NONE;
    	}
    	else if (pointCount == 1){
    		return AnnotationType.POINT;
    	}
    	else if (pointCount == 2){
    		return AnnotationType.BOX;
    	}
    	else{
    		return AnnotationType.POLYGON;
    	}
    }
}
