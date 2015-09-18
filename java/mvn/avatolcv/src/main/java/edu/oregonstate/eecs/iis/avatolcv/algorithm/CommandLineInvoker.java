package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This will launch a process to run a shell command.  This is a 
 * copy of OsuCommandLineRunner from the ADAMS project, but without logging
 */
public class CommandLineInvoker {
	private String dirToRunIn = null;
    public CommandLineInvoker(String dirToRunIn){
    	this.dirToRunIn = dirToRunIn;
    }
    public void printEnvironment(Map<String, String> env){
        System.out.println("...ENVIRONMENT...");
        Set<String> keySet = env.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()){
            String k = iter.next();
            System.out.println("key : " + k  + "   ,    value : " + env.get(k));
        }
         
    }
    
    public boolean runCommandLine(String commandLine, String stdoutPath){
       // ddh preOperation();
     boolean result = false;
     System.out.println("Execute Command: " + commandLine);
     String fullCommandLine = "cd " + this.dirToRunIn + ";" + commandLine;
     ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", fullCommandLine);
     
     Map<String, String> env = builder.environment();
     printEnvironment(env);
     // redirect the stderr to stdout
     builder.redirectErrorStream(true);
     try {
         BufferedWriter writer = null;
         if (stdoutPath != null) {
             System.out.println("Stdout filename: " + stdoutPath);
             writer = new BufferedWriter(new FileWriter(stdoutPath));
         }
         Process process = builder.start();

         BufferedReader reader = new BufferedReader(new InputStreamReader(
             process.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null) {
             System.out.println(line);
             if (writer != null) {
                 writer.write(line + "\n");
             }
         }
         if (writer != null) {
             writer.close();
             writer = null;
         }

         int status = process.waitFor();
         System.out.println("process status: " + status);

         if (status == 0) {
             result = true;
         }
         else {
            result = false;
         }

     } catch (Exception e) {
         System.out.println("OsuCommandRunner tried to run : " + commandLine + ": " + e.getMessage());
         e.printStackTrace();
         Throwable t = e.getCause();
         logNestedException(t);
     }
     return result;
 }
 
 public void logNestedException(Throwable t){
     if (null != t){
        System.out.println("nestedException was " + t.getMessage());
        t.printStackTrace();
        Throwable t2 = t.getCause();
        logNestedException(t2);
      }
 }
}
