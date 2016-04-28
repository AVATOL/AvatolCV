package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSession.UploadEvent;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import junit.framework.Assert;
import junit.framework.TestCase;

public class UploadSessionTest extends TestCase {
    protected void setUp(){
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
            DatasetInfo di = new DatasetInfo();
            di.setName("unitTest");
            di.setID("xyz");
            di.setProjectID("abc");
            AvatolCVFileSystem.setDatasourceName("local");
            AvatolCVFileSystem.setSessionID("testSession");
            AvatolCVFileSystem.setChosenDataset(di);
        }
        catch(AvatolCVException ace){
            Assert.fail("Proplem initializing AvatolCVFileSystem " + ace.getMessage());
        }
    }
    public void testUploadSession1deep(){
        try {
            UploadSession us = new UploadSession("runX");
            us.nextSession();
            Assert.assertEquals(1, us.getUploadSessionNumber());
            us.addNewKeyValue("image1", new NormalizedKey("charx"), new NormalizedValue("val1"), null, null);
            us.addNewKeyValue("image2", new NormalizedKey("charx"), new NormalizedValue("val2"), null, null);
            us.reviseValueForKey("image3", new NormalizedKey("charx"), new NormalizedValue("val3"), new NormalizedValue("val1"), null, null);
            us.persist();
            List<UploadEvent> undoEvents = us.getEventsForUndo();
            Assert.assertEquals(3, undoEvents.size());
            Assert.assertEquals("image1", undoEvents.get(0).getImageID());
            Assert.assertEquals("image2", undoEvents.get(1).getImageID());
            Assert.assertEquals("image3", undoEvents.get(2).getImageID());
            String logPath = AvatolCVFileSystem.getPathForUploadSessionFile("runX");
            File f = new File(logPath);
            Assert.assertTrue(f.exists());
            us.forgetEvents(undoEvents);
            Assert.assertEquals(0, us.getUploadSessionNumber());
            Assert.assertFalse(f.exists());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
    public void testUploadSession2deep(){
        try {
            UploadSession us = new UploadSession("runY");
            us.nextSession(); 
            Assert.assertEquals(1, us.getUploadSessionNumber());
            us.addNewKeyValue("image1", new NormalizedKey("charx"), new NormalizedValue("val1"), null, null);
            us.addNewKeyValue("image2", new NormalizedKey("charx"), new NormalizedValue("val2"), null, null);
            us.reviseValueForKey("image3", new NormalizedKey("charx"), new NormalizedValue("val3"), new NormalizedValue("val1"), null, null);
            us.persist();
            
            us.nextSession();
            Assert.assertEquals(2, us.getUploadSessionNumber());
            us.addNewKeyValue("image1", new NormalizedKey("charx"), new NormalizedValue("val1"), null, null);
            us.addNewKeyValue("image2", new NormalizedKey("charx"), new NormalizedValue("val2"), null, null);
            us.reviseValueForKey("image3", new NormalizedKey("charx"), new NormalizedValue("val1"), new NormalizedValue("val3"), null, null);
            us.addNewKeyValue("image4", new NormalizedKey("charx"), new NormalizedValue("val4"), null, null);
            us.persist();
            
            List<UploadEvent> undoEvents = us.getEventsForUndo();
            Assert.assertEquals(4, undoEvents.size());
            Assert.assertEquals("image1", undoEvents.get(0).getImageID());
            Assert.assertEquals("image2", undoEvents.get(1).getImageID());
            Assert.assertEquals("image3", undoEvents.get(2).getImageID());
            Assert.assertEquals("image4", undoEvents.get(3).getImageID());
            Assert.assertEquals("val4",  undoEvents.get(3).getVal().getName());
            String logPath = AvatolCVFileSystem.getPathForUploadSessionFile("runY");
            File f = new File(logPath);
            Assert.assertTrue(f.exists());
            us.forgetEvents(undoEvents);
            Assert.assertEquals(1, us.getUploadSessionNumber());
            Assert.assertTrue(f.exists()); // should still be there
            
            undoEvents = us.getEventsForUndo();
            Assert.assertEquals(3, undoEvents.size());
            Assert.assertEquals("image1", undoEvents.get(0).getImageID());
            Assert.assertEquals("image2", undoEvents.get(1).getImageID());
            Assert.assertEquals("image3", undoEvents.get(2).getImageID());
            us.forgetEvents(undoEvents);
            Assert.assertEquals(0, us.getUploadSessionNumber());
            Assert.assertFalse(f.exists());
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
    }
}
