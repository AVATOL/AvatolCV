package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import junit.framework.TestCase;

public class BisqueUploadTest extends TestCase {
    /*
    public void testUploadAdd(){
        BisqueWSClientImpl bisque = new BisqueWSClientImpl();
        try {
            AvatolCVFileSystem.setRootDir("C:\\jed\\avatol\\git\\avatol_cv");
            bisque.authenticate("jedirv","Neton3plants**");
            bisque.addNewAnnotation("00-b7itcHVfYEEaBiMXFsibVS", "cuteness", "infinite");
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            System.out.println(ace.getMessage());
        }
        catch(BisqueWSException ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
    */
    
    public void testUploadRevise(){
        BisqueWSClientImpl bisque = new BisqueWSClientImpl();
        try {
            AvatolCVFileSystem.setRootDir("C:\\jed\\avatol\\git\\avatol_cv");
            bisque.authenticate("jedirv","Neton3plants**");
            //bisque.reviseAnnotation("00-XUMRpWGerqJMpvZndSQfpR", "leaf apex angle", "acute"); <- won't work cause Ramona owns that tag
            bisque.reviseAnnotation("00-b7itcHVfYEEaBiMXFsibVS", "cuteness", "infinite");
        }
        catch(AvatolCVException ace){
            ace.printStackTrace();
            System.out.println(ace.getMessage());
        }
        catch(BisqueWSException ex){
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
    
   
}
