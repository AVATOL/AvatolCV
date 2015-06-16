package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForRectangle;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForSinglePoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForRectangle.MBAnnotationWithRectangle;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForRectangle.RectangleAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForSinglePoint.MBAnnotationWithSinglePoint;

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
    
    public static MBAnnotation getMBAnnotationForSinglePointAnnotation(String json) throws AvatolCVException {
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
            throw new AvatolCVException("problem mapping json to MBAnnotationWithSinglePoint " + json);
        }
        catch(JsonParseException jme){
            throw new AvatolCVException("problem parsing json to MBAnnotationWithSinglePoint " + json);
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem converting json to MBAnnotationWithSinglePoint " + json);
        }
    }
    
    public static MBAnnotation getMBAnnotationForRectangleAnnotation(String json) throws AvatolCVException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = AnnotationInfoForRectangle.hackAwayCurlyBracesThatComplicateMapping(json);
            MBAnnotationWithRectangle awr = mapper.readValue(json, MBAnnotationWithRectangle.class);
            
            RectangleAnnotation ra = awr.getPoints();
            double x = ra.getX();
            double y = ra.getY();
            double width = ra.getW();
            double height = ra.getH();
            double x2 = x + width;
            double y2 = y + height;
            MBAnnotationPoint ap1 = new MBAnnotationPoint();
            ap1.setX(x);
            ap1.setY(y);
            MBAnnotationPoint ap2 = new MBAnnotationPoint();
            ap1.setX(x2);
            ap1.setY(y2);
           
            MBAnnotation annotationOfCorrectForm = new MBAnnotation();
            annotationOfCorrectForm.setType(awr.getType());
            List<MBAnnotationPoint> annotationPoints = new ArrayList<MBAnnotationPoint>();
            annotationPoints.add(ap1);
            annotationPoints.add(ap2);
            annotationOfCorrectForm.setPoints(annotationPoints);// we add it into the new object    
            return annotationOfCorrectForm;
        }
        catch(JsonMappingException jme){
            throw new AvatolCVException("problem mapping json to MBAnnotationWithRectangle " + json);
        }
        catch(JsonParseException jme){
            throw new AvatolCVException("problem parsing json to MBAnnotationWithRectangle " + json);
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem converting json to MBAnnotationWithRectangle " + json);
        }
    }    
        
    
    public static String getMBAnnotationForPolygonAnnotation(String json){
        
    }
}
