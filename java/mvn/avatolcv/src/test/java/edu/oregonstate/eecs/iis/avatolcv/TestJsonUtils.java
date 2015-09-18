package edu.oregonstate.eecs.iis.avatolcv;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting.Annotations;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting.JsonUtils;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting.Point;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestJsonUtils extends TestCase {

	public void testStripOffJsonContainerLayer() {
		String json = "{\"results\":{\"sunrise\":\"6:42:25 AM\",\"sunset\":\"6:15:48 PM\"},\"status\":\"OK\"}";
		String result = JsonUtils.stripOffJsonContainerLayer(json);
		Assert.assertEquals("{\"sunrise\":\"6:42:25 AM\",\"sunset\":\"6:15:48 PM\"}", result);
	}
	public void testAnnotationJson(){
		Point p1 = new Point(3.0,4.0);
		Point p2 = new Point(5.0,6.0);
		Point[] pointsA = { p1, p2 };

		Point p3 = new Point(21.0,4.0);
		Point p4 = new Point(34.0,6.0);
		Point[] pointsB = { p3, p4 };
		
		Annotation ann1 = new Annotation();
		ann1.setPoints(pointsA);
		Annotation ann2 = new Annotation();
		ann2.setPoints(pointsB);
		Annotation[] annotations = { ann1, ann2 };
		Annotations anns = new Annotations();
		anns.setAnnotations(annotations);
		ObjectMapper mapper = new ObjectMapper();
	    try {
	    	StringWriter sw = new StringWriter();
	    	mapper.writeValue(sw, anns);
	    	String jsonOut = "" + sw;
        	System.out.println("" + sw);
        	anns = mapper.readValue(jsonOut, Annotations.class);
        	Annotation[] incomingAnnotations = anns.getAnnotations();
        	for (Annotation ann : incomingAnnotations){
        		Point[] ps = ann.getPoints();
        		for (Point p : ps){
        			System.out.println("x " + p.getX() + "  y  " + p.getY());
        		}
        	}
	    }
	    catch(JsonParseException jpe){
	    	System.out.println(jpe.getMessage());
        	jpe.printStackTrace();
	    }
	    catch(JsonMappingException jme){
	    	System.out.println(jme.getMessage());
        	jme.printStackTrace();
	    }
	    catch(IOException ioe){
	    	System.out.println(ioe.getMessage());
        	ioe.printStackTrace();
	    }
	}

}
