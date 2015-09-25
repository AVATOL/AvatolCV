package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;

public class ExploreData {
    private List<Properties> props = new ArrayList<Properties>();
    private static final String NL = System.getProperty("line.separator");
    public static void main(String[] args){
        ExploreData ed = new ExploreData();
        FileSystemPrimer.prime("C:\\jed\\avatol\\git\\avatol_cv", "explore", "leafDev", "20150924_01", "bisque");
        ed.summarizeLabelPresence();
    }
    
    private void assessProperty(String name){
        int propsCount = props.size();
        int curCount = 0;
        for (Properties p : props){
            String value = (String)p.get(name);
            if (value == null){
                // a miss
            }
            else if (value.equals("")){
                // a miss
            }
            else {
                curCount++;
            }
        }
        System.out.println(curCount + " out of " + propsCount);
    }
    private List<String> registerProperties(){
        List<String> propNames = new ArrayList<String>();
        for (Properties p : props){
            Enumeration keysEnum = p.keys();
            while (keysEnum.hasMoreElements()){
                String nextKey = (String)keysEnum.nextElement();
                if (!propNames.contains(nextKey)){
                    propNames.add(nextKey);
                }
            }
        }
        return propNames;
    }
    private void loadPropertiesFromFile(Properties p, String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while (null != (line = reader.readLine())){
                String[] parts = line.split("=");
                if (parts.length == 1){
                    p.setProperty(parts[0], "");
                }
                else {
                    p.setProperty(parts[0], parts[1]);
                }
                
            }
            reader.close();
        }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
            ioe.printStackTrace();
        }
    }
  
    private void summarizeLabelPresence(){
        try {
            String imageInfoDirPath = AvatolCVFileSystem.getNormalizedImageInfoDir();
            File dir = new File(imageInfoDirPath);
            File[] files = dir.listFiles();
            for (File f : files){
                Properties p = new Properties();
                loadPropertiesFromFile(p, f.getAbsolutePath());
                props.add(p);
            }
            List<String> propNames = registerProperties();
            Collections.sort(propNames);
            for (String name : propNames){
                System.out.print("For prop name " + name + " :  ");
                assessProperty(name);
            }
            System.out.println(props.size());
        }
        catch(AvatolCVException e){
            
        }
    }
}
