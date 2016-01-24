package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
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
	public void cleanDir(String path){
		File niiDir = new File(path);
		File[] files = niiDir.listFiles();
		for (File f : files){
			f.delete();
		}
	}
	// populate a blank slate with three
	public void testAPopulateBlankSlateDifferentIds(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id2";
			String mediaId3 = "id3";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			List<String> lines2 = new ArrayList<String>();
			lines2.add("key3=value3");
			lines2.add("key4=value4");
			List<String> lines3 = new ArrayList<String>();
			lines3.add("key5=value5");
			lines3.add("key6=value6");
			Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId1, lines1), niiDirPath + FILESEP + "id1_1.txt");
			Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId2, lines2), niiDirPath + FILESEP + "id2_1.txt");
			Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId3, lines3), niiDirPath + FILESEP + "id3_1.txt");
			
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
				cleanDir(niiDirPath);
				NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
				Assert.assertEquals(niis.getTotalCount(), 0);
				Assert.assertEquals(niis.getSessionCount(), 0);
				String mediaId1 = "id1";
				String mediaId2 = "id1";
				String mediaId3 = "id1";
				List<String> lines1 = new ArrayList<String>();
				lines1.add("key1=value1");
				lines1.add("key2=value2");
				List<String> lines2 = new ArrayList<String>();
				lines2.add("key3=value3");
				lines2.add("key4=value4");
				List<String> lines3 = new ArrayList<String>();
				lines3.add("key5=value5");
				lines3.add("key6=value6");
				Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId1, lines1), niiDirPath + FILESEP + "id1_1.txt");
				Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId2, lines2), niiDirPath + FILESEP + "id1_2.txt");
				Assert.assertEquals(niis.createNormalizedImageInfoFromLines(mediaId3, lines3), niiDirPath + FILESEP + "id1_3.txt");
				
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
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id1";
			String mediaId3 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			List<String> lines2 = new ArrayList<String>();
			lines2.add("key3=value3");
			lines2.add("key4=value4");
			List<String> lines3 = new ArrayList<String>();
			lines3.add("key5=value5");
			lines3.add("key6=value6");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			niis.createNormalizedImageInfoFromLines(mediaId2, lines2);
			niis.createNormalizedImageInfoFromLines(mediaId3, lines3);
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
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			String mediaId2 = "id1";
			String mediaId3 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			List<String> lines2 = new ArrayList<String>();
			lines2.add("key3=value3");
			lines2.add("key4=value4");
			List<String> lines3 = new ArrayList<String>();
			lines3.add("key5=value5");
			lines3.add("key6=value6");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			niis.createNormalizedImageInfoFromLines(mediaId2, lines2);
			niis.createNormalizedImageInfoFromLines(mediaId3, lines3);
			
			NormalizedImageInfos niis2 = new NormalizedImageInfos(niiDirPath);
			try {
				List<String> sessionList = new ArrayList<String>();
				sessionList.add("id1_1.txt");
				sessionList.add("id1_2.txt");
				niis2.focusToSession(sessionList);
				Assert.assertEquals(niis2.getSessionCount(),2);
			}
			catch(AvatolCVException ace){
				Assert.fail("hit exception trying to focus session " + ace.getMessage());
			}

			NormalizedImageInfos niis3 = new NormalizedImageInfos(niiDirPath);
			try {
				List<String> sessionList = new ArrayList<String>();
				sessionList.add("id1_1.txt");
				sessionList.add("id1_4.txt");
				niis3.focusToSession(sessionList);
				Assert.fail("should have thrown exception on bogus filename");
			}
			catch(AvatolCVException ace){
				Assert.assertTrue(ace.getMessage().startsWith("given filename "));
			}
			
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	
	//determine  "incoming" properties file already is present
	public void testDVerifyFileALreadyExists(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertEquals(niis.getTotalCount(),1);
			List<String> lines2 = new ArrayList<String>();
			lines2.add("key1=value1");
			lines2.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines2);
			// no new one added
			Assert.assertEquals(1,niis.getTotalCount());
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	//making a new file for it with one-up name if it isn't
	public void testEVerifyFileNewAdded(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			Assert.assertEquals(niis.getTotalCount(), 0);
			Assert.assertEquals(niis.getSessionCount(), 0);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertEquals(niis.getTotalCount(),1);
			List<String> lines2 = new ArrayList<String>();
			// different values, thus distinct, should add new one
			lines2.add("key1=value3");
			lines2.add("key2=value4");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines2);
			// new one added
			Assert.assertEquals(niis.getTotalCount(),2);
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
	}
	//providing properties files to scoringConfig screen based on filenames stored in the sessionImageList
	public void testFRetrievePropsForFilenames(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertEquals(niis.getTotalCount(),1);
			List<String> lines2 = new ArrayList<String>();
			// different values, thus distinct, should add new one
			lines2.add("key1=value3");
			lines2.add("key2=value4");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines2);
			// new one added
			Assert.assertEquals(niis.getTotalCount(),2);
			try {
				NormalizedImageInfo nii1 = niis.getNormalizedImageInfoForSessionWithName("id1_1.txt");
				Assert.fail("should have thrown exception - desired  nii not yet added to session");
			}
			catch(AvatolCVException ace){
				Assert.assertTrue(true);
			}
			List<String> sessionList = new ArrayList<String>();
			sessionList.add("id1_1.txt");
			sessionList.add("id1_2.txt");
			niis.focusToSession(sessionList);
			NormalizedImageInfo nii1 = niis.getNormalizedImageInfoForSessionWithName("id1_1.txt");
			Assert.assertEquals(nii1.getValueForKey(new NormalizedKey("key1")).getName(),"value1");
			Assert.assertEquals(nii1.getValueForKey(new NormalizedKey("key2")).getName(),"value2");
			NormalizedImageInfo nii2 = niis.getNormalizedImageInfoForSessionWithName("id1_2.txt");
			Assert.assertEquals(nii2.getValueForKey(new NormalizedKey("key1")).getName(),"value3");
			Assert.assertEquals(nii2.getValueForKey(new NormalizedKey("key2")).getName(),"value4");
			
			try {
				NormalizedImageInfo nii3 = niis.getNormalizedImageInfoForSessionWithName("id1_3.txt");
				Assert.fail("should have thrown exception getting nii from session that is not in the session");
			}
			catch(AvatolCVException ace){
				Assert.assertTrue(true);
			}
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	//flushes on request (and deletes files)
	public void testGFlush(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertEquals(niis.getTotalCount(),1);
			List<String> lines2 = new ArrayList<String>();
			// different values, thus distinct, should add new one
			lines2.add("key1=value3");
			lines2.add("key2=value4");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines2);
			
			List<String> sessionList = new ArrayList<String>();
			sessionList.add("id1_1.txt");
			niis.focusToSession(sessionList);
			
			Assert.assertEquals(niis.getSessionCount(),1);
			Assert.assertEquals(niis.getTotalCount(), 2);
			niis.flush();
			Assert.assertEquals(niis.getSessionCount(),0);
			Assert.assertEquals(niis.getTotalCount(), 0);
			File dirFile = new File(niiDirPath);
			File[] files = dirFile.listFiles();
			Assert.assertTrue(files.length==0);
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	public void testHGetSessionNIIsForKeyValue(){
		try {
			// first delete any existing files
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertEquals(niis.getTotalCount(),1);
			List<String> lines2 = new ArrayList<String>();
			// different values, thus distinct, should add new one
			lines2.add("key1=value3");
			lines2.add("key2=value4");
			niis.createNormalizedImageInfoFromLines(mediaId1, lines2);
			
			List<String> sessionList = new ArrayList<String>();
			sessionList.add("id1_1.txt");
			sessionList.add("id1_2.txt");
			niis.focusToSession(sessionList);
			
			List<NormalizedImageInfo> niiList = niis.getSessionNIIsForKeyValue(new NormalizedKey("key1"),new NormalizedValue("value1"));
			Assert.assertTrue(niiList.size() == 1);
			Assert.assertEquals(niiList.get(0).getValueForKey(new NormalizedKey("key1")).getName(),"value1");
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
	
	public void testDoesMatchingNormalizedImageFileExistAtPath(){
		try {
			String niiDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
			cleanDir(niiDirPath);
			NormalizedImageInfos niis = new NormalizedImageInfos(niiDirPath);
			String mediaId1 = "id1";
			List<String> lines1 = new ArrayList<String>();
			lines1.add("key1=value1");
			lines1.add("key2=value2");
			String path = niis.createNormalizedImageInfoFromLines(mediaId1, lines1);
			Assert.assertTrue(NormalizedImageInfos.doesMatchingNormalizedImageFileExistAtPath(lines1, mediaId1, path));
			
			List<String> lines2 = new ArrayList<String>();
			lines2.add("key1=valueX");
			lines2.add("key2=valueY");
			Assert.assertFalse(NormalizedImageInfos.doesMatchingNormalizedImageFileExistAtPath(lines2, mediaId1, path));
			
			File f = new File(path);
			f.delete();
		}
		catch(AvatolCVException ace){
			Assert.fail(ace.getMessage());
		}
		
	}
}
