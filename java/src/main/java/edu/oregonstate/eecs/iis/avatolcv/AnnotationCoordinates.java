package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

public class AnnotationCoordinates {
	public enum AnnotationType {
		POINT,
		BOX,
		POLYGON
	}
	private List<Point> annotationPoints = new ArrayList<Point>();
    public AnnotationCoordinates(String coordString){
    	String[] parts = coordString.split(";");
    	for (int i = 0; i < parts.length; i++){
    		String pair = parts[i];
    		String[] pairParts = pair.split(",");
    		String x = pairParts[0];
    		String y = pairParts[1];
    		int xInt = new Integer(x).intValue();
    		int yInt = new Integer(y).intValue();
    		Point p = new Point(xInt, yInt);
    		annotationPoints.add(p);
    	}
    }
    public List<Point> getPoints(){
    	ArrayList<Point> result = new ArrayList<Point>();
    	result.addAll(this.annotationPoints);
    	return result;
    }
    public int getPointCount(){
    	return annotationPoints.size();
    }
    public AnnotationType getType(){
    	int pointCount = getPointCount();
    	if (pointCount == 1){
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
