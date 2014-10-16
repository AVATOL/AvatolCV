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
		    ArrayList<String> charIds = new ArrayList<String>();
		    charIds.add("c427749");
		    bundle.filterInputsByView(charIds, "v3540", "DPM");
		    
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
		    bundle.filterInputsByView(charIds, "v3540", "DPM");
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
}
