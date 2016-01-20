package edu.oregonstate.eecs.iis.avatolcv.results;

public class ResultsRow {
    
    private String thumbnailPathname = null;
    private String origImageName = null;
    private String score = null;
    private String conf = null;
    private String truth = null;
    private String trainTestConcernValue = null;
    
    
    private Object thumbnailPathnameWidget = null;
    private Object origImageNameWidget = null;
    private Object scoreWidget = null;
    private Object confWidget = null;
    private Object truthWidget = null;
    private Object trainTestConcernValueWidget = null;
    
    public ResultsRow(String thumbnailPathname, String origImageName, String score, String conf, String truth, String trainTestConcernValue){
        this.thumbnailPathname = thumbnailPathname;
        this.origImageName = origImageName;
        this.score = score;
        this.conf = conf;
        this.truth = truth;
        this.trainTestConcernValue = trainTestConcernValue;
    }
    public String getThumbnailPathname() {
        return thumbnailPathname;
    }
    public String getOrigImageName() {
        return origImageName;
    }
    public String getScore() {
        return score;
    }
    public String getConf() {
        return conf;
    }
    public String getTruth() {
        return truth;
    }
    public String getTrainTestConcernValue() {
        return trainTestConcernValue;
    }
    public Object getThumbnailPathnameWidget() {
        return thumbnailPathnameWidget;
    }
    public void setThumbnailPathnameWidget(Object thumbnailPathnameWidget) {
        this.thumbnailPathnameWidget = thumbnailPathnameWidget;
    }
    public Object getOrigImageNameWidget() {
        return origImageNameWidget;
    }
    public void setOrigImageNameWidget(Object origImageNameWidget) {
        this.origImageNameWidget = origImageNameWidget;
    }
    public Object getScoreWidget() {
        return scoreWidget;
    }
    public void setScoreWidget(Object scoreWidget) {
        this.scoreWidget = scoreWidget;
    }
    public Object getConfWidget() {
        return confWidget;
    }
    public void setConfWidget(Object confWidget) {
        this.confWidget = confWidget;
    }
    public Object getTruthWidget() {
        return truthWidget;
    }
    public void setTruthWidget(Object truthWidget) {
        this.truthWidget = truthWidget;
    }
    public Object getTrainTestConcernValueWidget() {
        return trainTestConcernValueWidget;
    }
    public void setTrainTestConcernValueWidget(Object trainTestConcernValueWidget) {
        this.trainTestConcernValueWidget = trainTestConcernValueWidget;
    }
}
