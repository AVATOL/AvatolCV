package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;

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
	public List<ChoiceItem> getScoringConcernItems(ScoringAlgorithms sa) throws AvatolCVException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInstructionsForScoringConcernScreen(ScoringAlgorithms sa) {
		// TODO Auto-generated method stub
		return null;
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

}
