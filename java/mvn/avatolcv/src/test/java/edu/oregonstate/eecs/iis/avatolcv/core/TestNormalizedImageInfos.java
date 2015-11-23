package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestNormalizedImageInfos extends TestCase {
	private static final String FILESEP = System.getProperty("file.separator");
	protected void setUp(){
        try {
            AvatolCVFileSystem.setRootDir(TestAlgorithm.getValidRoot());
            DatasetInfo di = new DatasetInfo();
            di.setName("unitTest");
            di.setID("xyz");
            di.setProjectID("abc");
            AvatolCVFileSystem.setDatasourceName("local");
            AvatolCVFileSystem.setSessionID("testSession");
            AvatolCVFileSystem.setChosenDataset(di);
        }
        catch(AvatolCVException ace){
            Assert.fail("Proplem initializing AvatolCVFileSystem " + ace.getMessage());
        }
    }
	public void testGetFirstUnusedSuffix(){
		// empty list should return "1"
		List<String> list = new ArrayList<String>();
		Assert.assertEquals(NormalizedImageInfos.getFirstUnusedSuffix(list),"1");
		// list with "1" should return "2"
		list.add("1");
		Assert.assertEquals(NormalizedImageInfos.getFirstUnusedSuffix(list),"2");
		// list with "1" and "3" should return "2"
		list.add("3");
		Assert.assertEquals(NormalizedImageInfos.getFirstUnusedSuffix(list),"2");
		// list with "2" and "3" should return "1"
		list.remove("1");
		list.add("2");
		Assert.assertEquals(NormalizedImageInfos.getFirstUnusedSuffix(list),"1");
	}
	// populate a blank slate with three
	public void testAPopulateBlankSlateDifferentIds(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			File niiDir = new File(niiDirPath);
			File[] files = niiDir.listFiles();
			for (File f : files){
				f.delete();
			}
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id2";
			String mediaId3 = "id3";
			Properties p1 = new Properties();
			p1.setProperty("key1", "value1");
			p1.setProperty("key2", "value2");
			Properties p2 = new Properties();
			p2.setProperty("key3", "value3");
			p2.setProperty("key4", "value4");
			Properties p3 = new Properties();
			p3.setProperty("key5", "value5");
			p3.setProperty("key6", "value6");
			Assert.assertEquals(niis.addMediaInfo(mediaId1, p1), niiDirPath + FILESEP + "id1_1.txt");
			Assert.assertEquals(niis.addMediaInfo(mediaId2, p2), niiDirPath + FILESEP + "id2_1.txt");
			Assert.assertEquals(niis.addMediaInfo(mediaId3, p3), niiDirPath + FILESEP + "id3_1.txt");
			
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	// populate a blank slate with three
		public void testAPopulateBlankSlateSameIds(){
			try {
				// first delete any existing files
				String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
				File niiDir = new File(niiDirPath);
				File[] files = niiDir.listFiles();
				for (File f : files){
					f.delete();
				}
				NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
				Assert.assertEquals(niis.getTotalCount(), 0);
				Assert.assertEquals(niis.getSessionCount(), 0);
				String mediaId1 = "id1";
				String mediaId2 = "id1";
				String mediaId3 = "id1";
				Properties p1 = new Properties();
				p1.setProperty("key1", "value1");
				p1.setProperty("key2", "value2");
				Properties p2 = new Properties();
				p2.setProperty("key3", "value3");
				p2.setProperty("key4", "value4");
				Properties p3 = new Properties();
				p3.setProperty("key5", "value5");
				p3.setProperty("key6", "value6");
				Assert.assertEquals(niis.addMediaInfo(mediaId1, p1), niiDirPath + FILESEP + "id1_1.txt");
				Assert.assertEquals(niis.addMediaInfo(mediaId2, p2), niiDirPath + FILESEP + "id1_2.txt");
				Assert.assertEquals(niis.addMediaInfo(mediaId3, p3), niiDirPath + FILESEP + "id1_3.txt");
				
			}
			catch(AvatolCVException ace){
				Assert.fail(ace.getMessage());
			}
		}
	//load up existing normalizedImageInfo files for the dataset
	public void testB1LoadUpStored(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			File niiDir = new File(niiDirPath);
			File[] files = niiDir.listFiles();
			for (File f : files){
				f.delete();
			}
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id1";
			String mediaId3 = "id1";
			Properties p1 = new Properties();
			p1.setProperty("key1", "value1");
			p1.setProperty("key2", "value2");
			Properties p2 = new Properties();
			p2.setProperty("key3", "value3");
			p2.setProperty("key4", "value4");
			Properties p3 = new Properties();
			p3.setProperty("key5", "value5");
			p3.setProperty("key6", "value6");
			niis.addMediaInfo(mediaId1, p1);
			niis.addMediaInfo(mediaId2, p2);
			niis.addMediaInfo(mediaId3, p3);
			NormalizedImageInfos niis2 = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis2.getTotalCount(),3);
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	// focuses operations on set of filenames based on session
	public void testB2FocusToSessionData(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			File niiDir = new File(niiDirPath);
			File[] files = niiDir.listFiles();
			for (File f : files){
				f.delete();
			}
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id1";
			String mediaId3 = "id1";
			Properties p1 = new Properties();
			p1.setProperty("key1", "value1");
			p1.setProperty("key2", "value2");
			Properties p2 = new Properties();
			p2.setProperty("key3", "value3");
			p2.setProperty("key4", "value4");
			Properties p3 = new Properties();
			p3.setProperty("key5", "value5");
			p3.setProperty("key6", "value6");
			niis.addMediaInfo(mediaId1, p1);
			niis.addMediaInfo(mediaId2, p2);
			niis.addMediaInfo(mediaId3, p3);
			
			NormalizedImageInfos niis2 = new NormalizedImageInfos(niiDirPath);
			try {
				List<String> sessionList = new ArrayList<String>();
				sessionList.add("id1_1");
				sessionList.add("id1_2");
				niis2.focusToSession(sessionList);
				Assert.assertEquals(niis2.getSessionCount(),2);
			}
			catch(AvatolCVException ace){
				Assert.fail("hit exception trying to focus session " + ace.getMessage());
			}

			NormalizedImageInfos niis3 = new NormalizedImageInfos(niiDirPath);
			try {
				List<String> sessionList = new ArrayList<String>();
				sessionList.add("id1_1");
				sessionList.add("id1_4");
				niis3.focusToSession(sessionList);
				Assert.fail("should have thrown exception on bogus filename");
			}
			catch(AvatolCVException ace){
				Assert.assertTrue(ace.getMessage().startsWith("given filename "));
			}
			LEFT OFF HERE...
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	//storing unique properties files into unique filenames (for debugging)
	public void testCVerifyUniqueFilenames(){
		Assert.fail("not yet implemented");
		
	}
	//determine  "incoming" properties file already is present
	public void testDVerifyFileALreadyExists(){
		Assert.fail("not yet implemented");
		
	}
	//making a new file for it with one-up name if it isn't
	public void testEVerifyFileNewAdded(){
		Assert.fail("not yet implemented");
		
	}
	//providing properties files to scoringConfig screen based on filenames stored in the sessionImageList
	public void testFRetrievePropsForFilenames(){
		Assert.fail("not yet implemented");
		
	}
	//flushes on request (and deletes files)
	public void testGFlush(){
		Assert.fail("not yet implemented");
		
	}
	public void testHGetSessionNIIsForTaxonValue(){
		Assert.fail("not yet implemented");
		
	}
}
