package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestAvatolCVFileSystem extends TestCase {

    public void testGetNextIDForDate() {
        List<String> ids = new ArrayList<String>();
        String date = "20150831";
        Assert.assertEquals("20150831_01", AvatolCVFileSystem.getNextIDForDate(date, ids));
        ids.add("20150831_01");
        Assert.assertEquals("20150831_02", AvatolCVFileSystem.getNextIDForDate(date, ids));
        ids.add("20150830_01");
        Assert.assertEquals("20150831_02", AvatolCVFileSystem.getNextIDForDate(date, ids));
        ids.add("20150831_05");
        Assert.assertEquals("20150831_06", AvatolCVFileSystem.getNextIDForDate(date, ids));
    }
    public void testGetNextIDForDateThreeDigit(){
    	List<String> ids = new ArrayList<String>();
        String date = "20150831";
        for (int i = 1; i < 100; i++){
        	String id = "20150831_" + String.format("%02d", i);
        	ids.add(id);
        }
        Assert.assertEquals("20150831_100", AvatolCVFileSystem.getNextIDForDate(date, ids));
    }
    public void  testGetMostRecentIDForDate(){
        String dateString = "20151124";
        List<String> ids = new ArrayList<String>();
        ids.add("20151124_01");
        ids.add("20151124_02");
        ids.add("20151124_03");
        Assert.assertEquals(AvatolCVFileSystem.getMostRecentIDForDate(dateString, ids),"20151124_03");
        ids = new ArrayList<String>();
        Assert.assertEquals(AvatolCVFileSystem.getMostRecentIDForDate(dateString, ids),"20151124_01");
    }
    public void  testNextIDForDate(){
        String dateString = "20151124";
        List<String> ids = new ArrayList<String>();
        ids.add("20151124_01");
        ids.add("20151124_02");
        ids.add("20151124_03");
        Assert.assertEquals(AvatolCVFileSystem.getNextIDForDate(dateString, ids),"20151124_04");
        ids = new ArrayList<String>();
        Assert.assertEquals(AvatolCVFileSystem.getNextIDForDate(dateString, ids),"20151124_01");
    }
}
