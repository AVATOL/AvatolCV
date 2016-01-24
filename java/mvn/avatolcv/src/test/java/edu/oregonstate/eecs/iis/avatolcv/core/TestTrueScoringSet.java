package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TrueScoringSet;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestTrueScoringSet extends TestCase {
	public List<NormalizedImageInfo> getNiis(int count, String key, String val) throws AvatolCVException {
		List<NormalizedImageInfo> niis = new ArrayList<NormalizedImageInfo>();
		for (int i = 0 ; i < count ; i++){
			List<String> lines = new ArrayList<String>();
			lines.add(key + "=" + val);
			NormalizedImageInfo nii = new NormalizedImageInfo(lines, "image" + i, "path" + i);
			niis.add(nii);
		}
		return niis;
	}
	public void testTrueScoringSetAllScored(){
		try {
			// ten images, all scored, so all can participate in split
			List<NormalizedImageInfo> niis = getNiis(10,"key1","value1");
			try {
				TrueScoringSet tss = new TrueScoringSet(niis, new NormalizedKey("key1"));
				Assert.fail("should have thrown an exception because all items are already scored");
			}
			catch(Exception e){
				Assert.assertTrue(true);
			}
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	public void testTrueScoringSetSomeScored(){
		try {
			// get 12 but remove scores from two of them, so we're back down to ten scored
			List<NormalizedImageInfo> niis = getNiis(12,"key1","value1");
			niis.get(0).forgetValue(new NormalizedKey("key1"));
			niis.get(1).forgetValue(new NormalizedKey("key1"));
			TrueScoringSet tss = new TrueScoringSet(niis, new NormalizedKey("key1"));
			Assert.assertEquals(10, tss.getImagesToTrainOn().size());
			Assert.assertEquals(2, tss.getImagesToScore().size());
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	
	public void testGetImagesToTrainOnForKeyValue(){
		try {
			// 
			List<NormalizedImageInfo> niisA = getNiis(11,"key1","value1");
			niisA.get(0).forgetValue(new NormalizedKey("key1"));
			List<NormalizedImageInfo> niisB = getNiis(11,"key2","value2");
			niisB.get(0).forgetValue(new NormalizedKey("key2"));
			List<NormalizedImageInfo> niisC = new ArrayList<NormalizedImageInfo>();
			niisC.addAll(niisA);
			niisC.addAll(niisB);
			TrueScoringSet tss = new TrueScoringSet(niisC, new NormalizedKey("key1"));
			Assert.assertEquals(10, tss.getImagesToTrainOnForKeyValue(new NormalizedKey("key1"),new NormalizedValue("value1")).size());
			
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
}
