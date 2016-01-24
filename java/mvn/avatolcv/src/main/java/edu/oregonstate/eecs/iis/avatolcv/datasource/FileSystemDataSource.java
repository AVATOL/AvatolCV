package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringSessionFocus;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;

public class FileSystemDataSource implements DataSource {

    @Override
    public boolean authenticate(String username, String password) {
        // nothing to authenticate
        return true;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

	@Override
	public List<DatasetInfo> getDatasets() throws AvatolCVException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp, String processName) throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp, String processName)
			throws AvatolCVException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDefaultUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setChosenDataset(DatasetInfo di) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChosenScoringConcerns(List<ChoiceItem> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChosenScoringConcern(ChoiceItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDatasetSummaryText() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ChoiceItem> getScoringConcernOptions(ScoringScope scope,
            ScoringSessionFocus focus) throws AvatolCVException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getInstructionsForScoringConcernScreen(ScoringScope scope,
            ScoringSessionFocus focus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AvatolCVDataFiles getAvatolCVDataFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataFilter getDataFilter(String specificSessionDir)
            throws AvatolCVException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void acceptFilter() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void downloadImages(ProgressPresenter pp, String processName)
            throws AvatolCVException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDatasetTitleText() {
        // TODO Auto-generated method stub
        return null;
    }

}
