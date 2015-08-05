package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;

public class BisqueDataFiles implements AvatolCVDataFiles {
    private static final String FILESEP = System.getProperty("file.separator");
    private static final String NL = System.getProperty("line.separator");
    private String datasetDir = null;
    private String sessionDataRoot = null;
    @Override
    public void setSessionDataRoot(String sessionDataRoot) {
        this.sessionDataRoot = sessionDataRoot;
    }
    @Override
    public void setDatasetDirname(String datasetDirName) {
        this.datasetDir = this.sessionDataRoot + FILESEP + datasetDirName;
    }

    public String getImageInfoDir(){
        return datasetDir + FILESEP + "bisqueData" + FILESEP + "imageInfo";
    }

    public String getAnnotationInfoDir(){
        return datasetDir + FILESEP + "bisqueData" + FILESEP + "annotationInfo";
    }
    
    public void persistAnnotationsForImage(List<BisqueAnnotation> annotations, String imageResource_uniq) throws AvatolCVException {
        String imageInfoRootDir = getImageInfoDir();
        File f = new File(imageInfoRootDir);
        f.mkdirs();
       
        String path = imageInfoRootDir + FILESEP + imageResource_uniq + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("#  imageResource_uniq: " + imageResource_uniq + NL);
            for (BisqueAnnotation ba : annotations){
                String value = ba.getValue();
                String name = ba.getName();
                if (name.equals("filename") || name.equalsIgnoreCase("upload_datetime")){
                    // skip these
                }
                else {
                    writer.write("name=" + name + ",value=" + value  + NL);
                }
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
        }
    }
    List<BisqueAnnotation> loadAnnotationsForImage(String imageResource_uniq) throws AvatolCVException {
        List<BisqueAnnotation> annotations = new ArrayList<BisqueAnnotation>();
        String imageInfoRootDir = getImageInfoDir();
        String path = imageInfoRootDir + FILESEP + imageResource_uniq + ".txt";
        boolean foundFile = false;
        File f = new File(path);
        if (f.exists()){
            foundFile = true;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line = null;
                while (null != (line = reader.readLine())){
                    if (!line.startsWith("#")){
                        String[] parts = line.split(",");
                        String nameInfo = parts[0];
                        String valueInfo = parts[1];
                        String[] nameInfoParts = nameInfo.split("=");
                        String[] valueInfoParts = valueInfo.split("=");
                        String name = "";
                        if (nameInfoParts.length > 1){
                            name = nameInfoParts[1];
                        }
                        String value = "";
                        if (valueInfoParts.length > 1){
                            value = valueInfoParts[1];
                        }
                        BisqueAnnotation ba = new BisqueAnnotation();
                        ba.setName(name);
                        ba.setValue(value);
                        annotations.add(ba);
                    }
                }
                reader.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
            }
        }
        if (foundFile){
            return annotations;
        }
        return null;
    }
    public List<String> loadAnnotationValueOptions(String name, String annotationTypeValue)throws AvatolCVException {
        List<String> values = new ArrayList<String>();
        String path = getPathForAnnotationInfo(name, annotationTypeValue);
        boolean foundFile = false;
        File f = new File(path);
        if (f.exists()){
            foundFile = true;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(path));
                String line = null;
                while (null != (line = reader.readLine())){
                    if (!line.startsWith("#")){
                        String[] parts = line.split("=");
                        String value = parts[1];
                        values.add(value);
                    }
                }
                reader.close();
            }
            catch(IOException ioe){
                throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
            }
        }
        if (foundFile){
            return values;
        }
        return null;
    }
    public void persistAnnotationValueOptions(String name, String annotationTypeValue, List<String> values) throws AvatolCVException {
        String annotationInfoRootDir = getAnnotationInfoDir();
        File f = new File(annotationInfoRootDir);
        f.mkdirs();
       
        String path = getPathForAnnotationInfo(name, annotationTypeValue);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("#  name: " + name + " type: " + annotationTypeValue + NL);
            for (String s : values){
                writer.write("value=" + s + NL);
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting annotationInfo to filepath: " + path);
        }
    }
    public String getPathForAnnotationInfo(String name, String annotationType){
        String annotationInfoRootDir = getAnnotationInfoDir();
        String path = annotationInfoRootDir + FILESEP + name + "_" + annotationType + ".txt";
        return path;
    }
}
