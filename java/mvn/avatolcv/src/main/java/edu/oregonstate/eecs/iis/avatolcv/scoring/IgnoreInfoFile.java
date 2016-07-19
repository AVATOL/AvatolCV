package edu.oregonstate.eecs.iis.avatolcv.scoring;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class IgnoreInfoFile extends TrainingInfoFile {
    public static final String FILE_PREFIX = "ignore_";
    public IgnoreInfoFile(String scoringConcernType, String scoringConcernID, String scoringConcernName){
        super(scoringConcernType,scoringConcernID,scoringConcernName);
    }
    public IgnoreInfoFile(String pathname) throws AvatolCVException {
        super(pathname);
    }
    @Override
    public String getPrefix(){
        return FILE_PREFIX;
    }
}
