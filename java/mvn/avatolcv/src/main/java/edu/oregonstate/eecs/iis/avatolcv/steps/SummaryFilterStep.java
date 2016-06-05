package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter.FilterItem;

public class SummaryFilterStep  extends Answerable implements Step {
    private SessionInfo sessionInfo;
    private static final Logger logger = LogManager.getLogger(SummaryFilterStep.class);

    public SummaryFilterStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() throws AvatolCVException {
    	
    }

    public DataFilter getDataFilter() throws AvatolCVException {
    	this.sessionInfo.reAssessImagesInPlay();
    	return this.sessionInfo.getDataFilter();
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        logger.info("consuming filter settings");
    	DataFilter df = this.sessionInfo.getDataFilter();
    	List<FilterItem> items = df.getItems();
    	for (FilterItem item : items){
    		NormalizedKey key = item.getKey();
			NormalizedValue value = item.getValue();
			NormalizedImageInfos niis = this.sessionInfo.getNormalizedImageInfos();
			List<NormalizedImageInfo> niisForKeyValue = niis.getSessionNIIsForKeyValue(key, value);
			for (NormalizedImageInfo nii : niisForKeyValue){
				if (item.isSelected()){
	    			nii.excludeForSession("filter");
	    		}
	    		else {
	    			nii.undoExcludeForSession("filter");
	    		}
			}
    	}
    }

    @Override
    public boolean hasFollowUpDataLoadPhase() {
        return false;
    }

    public String getSummaryText(){
        return this.sessionInfo.getDataSource().getDatasetSummaryText();
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
    	return !SessionInfo.isBisqueSession();
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
    	return !SessionInfo.isBisqueSession();
	}
    @Override
	public List<DataIssue> getDataIssues() throws AvatolCVException {
		return sessionInfo.checkDataIssues();
	}
}
