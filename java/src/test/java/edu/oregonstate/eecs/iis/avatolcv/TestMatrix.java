package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.*;

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
		    MorphobankData md = new MorphobankData("/nfs/guille/tgd/users/irvine/matlabui/matrix_downloads");
		    md.loadMatrix("BOGUS");
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
		    md.loadMatrix("BOGUS");
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
}
