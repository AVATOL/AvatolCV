package edu.oregonstate.eecs.iis.avatolcv;

import org.junit.Assert;
import org.junit.Test;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankDataException;

public class TestScoredSetMetadata {
	private static final String SEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	
	
	@Test
    public void testSSMLoadAll(){
		try {
			ScoredSetMetadatas ssm = new ScoredSetMetadatas("C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT");
			ssm.loadAll();
		}
		catch(AvatolCVException e){
			Assert.fail(e.getMessage());
		}
		
    	
    }
	@Test
    public void testSSMGetSessionResultsData(){
		try {
			String rootDir = "C:\\avatol\\git\\avatol_cv";
			String bundleRoot = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT";
			ScoredSetMetadatas ssm = new ScoredSetMetadatas(rootDir);
			ssm.loadAll();
			MorphobankBundle mb = new MorphobankBundle(bundleRoot);
			mb.init();
			SessionData sd = ssm.getSessionResultsData(mb);
			if (null != sd){

				boolean foo = sd.canShowImage();
			}
		}
		catch(AvatolCVException e){
			Assert.fail(e.getMessage());
		}
		catch(MorphobankDataException me){
			Assert.fail(me.getMessage());
		}
		
    	
    }
}
