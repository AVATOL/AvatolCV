package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.junit.Test;

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
	@Test
	public void testScoredSetMetadataWindows(){
		String NL = System.getProperty("line.separator");
		String matrixName = "BAT";
		String taxon = "t281048";
		String view = "v3540";
		String character = "c427749";
		ArrayList<String> charIds = new ArrayList<String>();
		charIds.add("c427749");
		charIds.add("c427753");
		charIds.add("c427754");
		charIds.add("c427760");
		ScoredSetMetadata ssm = new ScoredSetMetadata("C:\\avatol\\git\\avatol_cv");
		try {
			ssm.persistForDPM(matrixName, taxon, character, view, charIds);
			ssm.loadAll();
			List<String> keys = ssm.getKeys();
			
            for (String key : keys){
                String data = ssm.getDataForKey(key);
                System.out.println("key " + key + NL + " data found : " + data);
            }   
		}
		catch(Exception ioe){
			ioe.printStackTrace();
			System.out.println(ioe.getMessage());
		}
	}
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
	/*
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
		    bundle.filterInputs(charIds, "t281048", "v3540", "DPM");
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	*/
	
}
