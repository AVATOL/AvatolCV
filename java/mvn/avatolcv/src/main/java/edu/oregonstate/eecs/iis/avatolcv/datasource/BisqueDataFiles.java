package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;

public class BisqueDataFiles extends AvatolCVDataFiles {
    
    public String getAnnotationInfoDir() throws AvatolCVException {
        return AvatolCVFileSystem.getSpecializedDataDir() + FILESEP + "annotationInfo";
    }
    
    public void persistAnnotationsForImage(List<BisqueAnnotation> annotations, String imageResource_uniq) throws AvatolCVException {
        String imageInfoRootDir = AvatolCVFileSystem.getSpecializedImageInfoDir();
        File f = new File(imageInfoRootDir);
        f.mkdirs();
       
        String path = imageInfoRootDir + FILESEP + imageResource_uniq + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write("#  imageResource_uniq: " + imageResource_uniq + NL);
            for (BisqueAnnotation ba : annotations){
                String name = ba.getName();
                String value = ba.getValue();
                String type = ba.getType();
                String created = ba.getCreated();
                String owner = ba.getOwner();
                String permission = ba.getPermission();
                String uri = ba.getUri();
                //if (name.equals("filename") || name.equalsIgnoreCase("upload_datetime")){
                    // skip these
                //}
                //else {
                    writer.write("name=" + name + ",value=" + value  + ",type=" + type + ",created=" + created + ",owner=" + owner + ",permission=" + permission + ",uri=" + uri + NL);
                //}
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem persisting imageInfo to filepath: " + path);
        }
    }
    List<BisqueAnnotation> loadAnnotationsForImage(String imageResource_uniq) throws AvatolCVException {
        List<BisqueAnnotation> annotations = new ArrayList<BisqueAnnotation>();
        String imageInfoRootDir = AvatolCVFileSystem.getSpecializedImageInfoDir();
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
                        String typeInfo = parts[2];
                        String createdInfo = parts[3];
                        String ownerInfo = parts[4];
                        String permissionInfo = parts[5];
                        String uriInfo = parts[6];
                        
                        String name = getValueFromKeyValueString(nameInfo);
                        String value = getValueFromKeyValueString(valueInfo);
                        String type = getValueFromKeyValueString(typeInfo);
                        String created = getValueFromKeyValueString(createdInfo);
                        String owner = getValueFromKeyValueString(ownerInfo);
                        String permission = getValueFromKeyValueString(permissionInfo);
                        String uri = getValueFromKeyValueString(uriInfo);

                        BisqueAnnotation ba = new BisqueAnnotation();
                        ba.setName(name);
                        ba.setValue(value);
                        ba.setType(type);
                        ba.setCreated(created);
                        ba.setOwner(owner);
                        ba.setPermission(permission);
                        ba.setUri(uri);
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
    public String getValueFromKeyValueString(String s){
        String value = "";
        String[] parts = s.split("=");
        if (parts.length > 1){
            value = parts[1];
        }
        return value;
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
    public String getPathForAnnotationInfo(String name, String annotationType) throws AvatolCVException {
        String annotationInfoRootDir = getAnnotationInfoDir();
        //String path = annotationInfoRootDir + FILESEP + name + "_" + annotationType + ".txt";
        String path = annotationInfoRootDir + FILESEP + name + ".txt";
        return path;
    }
}
