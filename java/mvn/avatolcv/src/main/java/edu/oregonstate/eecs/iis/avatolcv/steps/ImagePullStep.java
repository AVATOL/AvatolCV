package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVExceptionExpresser;
import edu.oregonstate.eecs.iis.avatolcv.datasource.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.AvatolCVExceptionExpresserJavaFX;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ui.javafx.ImagePullStepController;

public class ImagePullStep  extends Answerable implements Step {
    private SessionInfo sessionInfo;
    public ImagePullStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // nothing to do

    }
    public SessionInfo getSessionInfo(){
        return this.sessionInfo;
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
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
		return false;
	}
	@Override
	public List<DataIssue> getDataIssues() throws AvatolCVException{
		return null;
	}
}
