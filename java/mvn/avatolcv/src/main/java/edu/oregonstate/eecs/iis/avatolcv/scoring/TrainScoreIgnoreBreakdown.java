package edu.oregonstate.eecs.iis.avatolcv.scoring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;

public class TrainScoreIgnoreBreakdown {
    public enum TreatmentOption {
        TRAIN,
        SCORE,
        IGNORE
    };
    private static final String NL = System.getProperty("line.separator");
    private Hashtable<String,TreatmentOption> treatmentHash = new Hashtable<String, TreatmentOption>();
    private List<String> nvNames = new ArrayList<String>();
    public void setTreatmentOptionForNormalizedValue(NormalizedValue nv, TreatmentOption treatmentOption){
        String nvName = nv.getName();
        if (!nvNames.contains(nvName)){
            nvNames.add(nvName);
        }
        treatmentHash.put(nvName,  treatmentOption);
    }
    public void persist() throws AvatolCVException {
        String path = AvatolCVFileSystem.getTrainScoreIgnorePath();
        try {
            File f = new File(path);
            if (f.exists()){
                f.delete();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            Collections.sort(nvNames);
            int countIgnore = 0;
            int countTest   = 0;
            int countTrain  = 0;
            for (String nvName : nvNames){
                TreatmentOption to = treatmentHash.get(nvName);
                if (to == TreatmentOption.IGNORE){
                    countIgnore++;
                }
                else if (to == TreatmentOption.SCORE){
                    countTest++;
                }
                else {
                    countTrain++;
                }
            }
            writer.write("IGNORING : " + countIgnore + NL);
            for (String nvName : nvNames){
                TreatmentOption to = treatmentHash.get(nvName);
                if (to == TreatmentOption.IGNORE){
                    writer.write("    " + nvName + NL);
                }
            }
            writer.write(NL);
            writer.write(NL);
            writer.write("TRAINING : " + countTrain + NL);
            for (String nvName : nvNames){
                TreatmentOption to = treatmentHash.get(nvName);
                if (to == TreatmentOption.TRAIN){
                    writer.write("    " + nvName + NL);
                }
            }
            writer.write(NL);
            writer.write(NL);
            writer.write("SCORING : " + countTest + NL);
            for (String nvName : nvNames){
                TreatmentOption to = treatmentHash.get(nvName);
                if (to == TreatmentOption.SCORE){
                    writer.write("    " + nvName + NL);
                }
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("Cannot create trainScoreIgnore breakdown file for evaluation run", ioe);
        }
        
    }
}
