package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
