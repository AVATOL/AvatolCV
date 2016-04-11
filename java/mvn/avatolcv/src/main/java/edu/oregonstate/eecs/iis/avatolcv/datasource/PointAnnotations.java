package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.normalized.PointAsPercent;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class PointAnnotations {
    public static final String ANNOTATION_SEQUENCE_DELIMETER = "+";
    public static final char   ANNOTATION_SEQUENCE_DELIMETER_CHARACTER = '+';
    public static final String ANNOTATION_TYPE_DELIMETER = ":";
    public static final char   ANNOTATION_TYPE_DELIMETER_CHARACTER = ':';
    public static final String XY_DELIMETER = "-";
    public static final char   XY_DELIMETER_CHARACTER = '-';
    public static final String POINT_SERIES_DELIMETER = ";";
    public static final char   POINT_SERIES_DELIMETER_CHARACTER = ';';
    public enum AnnotationType {
        POINT,
        BOX,
        POLYGON,
        NONE
    }
    private List<Annotation> annotations = new ArrayList<Annotation>();
    public PointAnnotations(String coordString){
        // FROM MorphobankDataSource
        // avcv_annotation=rectangle:25-45;35-87+point:98-92
        // + delimits the annotations in the series
        // ; delimits the points in the annotation
        // - delimits x and y coordinates
        // : delimits type from points
        if (null == coordString){
            return;
        }
        if ("".equals(coordString)){
            return;
        }
        String[] annotationStrings = ClassicSplitter.splitt(coordString,ANNOTATION_SEQUENCE_DELIMETER_CHARACTER);
        for (int i = 0; i < annotationStrings.length; i++){
            String annotationString = annotationStrings[i];
            Annotation annotation = new Annotation(annotationString);
            annotations.add(annotation);
        }
    }
    public int getAnnotationCount(){
        return annotations.size();
    }
    public List<Annotation> getAnnotations(){
        return annotations;
    }
    public Annotation getFirstSinglePointAnnotation(){
        for (Annotation a : annotations){
            if (a.getType() == AnnotationType.POINT){
                return a;
            }
        }
        return null;
    }
    
    public static String getFirstAnnotation(String coordString){
        if (coordString == null){
            return "";
        }
        if (coordString.equals("")){
            return "";
        }
        String[] coordSequences = ClassicSplitter.splitt(coordString, PointAnnotations.ANNOTATION_SEQUENCE_DELIMETER_CHARACTER);
        return coordSequences[0];
    }
    public class Annotation {
        private AnnotationType type = null;
        private List<PointAsPercent> annotationPoints = new ArrayList<PointAsPercent>();
        public Annotation(String annotationString){
            String[] typeThenPoints = ClassicSplitter.splitt(annotationString,ANNOTATION_TYPE_DELIMETER_CHARACTER);
            String typeString = typeThenPoints[0];
            String typeStringCaps = typeString.toUpperCase();
            this.type = AnnotationType.valueOf(typeStringCaps);
            
            String pointsString = typeThenPoints[1];
            String[] pointsStrings = ClassicSplitter.splitt(pointsString, POINT_SERIES_DELIMETER_CHARACTER);
            for (int j = 0; j < pointsStrings.length ; j++){
                String point = pointsStrings[j];
                String[] pointParts = ClassicSplitter.splitt(point, XY_DELIMETER_CHARACTER);
                String x = pointParts[0];
                String y = pointParts[1];
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
            return this.type;
        }
    }
}
