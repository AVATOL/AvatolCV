package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.File;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class ValidRoot {
	 public static String getValidRoot() throws AvatolCVException {
	        String jedDesktopRoot = "C:\\jed\\avatol\\git\\avatol_cv";
	        File f = new File(jedDesktopRoot);
	        if (f.exists()){
	            return jedDesktopRoot;
	        }
	        String jedLaptopRoot = "C:\\avatol\\git\\avatol_cv";
	        f = new File(jedLaptopRoot);
	        if (f.exists()){
	            return jedLaptopRoot;
	        }
	        String jedMacRoot = "/Users/jedirvine/av/avatol_cv";
	        f = new File(jedMacRoot);
	        if (f.exists()){
	            return jedMacRoot;
	        }
	        throw new AvatolCVException("no valid algorithm root found");
	    }
}
