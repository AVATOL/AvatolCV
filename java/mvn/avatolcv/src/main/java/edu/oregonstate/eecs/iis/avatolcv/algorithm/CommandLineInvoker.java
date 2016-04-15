package edu.oregonstate.eecs.iis.avatolcv.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.Platform;

/**
 * This will launch a process to run a shell command.  This is a 
 * copy of OsuCommandLineRunner from the ADAMS project, but without logging
 */
public class CommandLineInvoker {
	private static final String NL = System.getProperty("line.separator");
	private Process process = null;
	private boolean processHasStarted = false;
	private static final Logger logger = LogManager.getLogger(CommandLineInvoker.class);
    public CommandLineInvoker(){
    }
    public void printEnvironment(Map<String, String> env){
        logger.info("...ENVIRONMENT...");
        Set<String> keySet = env.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()){
            String k = iter.next();
            logger.info("key : " + k  + "   ,    value : " + env.get(k));
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
     logger.info("Execute Command: " + commands);
    
     StringBuilder sb = new StringBuilder();
     logger.info("command array given as : " + NL);
     for (String s : commands){
    	 logger.info("  " + s);
    	 sb.append(s + " ");
     }
     logger.info("...or, as a single string...");
     logger.info("" + sb);
     ProcessBuilder builder = null;
     builder = new ProcessBuilder(commands);
     Map<String, String> env = builder.environment();
     //printEnvironment(env);
     
     
     // redirect the stderr to stdout
     builder.redirectErrorStream(true);
     try {
         BufferedWriter writer = null;
         if (stdoutPath != null) {
             logger.info("Stdout filename: " + stdoutPath);
             writer = new BufferedWriter(new FileWriter(stdoutPath));
         }
         this.process = builder.start();
         this.processHasStarted = true;
         BufferedReader reader = new BufferedReader(new InputStreamReader(
             process.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null) {
             logger.info(line);
             if (writer != null) {
                 writer.write(line + "\n");
             }
         }
         if (writer != null) {
             writer.close();
             writer = null;
         }

         int status = process.waitFor();
         logger.info("process status: " + status);

         if (status == 0) {
             result = true;
         }
         else {
            result = false;
         }

     } catch (Exception e) {
         logger.info("OsuCommandRunner tried to run : " + sb.toString() + ": " + e.getMessage());
         e.printStackTrace();
         Throwable t = e.getCause();
         logNestedException(t);
     }
     return result;
 }
    public void cancel(){
        if (null != this.process){
            if (this.process.isAlive()){
                this.process.destroyForcibly();
            }
        }
    }
    public boolean runCommandLine(List<String> commands, OutputMonitor outputMonitor){
        // ddh preOperation();
      boolean result = false;
      logger.info("Execute Command: " + commands);
     
      StringBuilder sb = new StringBuilder();
      logger.info("command array given as : " + NL);
      for (String s : commands){
          logger.info("  " + s);
          sb.append(s + " ");
      }
      logger.info("...or, as a single string...");
      logger.info("" + sb);
      ProcessBuilder builder = null;
      builder = new ProcessBuilder(commands);
      Map<String, String> env = builder.environment();
      //printEnvironment(env);
      
      
      // redirect the stderr to stdout
      builder.redirectErrorStream(true);
      try {
          
          this.process = builder.start();
          this.processHasStarted = true;
          BufferedReader reader = new BufferedReader(new InputStreamReader(
              process.getInputStream()));
          String line;
          while ((line = reader.readLine()) != null) {
              //logger.info(line);
              outputMonitor.acceptOutput(line);
          }
          

          int status = process.waitFor();
          logger.info("process status: " + status);

          if (status == 0) {
              result = true;
          }
          else {
             result = false;
          }

      } catch (Exception e) {
          logger.info("OsuCommandRunner tried to run : " + sb.toString() + ": " + e.getMessage());
          e.printStackTrace();
          Throwable t = e.getCause();
          logNestedException(t);
      }
      return result;
  }
 public void logNestedException(Throwable t){
     if (null != t){
        logger.info("nestedException was " + t.getMessage());
        t.printStackTrace();
        Throwable t2 = t.getCause();
        logNestedException(t2);
      }
 }
}
