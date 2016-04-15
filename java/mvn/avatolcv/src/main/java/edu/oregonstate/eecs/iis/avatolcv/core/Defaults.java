package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;

public class Defaults {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String DEFAULTS_KEY_MORPHOBANK_LOGIN = "morphobankUserId";
    private static final String DEFAULTS_KEY_MORPHOBANK_PASSWORD = "morphobankPassword";
    private static final String DEFAULTS_KEY_BISQUE_LOGIN = "bisqueUserId";
    private static final String DEFAULTS_KEY_BISQUE_PASSWORD = "bisquePassword";
    public static Defaults instance = null;
    private Hashtable<String, String> hash = new Hashtable<String, String>();
    private static final Logger logger = LogManager.getLogger(Defaults.class);

    public static void init(String path) throws AvatolCVException {
        if (null != instance){
            // already loaded it
            return;
        }
        Defaults d = new Defaults();
        d.load(path);
        instance = d;
    }
    private void load(String path) throws AvatolCVException {
        String defaultsFilePath = path + FILESEP + "defaults.txt";
        File f = new File(defaultsFilePath);
        if (!f.exists()){
            // nothing to load
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(defaultsFilePath));
            String line = null;
            while (null != (line = reader.readLine())){
                String[] parts = ClassicSplitter.splitt(line, '=');
                if (parts.length == 2){
                    String key = parts[0];
                    String val = parts[1];
                    hash.put(key,  val);
                    logger.info("loaded Defaults key: " + key);
                }
            }
            reader.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException(ioe.getMessage(), ioe);
        }
    }
    public String getMorphobankLogin(){
        return getValue(DEFAULTS_KEY_MORPHOBANK_LOGIN);
    }
    public String getMorphobankPassword(){
        return getValue(DEFAULTS_KEY_MORPHOBANK_PASSWORD);
    }

    public String getBisqueLogin(){
        return getValue(DEFAULTS_KEY_BISQUE_LOGIN);
    }
    public String getBisquePassword(){
        return getValue(DEFAULTS_KEY_BISQUE_PASSWORD);
    }
    // make value for missing keys to be empty string
    private String getValue(String key){
        String value = hash.get(key);
        if (null == value){
            return "";
        }
        return value;
    }
}
