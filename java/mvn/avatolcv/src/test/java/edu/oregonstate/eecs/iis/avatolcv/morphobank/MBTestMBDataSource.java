package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;
import edu.oregonstate.eecs.iis.avatolcv.datasource.MorphobankDataSource;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
public class MBTestMBDataSource extends TestCase {

    public void testGetAnnotationsValueStringNoPoints() {
        MBAnnotation a = new MBAnnotation();
        a.setType("type1");
        List<MBAnnotationPoint> points = new ArrayList<MBAnnotationPoint>();
        a.setPoints(points);
        String result =  MorphobankDataSource.getAnnotationValueStringForAnnotation(a);
        Assert.assertEquals("",result);
    }

    public void testGetAnnotationsValueString1Point() {
        MBAnnotation a = new MBAnnotation();
        a.setType("point");
        List<MBAnnotationPoint> points = new ArrayList<MBAnnotationPoint>();
        MBAnnotationPoint p = new MBAnnotationPoint();
        p.setX(0.0);
        p.setY(1.0);
        points.add(p);
        a.setPoints(points);
        String result =  MorphobankDataSource.getAnnotationValueStringForAnnotation(a);
        Assert.assertEquals("point:0.0,1.0",result);
    }

    public void testGetAnnotationsValueString2Point() {
        MBAnnotation a = new MBAnnotation();
        a.setType("point");
        List<MBAnnotationPoint> points = new ArrayList<MBAnnotationPoint>();
        MBAnnotationPoint p = new MBAnnotationPoint();
        p.setX(0.0);
        p.setY(1.0);
        points.add(p);
        MBAnnotationPoint p2 = new MBAnnotationPoint();
        p2.setX(3.3);
        p2.setY(4.4);
        points.add(p2);
        a.setPoints(points);
        String result =  MorphobankDataSource.getAnnotationValueStringForAnnotation(a);
        Assert.assertEquals("point:0.0,1.0;3.3,4.4",result);
    }
    public void testGetAnnotationsValueString() {
        MBAnnotation a = new MBAnnotation();
        a.setType("rectangle");
        List<MBAnnotationPoint> points = new ArrayList<MBAnnotationPoint>();
        MBAnnotationPoint p = new MBAnnotationPoint();
        p.setX(0.0);
        p.setY(1.0);
        points.add(p);
        MBAnnotationPoint p2 = new MBAnnotationPoint();
        p2.setX(3.3);
        p2.setY(4.4);
        points.add(p2);
        a.setPoints(points);
        
        MBAnnotation a2 = new MBAnnotation();
        a2.setType("point");
        List<MBAnnotationPoint> points2 = new ArrayList<MBAnnotationPoint>();
        MBAnnotationPoint p3 = new MBAnnotationPoint();
        p3.setX(5.5);
        p3.setY(6.6);
        points2.add(p3);
        a2.setPoints(points2);
        List<MBAnnotation> annotations = new ArrayList<MBAnnotation>();
        annotations.add(a);
        annotations.add(a2);
        String result =  MorphobankDataSource.getAnnotationsValueString(annotations);
        Assert.assertEquals("rectangle:0.0,1.0;3.3,4.4+point:5.5,6.6",result);
    }
    public void testGetAnnotationsValueStringNoAnnotations() {
        
        List<MBAnnotation> annotations = new ArrayList<MBAnnotation>();
        String result =  MorphobankDataSource.getAnnotationsValueString(annotations);
        Assert.assertEquals("",result);
    }
}
