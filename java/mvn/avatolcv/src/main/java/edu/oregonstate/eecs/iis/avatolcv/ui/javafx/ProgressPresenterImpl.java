package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.util.Hashtable;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;

public class ProgressPresenterImpl implements ProgressPresenter {
    private Hashtable<String,ProgressBar> progressBarForProcessNameHash = new Hashtable<String,ProgressBar>();
    private Hashtable<String,Label> labelForProcessNameHash = new Hashtable<String,Label>();
    public void connectProcessNameToProgressBar(String processName, ProgressBar pb){
        progressBarForProcessNameHash.put(processName, pb);
    }
    public void connectProcessNameToLabel(String processName, Label label){
        labelForProcessNameHash.put(processName, label);
    }
    @Override
    public void updateProgress(String processName, double percentDone) {
        ProgressUpdater pu = new ProgressUpdater(processName, percentDone);
        Platform.runLater(pu);

    }

    @Override
    public void setMessage(String processName, String m) {
        MessageUpdater mu = new MessageUpdater(processName,m);
        Platform.runLater(mu);

    }
    public class MessageUpdater implements Runnable {
        private String processName;
        private String message;
        public MessageUpdater(String processName, String message){
            this.processName = processName;
            this.message = message;
        }
        @Override
        public void run() {
            
            Label label = labelForProcessNameHash.get(processName);
            if (null != label){
                label.setText(message);
            }
        }
    }
    public class ProgressUpdater implements Runnable {
        private String processName;
        private double percent;
        public ProgressUpdater(String processName, double percent){
            this.processName = processName;
            this.percent = percent;
        }
        @Override
        public void run() {
            ProgressBar pb = progressBarForProcessNameHash.get(processName);
            if (null != pb){
                pb.setProgress((double)percent);
            }
        }
    }
}
