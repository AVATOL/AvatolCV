package edu.oregonstate.eecs.iis.avatolcv.util;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;

public class ImageRemover {

	public static void main(String[] args){
		try {
			MorphobankData md = new MorphobankData("C:\\avatol\\git\\avatol_cv\\matrix_downloads");
			//md.loadMatrix("BOGUS");
			md.loadMatrix("BAT");
			MorphobankBundle bundle = md.getBundle("BAT");
		}
		catch(Exception ex){
			
		}
	}
	public ImageRemover(){
		
	}
}
