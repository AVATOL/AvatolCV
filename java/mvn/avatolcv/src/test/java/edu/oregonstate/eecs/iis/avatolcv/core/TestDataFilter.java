package edu.oregonstate.eecs.iis.avatolcv.core;



import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter.Pair;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestDataFilter extends TestCase {

    
    public void testBasic(){
        String curDir = System.getProperty("user.dir");
        DataFilter df = new DataFilter(curDir);
        try {
            df.addPropertyValue("propC", "c", "someID", true);
            df.addPropertyValue("propA", "a", "someID",true);
            df.addPropertyValue("propB", "b", "someID",true);
            List<Pair> pairs = df.getItems();
            
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
            df.addPropertyValue("propC", "c", "someID",true);
            df.addPropertyValue("propA", "a", "someID",true);
            df.addPropertyValue("propA", "a", "someID",true);
            List<Pair> pairs = df.getItems();
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
            df.addPropertyValue("propC", "c", "someID",false);
            df.addPropertyValue("propA", "a", "someID",true);
            df.addPropertyValue("propB", "b", "someID",true);
            List<Pair> pairs = df.getItems();
            
            assertEquals("a", pairs.get(0).getValue());
            assertEquals("b", pairs.get(1).getValue());
            assertEquals("c", pairs.get(2).getValue());
            pairs.get(1).setSelected(false);
            df.persist();
            DataFilter df2 = new DataFilter(curDir);
            boolean loaded = df2.load();
            Assert.assertTrue(loaded);;
            List<Pair> pairs2 = df2.getItems();
            assertEquals("a", pairs2.get(0).getValue());
            assertEquals("b", pairs2.get(1).getValue());
            assertEquals("c", pairs2.get(2).getValue());
            assertEquals("propA", pairs2.get(0).getName());
            assertEquals("propB", pairs2.get(1).getName());
            assertEquals("propC", pairs2.get(2).getName());
            assertEquals(true,pairs2.get(0).isEditable());
            assertEquals(true,pairs2.get(1).isEditable());
            assertEquals(false,pairs2.get(2).isEditable());
            assertEquals("propC_c", pairs2.get(2).getID());
            assertEquals(true, pairs2.get(0).isSelected());
            assertEquals(false, pairs2.get(1).isSelected());
            assertEquals(true, pairs2.get(2).isSelected());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
}
