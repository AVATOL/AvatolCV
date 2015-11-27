package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestEvaluationSet extends TestCase {
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
	public void testEvaluationSetAllScored(){
		try {
			// ten images, all scored, so all can participate in split
			List<NormalizedImageInfo> niis = getNiis(10,"key1","value1");
			EvaluationSet es = new EvaluationSet(niis, "key1", 0.8);
			Assert.assertEquals(8, es.getImagesToTrainOn().size());
			Assert.assertEquals(2, es.getImagesToScore().size());
			es = new EvaluationSet(niis, "key1", 0.3);
			Assert.assertEquals(3, es.getImagesToTrainOn().size());
			Assert.assertEquals(7, es.getImagesToScore().size());
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	public void testEvaluationSetSomeScored(){
		try {
			// get 12 but remove scores from two of them, so we're back down to ten candidates
			List<NormalizedImageInfo> niis = getNiis(12,"key1","value1");
			niis.get(0).forgetValue("key1");
			niis.get(1).forgetValue("key1");
			EvaluationSet es = new EvaluationSet(niis, "key1", 0.8);
			Assert.assertEquals(8, es.getImagesToTrainOn().size());
			Assert.assertEquals(2, es.getImagesToScore().size());
			es = new EvaluationSet(niis, "key1", 0.3);
			Assert.assertEquals(3, es.getImagesToTrainOn().size());
			Assert.assertEquals(7, es.getImagesToScore().size());
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	public void testGetHoldoutCount(){
		Assert.assertEquals(EvaluationSet.getHoldoutCount(10, 0.8),2);
		Assert.assertEquals(EvaluationSet.getHoldoutCount(9, 0.8),2);
		Assert.assertEquals(EvaluationSet.getHoldoutCount(20, 0.8),4);
		Assert.assertEquals(EvaluationSet.getHoldoutCount(100, 0.5),50);
		Assert.assertEquals(EvaluationSet.getHoldoutCount(10, 0.3),7);
	}
	public void testGetImagesToTrainOnForKeyValue(){
		try {
			// 
			List<NormalizedImageInfo> niisA = getNiis(11,"key1","value1");
			niisA.get(0).forgetValue("key1");
			List<NormalizedImageInfo> niisB = getNiis(11,"key2","value2");
			niisB.get(0).forgetValue("key2");
			List<NormalizedImageInfo> niisC = new ArrayList<NormalizedImageInfo>();
			niisC.addAll(niisA);
			niisC.addAll(niisB);
			EvaluationSet es = new EvaluationSet(niisC, "key1", 0.5);
			Assert.assertEquals(5, es.getImagesToTrainOnForKeyValue("key1","value1").size());
			
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
}
