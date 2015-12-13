package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter.FilterItem;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;

public class SummaryFilterStep  extends Answerable implements Step {
    private SessionInfo sessionInfo;
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
        return true;
    }
}
