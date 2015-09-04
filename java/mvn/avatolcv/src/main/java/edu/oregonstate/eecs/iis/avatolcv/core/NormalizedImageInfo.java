package edu.oregonstate.eecs.iis.avatolcv.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class NormalizedImageInfo {
    //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
    //character:1824350|Diastema between I2 and C=characterState:4884329|Diastema present
    //taxon=773126|Artibeus jamaicensis
    //view=8905|Skull - ventral annotated teeth
    Hashtable<String, Object> keyValueHash = new Hashtable<String, Object>();
    private static final String KEY_ANNOTATION         = "avcv_annotation";
    private static final String KEY_SCORING_CONFIDENCE = "avcv_scoringConfidence";
    private static final String KEY_SCORE              = "avcv_score";
    private static final String KEY_TRUTH              = "avcv_truth";
    private static final String KEY_IMAGE_NAME         = "avcv_imageName";
    private static final String KEY_TRAINING_VS_TEST_CONCERN_VALUE  = "avcv_trainingVsTestConcernValue";
    public NormalizedImageInfo(String path) throws AvatolCVException {
        loadNormalizedInfoFromPath(path, "Problem loading Normalized Image Info file: ");
    }
    
    public class IdAndNameValue{
        private String id = null;
        private String name = null;
        public IdAndNameValue(String id, String name){
            this.id = id;
            this.name = name;
        }
        public String getID(){
            return this.id;
        }
        public String getName(){
            return this.name;
        }
    }
    private void loadNormalizedInfoFromPath(String path, String errorMessage)throws AvatolCVException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            while(null != (line = reader.readLine())){
                if (line.startsWith("#")){
                    // ignore
                }
                else {
                    if (line.startsWith("avcv")){
                        loadAvatolCVKeyedLine(line);
                    }
                    else {
                        String[] parts = line.split("=");
                        String key = parts[0];
                        String value = parts[1];
                        if (key.contains(":")){
                            // skip for now
                        }
                        else {
                            if (value.contains("|")){
                                String[] valueParts = value.split("|");
                                String id = valueParts[0];
                                String name = valueParts[1];
                                IdAndNameValue inv = new IdAndNameValue(id, name);
                                keyValueHash.put(key, inv);
                            }
                            else {
                                keyValueHash.put(key,value);
                            }
                        }
                    }
                }
            }
            reader.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException(errorMessage + path);
        }
    }
    public void overlayInfoFromFile(String path) throws AvatolCVException {
        loadNormalizedInfoFromPath(path, "Problem loading overlay Info file: ");
    }
    public boolean isScored(){
        return false;
    }
    public boolean isTraining(){
        return false;
    }
    public String getScoringConfidence(){
        return (String)keyValueHash.get(KEY_SCORING_CONFIDENCE);
    }
    public String getScore(){
        return (String)keyValueHash.get(KEY_SCORE);
    }
    public String getTruth(){
        return (String)keyValueHash.get(KEY_TRUTH);
    }
    public String getImageName(){
        return (String)keyValueHash.get(KEY_IMAGE_NAME);
    }
    public String getTrainingVsTestName(){
        return (String)keyValueHash.get(KEY_TRAINING_VS_TEST_CONCERN_VALUE);
    }
    private void loadAvatolCVKeyedLine(String line) throws AvatolCVException {
        String[] parts = line.split("=");
        String key = parts[0];
        String value = parts[1];
        if (key.equals(KEY_ANNOTATION)){
            //avcv_annotation=point:21.2571225071225,55.3632478632479+point:21.84729344729345,40.810256410256414
            // ...from MorphobankDataSource.java
            // avcv_annotation=rectangle:25,45;35,87+point:98,92
            // + delimits the annotations in the series
            // ; delimits the points in the annotation
            // , delimits x and y coordinates
            // : delimits type from points
            String[] annotationValueParts = value.split("+");
            for (String annotation : annotationValueParts){
                String[] annotationParts = annotation.split(":");
                String annotationType = annotationParts[0];
                String annotationPointSequence = annotationParts[1];
                String[] annotationPointPairs = annotationPointSequence.split(";");
                for (String pair : annotationPointPairs){
                    String[] pairParts = pair.split(",");
                    String x = pairParts[0];
                    String y = pairParts[1];
                    
                }
            }
        }
        else {
            throw new AvatolCVException("unrecognized avcv key");
        }
    }
}
