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

}
