package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestSegmentationResults extends TestCase {
	public void testSegResults(){
		String path = "C:\\avatol\\git\\avatol_cv\\modules\\segmentation\\yaoSeg\\segOutput";
		try  {
			SegmentationResults sr = new SegmentationResults(path);
			//LEFT OFF HERE
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
				
	}
}
