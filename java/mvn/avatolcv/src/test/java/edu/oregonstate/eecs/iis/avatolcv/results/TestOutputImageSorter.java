package edu.oregonstate.eecs.iis.avatolcv.results;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.algorithm.AlgorithmInput;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestOutputImageSorter extends TestCase {
	private static final String FILESEP = System.getProperty("file.separator");
	public void testSorter(){
		OutputImageSorter sorter = new OutputImageSorter();
		String inputSuffix1 = AlgorithmInput.NO_SUFFIX;
		List<String> suffix1InputPaths = new ArrayList<String>();
		suffix1InputPaths.add("id1_1000.jpg");
		suffix1InputPaths.add("id2_1000.jpg");
		suffix1InputPaths.add("id3_1000.jpg");
		sorter.addInputSuffixAndPaths(inputSuffix1, suffix1InputPaths);
		
		String inputSuffix2 = "insuffix2";
		List<String> suffix2InputPaths = new ArrayList<String>();
		suffix2InputPaths.add("id1_1000_insuffix2.jpg");
		suffix2InputPaths.add("id2_1000_insuffix2.jpg");
		suffix2InputPaths.add("id3_1000_insuffix2.jpg");
		sorter.addInputSuffixAndPaths(inputSuffix2, suffix2InputPaths);
		
		String inputSuffix3 = "insuffix3";
		List<String> suffix3InputPaths = new ArrayList<String>();
		suffix3InputPaths.add("id1_1000_insuffix3.jpg");
		suffix3InputPaths.add("id2_1000_insuffix3.jpg");
		suffix3InputPaths.add("id3_1000_insuffix3.jpg");
		sorter.addInputSuffixAndPaths(inputSuffix3, suffix3InputPaths);
		
		
		String outputSuffix1 = "outsuffix1";
		List<String> suffix1OutputPaths = new ArrayList<String>();
		suffix1OutputPaths.add("id1_1000_outsuffix1.jpg");
		suffix1OutputPaths.add("id2_1000_outsuffix1.jpg");
		suffix1OutputPaths.add("id3_1000_outsuffix1.jpg");
		sorter.addOutputSuffixAndPaths(outputSuffix1, suffix1OutputPaths);
		
		String outputSuffix2 = "outsuffix2";
		List<String> suffix2OutputPaths = new ArrayList<String>();
		suffix2OutputPaths.add("id1_1000_outsuffix2.jpg");
		suffix2OutputPaths.add("id2_1000_outsuffix2.jpg");
		suffix2OutputPaths.add("id3_1000_outsuffix2.jpg");
		sorter.addOutputSuffixAndPaths(outputSuffix2, suffix2OutputPaths);
		
		sorter.sort();
		
		// prove that when we ask for image paths, we get an ordered list that we can just lay out in columns and 
		// assume that the images in one column are all of the same type
		List<String> imageIDs = sorter.getImageIDs();
		Assert.assertEquals(3, imageIDs.size());
		String imageID0 = imageIDs.get(0);
		String imageID1 = imageIDs.get(1);
		String imageID2 = imageIDs.get(2);
		Assert.assertEquals("id1", imageID0);
		Assert.assertEquals("id2", imageID1);
		Assert.assertEquals("id3", imageID2);
			

		List<String> outputPaths = sorter.getOutputPathsForImageID(imageID0);
		Assert.assertEquals(2, outputPaths.size());
		Assert.assertEquals("id1_1000_outsuffix1.jpg",outputPaths.get(0));
		Assert.assertEquals("id1_1000_outsuffix2.jpg",outputPaths.get(1));
		
		outputPaths = sorter.getOutputPathsForImageID(imageID1);
		Assert.assertEquals(2, outputPaths.size());
		Assert.assertEquals("id2_1000_outsuffix1.jpg",outputPaths.get(0));
		Assert.assertEquals("id2_1000_outsuffix2.jpg",outputPaths.get(1));
		
		outputPaths = sorter.getOutputPathsForImageID(imageID2);
		Assert.assertEquals(2, outputPaths.size());
		Assert.assertEquals("id3_1000_outsuffix1.jpg",outputPaths.get(0));
		Assert.assertEquals("id3_1000_outsuffix2.jpg",outputPaths.get(1));
		
		List<String> inputPaths = sorter.getInputPathsForImageID(imageID0);
		Assert.assertEquals(3, inputPaths.size());
		Assert.assertEquals("id1_1000.jpg",inputPaths.get(0));
		Assert.assertEquals("id1_1000_insuffix2.jpg",inputPaths.get(1));
		Assert.assertEquals("id1_1000_insuffix3.jpg",inputPaths.get(2));

		inputPaths = sorter.getInputPathsForImageID(imageID1);
		Assert.assertEquals(3, inputPaths.size());
		Assert.assertEquals("id2_1000.jpg",inputPaths.get(0));
		Assert.assertEquals("id2_1000_insuffix2.jpg",inputPaths.get(1));
		Assert.assertEquals("id2_1000_insuffix3.jpg",inputPaths.get(2));

		inputPaths = sorter.getInputPathsForImageID(imageID2);
		Assert.assertEquals(3, inputPaths.size());
		Assert.assertEquals("id3_1000.jpg",inputPaths.get(0));
		Assert.assertEquals("id3_1000_insuffix2.jpg",inputPaths.get(1));
		Assert.assertEquals("id3_1000_insuffix3.jpg",inputPaths.get(2));
	}
}
