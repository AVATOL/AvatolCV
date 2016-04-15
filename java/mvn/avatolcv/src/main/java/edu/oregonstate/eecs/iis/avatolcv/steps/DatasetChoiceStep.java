package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Defaults;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

public class DatasetChoiceStep extends Answerable implements Step {
    private DatasetInfo chosenDataset = null;
    private List<DatasetInfo> datasets = null;
    private SessionInfo sessionInfo = null;
    private Hashtable<String,String> priorAnswers = null;
    private static final Logger logger = LogManager.getLogger(DatasetChoiceStep.class);

    public DatasetChoiceStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
        // nothing to do
    }
    public List<String> getAvailableDatasets() throws AvatolCVException {
        List<String> result = new ArrayList<String>();
        this.datasets = this.sessionInfo.getDataSource().getDatasets();
        //Collections.sort(this.datasets);
        for (DatasetInfo di : this.datasets){
            String name = di.getName();
            result.add(name);
        }
        Collections.sort(result);
        return result;
    }
    public void setChosenDataset(String s) throws AvatolCVException {
        this.chosenDataset = null;
        
        for (DatasetInfo di : this.datasets){
            String name = di.getName();
            if (name.equals(s)){
                this.chosenDataset = di;
                logger.info("chosenDataset : " + s);
            }
        }
        if (this.chosenDataset == null){
            throw new AvatolCVException("no DatasetInfo match for name " + s);
        }
    }
    public String getDatasetTitleText(){
        return this.sessionInfo.getDataSource().getDatasetTitleText();
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.sessionInfo.setChosenDataset(this.chosenDataset);
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return true;
    }
    public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException {
        this.sessionInfo.getDataSource().loadPrimaryMetadataForChosenDataset(pp, processName);
        
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
        return true;
    }
	@Override
	public boolean shouldRenderIfBackingIntoIt() {
		return true;
	}
}
