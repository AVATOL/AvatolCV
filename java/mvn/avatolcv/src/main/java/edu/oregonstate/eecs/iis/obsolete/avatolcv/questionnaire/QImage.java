package edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire;

import edu.oregonstate.eecs.iis.avatolcv.Platform;

public class QImage {
    private String path = null;
    private String caption = null;
    public QImage(String path, String caption){
        if (Platform.isWindows()){
            path = Platform.convertUnixPathToWindows(path);
        }
        this.path = path;
        this.caption = caption;
    }
    public String getPath(){
        return this.path;
    }
    public String getCaption(){
        return this.caption;
    }
}
