package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.ImagePullStep;

public class ImagePullStepController implements StepController, ProgressPresenter{
    public static final String IMAGE_FILE_DOWNLOAD = "imageFileDownload"; 

    private ImagePullStep step;
    private String fxmlDocName;
    private JavaFXStepSequencer fxSession;
    public ProgressBar imageFileDownloadProgress;
    public Label imageFileDownloadMessage;
    
    public ImagePullStepController(JavaFXStepSequencer fxSession, ImagePullStep step, String fxmlDocName){
        this.step = step;
        this.fxmlDocName = fxmlDocName;
        this.fxSession = fxSession;
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

    @Override
    public boolean consumeUIData() {
    	Hashtable<String, String> answerHash = new Hashtable<String, String>();
		this.step.saveAnswers(answerHash);
        return true;
    }

    @Override
    public void clearUIFields() {
        imageFileDownloadProgress.setProgress(0.0);
        imageFileDownloadMessage.setText("");
        
    }

    @Override
    public Node getContentNode() throws AvatolCVException {
        try {
            System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            
            
            
            //imageInfoDownloadProgress.setProgress(0.0);
            //imageInfoDownloadMessage.setText("");
            
            imageFileDownloadProgress.setProgress(0.0);
            imageFileDownloadMessage.setText("");
            
            Task<Boolean> task = new ImageDownloadTask(this, this.step, IMAGE_FILE_DOWNLOAD);
            /*
             * NOTE - wanted to use javafx properties and binding here but couldn't dovetail it in.  I could not put
             * the loops that do work in the call method of the Task (which manages updating on the JavaFX APp Thread), 
             * I had to manage this myself the old school way, to update the progress bar, by using Platform.runLater
             * 
             */
            //imageFileDownloadProgress.progressProperty().bind(fileDownloadPercentProperty);
            //imageInfoDownloadProgress.progressProperty().bind(infoDownloadPercentProperty);
            //imageFileDownloadMessage.textProperty().bind(fileMessageProperty);
            //imageInfoDownloadMessage.textProperty().bind(infoMessageProperty);
            new Thread(task).start();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        } 
    }

    public class NavButtonEnablerRunner implements Runnable{
        @Override
        public void run() {
            fxSession.enableNavButtons();
        }
        
    }
    @Override
    public boolean delayEnableNavButtons() {
        return true;
    }

    @Override
    public void executeFollowUpDataLoadPhase() throws AvatolCVException {
        // NA
    }

    @Override
    public void configureUIForFollowUpDataLoadPhase() {
        // NA
    }

    @Override
    public boolean isFollowUpDataLoadPhaseComplete() {
        // NA
        return false;
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
            if (IMAGE_FILE_DOWNLOAD.equals(processName)){
                System.out.println("should have setr progress to " + percent);
                imageFileDownloadProgress.setProgress((double)percent);
            }
        }
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
            if (IMAGE_FILE_DOWNLOAD.equals(processName)){
                imageFileDownloadMessage.setText(message);
            }
        }
    }
    public class ImageDownloadTask extends Task<Boolean> {
        private String processName;
        private ImagePullStep step;
        private ImagePullStepController controller;
        private final Logger logger = LogManager.getLogger(ImageDownloadTask.class);
        
        public ImageDownloadTask(ImagePullStepController controller, ImagePullStep step, String processName){
            this.controller = controller;
            this.step = step;
            this.processName = processName;
        }
        @Override
        protected Boolean call() throws Exception {
            try {
                this.step.downloadImages(this.controller, processName, fxSession.getExceptionExpresser());
                NavButtonEnablerRunner runner = new NavButtonEnablerRunner();
                Platform.runLater(runner);
                return new Boolean(true);
            }
            catch(AvatolCVException ace){
            	
                logger.error("AvatolCV error downloading images");
                logger.error(ace.getMessage());
                System.out.println("AvatolCV error downloading images");
                ace.printStackTrace();
                fxSession.getExceptionExpresser().showException(ace, "problem downloading image");
                return new Boolean(false);
            }
        }
       
    }
}
