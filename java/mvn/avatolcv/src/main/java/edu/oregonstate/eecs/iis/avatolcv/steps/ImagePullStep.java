package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.Hashtable;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ImagePullStepController;

public class ImagePullStep  extends Answerable implements Step {
    private SessionInfo sessionInfo;
    public ImagePullStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // Tnothing to do

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // no input provided, just loading images
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }

    public void downloadImages(ImagePullStepController controller, String processName) throws AvatolCVException {
        try {
            DataSource dataSource = sessionInfo.getDataSource();
            dataSource.downloadImages(controller, processName);
        }
        catch(Exception e){
            AvatolCVExceptionExpresserJavaFX.instance.showException(e, "Problem encountered loading images...");
        }
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
}
