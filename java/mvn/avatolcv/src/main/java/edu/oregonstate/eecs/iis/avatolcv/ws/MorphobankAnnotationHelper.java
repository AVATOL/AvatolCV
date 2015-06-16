package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForSinglePoint.MBAnnotationWithSinglePoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MBRectangleAnnotation;

public class MorphobankAnnotationHelper {
    public static List<String> splitTypes(String justTypes){
        List<String> results = new ArrayList<String>();
        String result = "";
        while (!("".equals(result = getFirstAnnotationJson(justTypes)))){
            results.add(result);
            int lengthOfFirstAnnotation = result.length();
            int lengthOfEntireString = justTypes.length();
            if (lengthOfFirstAnnotation == lengthOfEntireString){
                justTypes = "";
            }
            else {
                justTypes = justTypes.substring(lengthOfFirstAnnotation + 1, justTypes.length());
            }
        }
        return results;
    }
    public static String getFirstAnnotationJson(String s){
        if ("".equals(s)){
            return "";
        }
        int index = 0;
        char nextChar = s.charAt(index);
        while (nextChar != '{'){
            index++; 
            nextChar = s.charAt(index);
        }
        int openBraceIndex = index;
        int closeBraceToIgnoreCount = 0;
        boolean encounteredInnerClosedBrace = false;
        while (nextChar != '}' || closeBraceToIgnoreCount > 0 || encounteredInnerClosedBrace ){
            encounteredInnerClosedBrace = false;
            index++; 
            nextChar = s.charAt(index);
            if (closeBraceToIgnoreCount > 0){
                if (nextChar == '}'){
                    closeBraceToIgnoreCount--;
                    encounteredInnerClosedBrace = true;
                }
            }
            if (nextChar == '{'){
                closeBraceToIgnoreCount++;
            }
        }
        int closeBraceIndex = index;
        int endIndex = closeBraceIndex + 1;
        String result = s.substring(openBraceIndex, endIndex);
        return result;
    }
    public static String getJustTypes(String full){
        int indexOfOpeningSquareBracket = full.indexOf("[");
        int startTrimIndex = indexOfOpeningSquareBracket + 1;
        int endTrimIndex = full.lastIndexOf("]");
        String withEndTrimmed = full.substring(0, endTrimIndex);
        String withStartTrimmed = withEndTrimmed.substring(startTrimIndex, withEndTrimmed.length());
        return withStartTrimmed;
    }
    
    public static MBAnnotation getMBAnnotationForSinglePointAnnotation(String json) throws MorphobankWSException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MBAnnotationWithSinglePoint asp = mapper.readValue(json, MBAnnotationWithSinglePoint.class);
            MBAnnotationPoint ap = asp.getPoints(); // it's a single point
            MBAnnotation annotationOfCorrectForm = new MBAnnotation();
            annotationOfCorrectForm.setType(asp.getType());
            List<MBAnnotationPoint> annotationPoints = new ArrayList<MBAnnotationPoint>();
            annotationPoints.add(ap);
            annotationOfCorrectForm.setPoints(annotationPoints);// we add it into the new object (of the correct list-bearing form) as a list
            return annotationOfCorrectForm;
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json to MBAnnotationWithSinglePoint " + json);
        }
        catch(JsonParseException jme){
            throw new MorphobankWSException("problem parsing json to MBAnnotationWithSinglePoint " + json);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("problem converting json to MBAnnotationWithSinglePoint " + json);
        }
    }
    
   
        
    public static MBAnnotation getMBAnnotationForRectangleAnnotation(String json) throws MorphobankWSException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MBRectangleAnnotation rect = mapper.readValue(json, MBRectangleAnnotation.class);
            MBAnnotationPoint p = rect.getPoints();
            double x = p.getX();
            double y = p.getY();
            double width = rect.getW();
            double height = rect.getH();
            double x2 = x + width;
            double y2 = y + height;
            
            MBAnnotationPoint ap = new MBAnnotationPoint();
            ap.setX(x2);
            ap.setY(y2);
           
            MBAnnotation annotationOfCorrectForm = new MBAnnotation();
            annotationOfCorrectForm.setType(rect.getType());
            List<MBAnnotationPoint> annotationPoints = new ArrayList<MBAnnotationPoint>();
            annotationPoints.add(p);
            annotationPoints.add(ap);
            annotationOfCorrectForm.setPoints(annotationPoints);// we add it into the new object    
            return annotationOfCorrectForm;
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json to MBAnnotationWithRectangle " + json);
        }
        catch(JsonParseException jme){
            throw new MorphobankWSException("problem parsing json to MBAnnotationWithRectangle " + json);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("problem converting json to MBAnnotationWithRectangle " + json);
        }
    }    
    public static MBAnnotation getMBAnnotationForPolygonAnnotation(String json) throws MorphobankWSException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            MBAnnotation a = mapper.readValue(json, MBAnnotation.class);
            return a;
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json to MBAnnotationWithRectangle " + json);
        }
        catch(JsonParseException jme){
            throw new MorphobankWSException("problem parsing json to MBAnnotationWithRectangle " + json);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("problem converting json to MBAnnotationWithRectangle " + json);
        }
    }
}

















