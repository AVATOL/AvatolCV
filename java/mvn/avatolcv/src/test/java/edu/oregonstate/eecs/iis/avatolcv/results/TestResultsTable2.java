package edu.oregonstate.eecs.iis.avatolcv.results;

import javafx.scene.Node;
import javafx.scene.control.Label;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestResultsTable2 extends TestCase {

    public void testTable(){
       
        
        try {
            ResultsTableSortable rt = new ResultsTableSortable();
            // column A
            rt.addValueForColumn("image1","colA","valA1");
            rt.addValueForColumn("image2","colA","valA2");
            rt.addValueForColumn("image3","colA","valA3");
            
            Node objA1 = new Label();
            Node objA2 = new Label();
            Node objA3 = new Label();
            rt.addWidgetForColumn("image1","colA",objA1);
            rt.addWidgetForColumn("image2","colA",objA2);
            rt.addWidgetForColumn("image3","colA",objA3);
            
            // column B
            rt.addValueForColumn("image1","colB","valBz");
            rt.addValueForColumn("image2","colB","valBy");
            rt.addValueForColumn("image3","colB","valBx");
            
            Node objBz = new Label();
            Node objBy = new Label();
            Node objBx = new Label();
            rt.addWidgetForColumn("image1","colB",objBz);
            rt.addWidgetForColumn("image2","colB",objBy);
            rt.addWidgetForColumn("image3","colB",objBx);
            
            // column C
            rt.addValueForColumn("image1","colC","valCaa");
            rt.addValueForColumn("image2","colC","valCcc");
            rt.addValueForColumn("image3","colC","valCbb");
            
            Node objCaa = new Label();
            Node objCcc = new Label();
            Node objCbb = new Label();
            rt.addWidgetForColumn("image1","colC",objCaa);
            rt.addWidgetForColumn("image2","colC",objCcc);
            rt.addWidgetForColumn("image3","colC",objCbb);
            rt.sortOnColumn("colB");
            Assert.assertEquals("image3", rt.getImageIDsInCurrentOrder().get(0));
            Assert.assertEquals("image2", rt.getImageIDsInCurrentOrder().get(1));
            Assert.assertEquals("image1", rt.getImageIDsInCurrentOrder().get(2));
            
            Assert.assertEquals("valA1", rt.getValue("image1","colA"));
            Assert.assertEquals("valA2", rt.getValue("image2","colA"));
            Assert.assertEquals("valA3", rt.getValue("image3","colA"));

            Assert.assertEquals("valBz", rt.getValue("image1","colB"));
            Assert.assertEquals("valBy", rt.getValue("image2","colB"));
            Assert.assertEquals("valBx", rt.getValue("image3","colB"));

            Assert.assertEquals("valCaa", rt.getValue("image1","colC"));
            Assert.assertEquals("valCcc", rt.getValue("image2","colC"));
            Assert.assertEquals("valCbb", rt.getValue("image3","colC"));
            

            rt.sortOnColumn("colC");
            Assert.assertEquals("image1", rt.getImageIDsInCurrentOrder().get(0));
            Assert.assertEquals("image3", rt.getImageIDsInCurrentOrder().get(1));
            Assert.assertEquals("image2", rt.getImageIDsInCurrentOrder().get(2));
            
            Assert.assertEquals("valA1", rt.getValue("image1","colA"));
            Assert.assertEquals("valA2", rt.getValue("image2","colA"));
            Assert.assertEquals("valA3", rt.getValue("image3","colA"));

            Assert.assertEquals("valBz", rt.getValue("image1","colB"));
            Assert.assertEquals("valBy", rt.getValue("image2","colB"));
            Assert.assertEquals("valBx", rt.getValue("image3","colB"));

            Assert.assertEquals("valCaa", rt.getValue("image1","colC"));
            Assert.assertEquals("valCcc", rt.getValue("image2","colC"));
            Assert.assertEquals("valCbb", rt.getValue("image3","colC"));

            rt.sortOnColumn("colA");
            Assert.assertEquals("image1", rt.getImageIDsInCurrentOrder().get(0));
            Assert.assertEquals("image2", rt.getImageIDsInCurrentOrder().get(1));
            Assert.assertEquals("image3", rt.getImageIDsInCurrentOrder().get(2));
            
            Assert.assertEquals("valA1", rt.getValue("image1","colA"));
            Assert.assertEquals("valA2", rt.getValue("image2","colA"));
            Assert.assertEquals("valA3", rt.getValue("image3","colA"));

            Assert.assertEquals("valBz", rt.getValue("image1","colB"));
            Assert.assertEquals("valBy", rt.getValue("image2","colB"));
            Assert.assertEquals("valBx", rt.getValue("image3","colB"));

            Assert.assertEquals("valCaa", rt.getValue("image1","colC"));
            Assert.assertEquals("valCcc", rt.getValue("image2","colC"));
            Assert.assertEquals("valCbb", rt.getValue("image3","colC"));
        }
        catch(AvatolCVException ace){
            Assert.fail(ace.getMessage());
        }
        
        
        
    }
}
