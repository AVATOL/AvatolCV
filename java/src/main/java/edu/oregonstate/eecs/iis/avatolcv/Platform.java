package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.io.IOException;

public class Platform {
	public static boolean isWindows() {
		String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
    	String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
    	String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0);
    }
    public static void setPermissions(String path){
    	if (Platform.isUnix() || Platform.isMac()){
    		File f = new File(path);
    		if (f.isDirectory()){
    			chmod775(path);
    		}
    		else {
    			chmod664(path);
    		}
    		File parentDir = f.getParentFile();
    		while (null != parentDir  && !parentDir.getName().equals("matrix_downloads")){
    			chmod775(parentDir.getAbsolutePath());
    			parentDir = parentDir.getParentFile();
    		}
    		
    	}
    }
    public static void chmod775(String folderPath){
    	try {
    		String[] command = new String[]{"csh","-c","chmod 0775 \"" + folderPath + "\""};
    		//System.out.print ("attempting : " + command);
    		Process p = Runtime.getRuntime().exec(command);
    		p.waitFor();
    		int exitValue = p.exitValue();
    		//System.out.println(" " + exitValue);
    	}
    	catch(IOException ioe){
    		// do nothing
    	}
    	catch(InterruptedException ie){
    		// do nothing
    	}
    	
    }
    public static void chmod664(String filePath){
    	try {
    		String[] command = new String[]{"csh","-c","chmod 0664 \"" + filePath + "\""};
    		//System.out.print("attempting : " + command);
    		Process p = Runtime.getRuntime().exec(command);
    		p.waitFor();
    		int exitValue = p.exitValue();
    		//System.out.println(" " + exitValue);
    	}
    	catch(IOException ioe){
    		// do nothing
    	}
    	catch(InterruptedException ie){
    		// do nothing
    	}
    	
    }
}
