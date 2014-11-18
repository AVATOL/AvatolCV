package edu.oregonstate.eecs.iis.avatolcv;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;

public class TestMatrix {
    /*
	@Test
	public void testBundle() {
		try {
		    MorphobankBundle m = new MorphobankBundle("/nfs/guille/tgd/users/irvine/matlabui/matrix_downloads/BOGUS");
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}*/
	/*
	@Test
	public void testScoredSetMetadataUnix(){
		String matrixName = "BAT";
		String taxon = "t281048";
		String view = "v3540";
		String character = "c427749";
		ArrayList<String> charIds = new ArrayList<String>();
		charIds.add("c427749");
		charIds.add("c427753");
		charIds.add("c427754");
		charIds.add("c427760");
		ScoredSetMetadata ssm = new ScoredSetMetadata("/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv");
		try {
			ssm.persistForDPM(matrixName, taxon, character, view, charIds);
			Hashtable<String,String> resultsMetadata = ssm.loadAll();
			Enumeration<String> keysEnumeration = resultsMetadata.keys();
            while (keysEnumeration.hasMoreElements()){
                String key = (String)keysEnumeration.nextElement();
                String data = resultsMetadata.get(key);
                System.out.println("data found : " + data);
            }   
		}
		catch(Exception ioe){
			ioe.printStackTrace();
			System.out.println(ioe.getMessage());
		}
	}*/
	/*
	@Test
	public void testScoredSetMetadataWindows(){
		String NL = System.getProperty("line.separator");
		String matrixName = "BAT";
		String taxon = "t281048";
		String view = "v3540";
		String characterId = "c427749";
		String characterName = "someCharacter";
		List<String> charIds = new ArrayList<String>();
		charIds.add("c427749");
		charIds.add("c427753");
		charIds.add("c427754");
		charIds.add("c427760");
		ScoredSetMetadata ssm = new ScoredSetMetadata("C:\\avatol\\git\\avatol_cv");
		String input_folder = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\input\\DPM\\t281048\\c427749c427753c427754c427760\\v3540";
		String output_folder = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\output\\DPM\\t281048\\c427749c427753c427754c427760\\v3540";
		String detection_results_folder = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT\\detection_results\\DPM\\t281048\\c427749c427753c427754c427760\\v3540";
		try {
			ssm.persistForDPM(matrixName, taxon, characterName, characterId, view, charIds, input_folder,  output_folder,  detection_results_folder);
			ssm.loadAll();
			List<String> keys = ssm.getKeys();
			
            for (String key : keys){
                String data = ssm.getDataForKey(key);
                System.out.println("key " + key + NL + " data found : " + data);
                String displayableData = ssm.getDisplayableDataForKey(key);
                System.out.println("key " + key + NL + "displayable data found : " + displayableData);
                System.out.println("inputFolder " + ssm.getInputFolderForKey(key));
                System.out.println("outputFolder " + ssm.getOutputFolderForKey(key));
                System.out.println("detectionResultsFolder " + ssm.getDetectionResultsFolderForKey(key));
            }   
		}
		catch(Exception ioe){
			ioe.printStackTrace();
			System.out.println(ioe.getMessage());
		}
	}
	*/
	/*
	@Test
	public void testLoadInputFilesWindows(){
		try {
			String rootDir = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT";
		    MorphobankBundle mb = new MorphobankBundle(rootDir);
		    Hashtable<String,InputFile> h = mb.getInputFilesForCharacter(rootDir + "\\input\\DPM\\t281048\\c427749c427753c427754c427760\\v3540");
		    ScoredSetMetadata ssm = new ScoredSetMetadata("C:\\avatol\\git\\avatol_cv");
		    ssm.loadAll();
		    List<String> keys = ssm.getKeys();
		    String curKey = keys.get(keys.size() - 1);
            String input_folder = ssm.getInputFolderForKey(curKey);
            String taxonName = "Thyroptera tricolor";
            String taxonId = mb.getTaxonIdForName(taxonName);
            String foo = "hello";
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		
	}
	*/
	/*
	@Test
	public void testDataUnix() {
		try {
		    MorphobankData md = new MorphobankData("/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads");
		    md.loadMatrix("BAT");
		    MorphobankBundle bundle = md.getBundle("BAT");
		    List<String> names = bundle.getScorableCharacterNames();
		    ArrayList<String> charIds = new ArrayList<String>();
		    charIds.add("c427749");
		    charIds.add("c427753");
		    charIds.add("c427754");
		    charIds.add("c427760");
		    bundle.filterInputs(charIds, "t281048", "v3540", "DPM");
		    
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	*/
	/*
	@Test
	public void testGetTaxonIdForNameUnix() {
		try {
		    MorphobankData md = new MorphobankData("/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads");
		    md.loadMatrix("BAT");
		    MorphobankBundle bundle = md.getBundle("BAT");
		    bundle.getTaxonIdForName("Thyroptera tricolor");
		    
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	*/
	/*
	@Test
	public void testGetScorableTaxonNamesUnix() {
		try {
		    MorphobankData md = new MorphobankData("/nfs/guille/bugid/bugid/AVATOL/av_cv/git/avatol_cv/matrix_downloads");
		    md.loadMatrix("BAT");
		    MorphobankBundle bundle = md.getBundle("BAT");
		    bundle.getScorableTaxonNames();
		    
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	*/
	
	@Test
	public void testDataWindows() {
		try {
		    MorphobankData md = new MorphobankData("C:\\avatol\\git\\avatol_cv\\matrix_downloads");
		    //md.loadMatrix("BOGUS");
		    md.loadMatrix("BAT");
		    MorphobankBundle bundle = md.getBundle("BAT");
		    List<String> names = bundle.getScorableCharacterNames();
		    ArrayList<String> charIds = new ArrayList<String>();
		    charIds.add("c427749");
		    charIds.add("c427753");
		    charIds.add("c427754");
		    charIds.add("c427760");
		    bundle.filterInputs(charIds, "t281048", "v3540", "DPM");
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	
}
