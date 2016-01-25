package edu.oregonstate.eecs.iis.avatolcv.core;



import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter.FilterItem;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestDataFilter extends TestCase {

    
    public void testBasic(){
        String curDir = System.getProperty("user.dir");
        DataFilter df = new DataFilter(curDir);
        try {
            df.addFilterItem(new NormalizedKey("propC"), new NormalizedValue("c"),true);
            df.addFilterItem(new NormalizedKey("propA"), new NormalizedValue("a"),true);
            df.addFilterItem(new NormalizedKey("propB"), new NormalizedValue("b"),true);
            List<FilterItem> pairs = df.getItems();
            
            assertEquals("a", pairs.get(0).getValue());
            assertEquals("b", pairs.get(1).getValue());
            assertEquals("c", pairs.get(2).getValue());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }

    public void testDouble(){
        String curDir = System.getProperty("user.dir");
        DataFilter df = new DataFilter(curDir);
        try {
            df.addFilterItem(new NormalizedKey("propC"), new NormalizedValue("c"),true);
            df.addFilterItem(new NormalizedKey("propA"), new NormalizedValue("a"),true);
            df.addFilterItem(new NormalizedKey("propA"), new NormalizedValue("a"),true);
            List<FilterItem> pairs = df.getItems();
            Assert.fail("should not thrown exception on redundant value");
        }
        catch(AvatolCVException e){
            Assert.assertTrue(true);
        }
    }
    public void testPersistAndLoad(){
        String curDir = System.getProperty("user.dir");
        DataFilter df = new DataFilter(curDir);
        try {
            df.addFilterItem(new NormalizedKey("propC"), new NormalizedValue("c"),false);
            df.addFilterItem(new NormalizedKey("propA"), new NormalizedValue("a"),true);
            df.addFilterItem(new NormalizedKey("propB"), new NormalizedValue("b"),true);
            List<FilterItem> pairs = df.getItems();
            
            assertEquals("a", pairs.get(0).getValue());
            assertEquals("b", pairs.get(1).getValue());
            assertEquals("c", pairs.get(2).getValue());
            pairs.get(1).setSelected(false);
            df.persist();
            DataFilter df2 = new DataFilter(curDir);
            boolean loaded = df2.load();
            Assert.assertTrue(loaded);;
            List<FilterItem> pairs2 = df2.getItems();
            assertEquals("a", pairs2.get(0).getValue());
            assertEquals("b", pairs2.get(1).getValue());
            assertEquals("c", pairs2.get(2).getValue());
            assertEquals("propA", pairs2.get(0).getKey());
            assertEquals("propB", pairs2.get(1).getKey());
            assertEquals("propC", pairs2.get(2).getKey());
            assertEquals(true,pairs2.get(0).isEditable());
            assertEquals(true,pairs2.get(1).isEditable());
            assertEquals(false,pairs2.get(2).isEditable());
            assertEquals(true, pairs2.get(0).isSelected());
            assertEquals(false, pairs2.get(1).isSelected());
            assertEquals(true, pairs2.get(2).isSelected());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
}
