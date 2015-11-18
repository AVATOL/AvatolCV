package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.oregonstate.eecs.iis.avatolcv.Platform;

/**
 * This will launch a process to run a shell command.  This is a 
 * copy of OsuCommandLineRunner from the ADAMS project, but without logging
 */
public class CommandLineInvoker {
	private static final String NL = System.getProperty("line.separator");
	private Process process = null;
	private boolean processHasStarted = false;
    public CommandLineInvoker(){
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
    public boolean isProcessRunning(){
        if (!processHasStarted){
            return true;
        }
        return process.isAlive();
    }
    public boolean runCommandLine(List<String> commands, String stdoutPath){
       // ddh preOperation();
     boolean result = false;
     System.out.println("Execute Command: " + commands);
    
     StringBuilder sb = new StringBuilder();
     System.out.println("command array given as : " + NL);
     for (String s : commands){
    	 System.out.println("  " + s);
    	 sb.append(s + " ");
     }
     System.out.println("...or, as a single string...");
     System.out.println("" + sb);
     ProcessBuilder builder = null;
     builder = new ProcessBuilder(commands);
     Map<String, String> env = builder.environment();
     //printEnvironment(env);
     
     
     // redirect the stderr to stdout
     builder.redirectErrorStream(true);
     try {
         BufferedWriter writer = null;
         if (stdoutPath != null) {
             System.out.println("Stdout filename: " + stdoutPath);
             writer = new BufferedWriter(new FileWriter(stdoutPath));
         }
         this.process = builder.start();
         this.processHasStarted = true;
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
         System.out.println("OsuCommandRunner tried to run : " + sb.toString() + ": " + e.getMessage());
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
