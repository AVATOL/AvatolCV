package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringScope;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm.ScoringSessionFocus;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionImages;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;

public class FileSystemDataSource implements DataSource {
	private static final String FILESEP = System.getProperty("file.separator");
	private DatasetInfo chosenDataset = null;
	private NormalizedImageInfos imageInfos = null;
	private List<NormalizedKey> scoringConcerns = null;
    public FileSystemDataSource(){
        
    }
	@Override
	public String getName() {
		return "local";
	}

	@Override
	public boolean authenticate(String username, String password)
			throws AvatolCVException {
		return true;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	
	@Override
	public List<DatasetInfo> getDatasets() throws AvatolCVException {
		List<DatasetInfo> result = new ArrayList<DatasetInfo>();
		String sessionsRoot = AvatolCVFileSystem.getSessionsRoot();
		File sessionsFile = new File(sessionsRoot);
		File[] datasetDirFiles = sessionsFile.listFiles();
		for (File datasetDirFile: datasetDirFiles){
			if (AvatolCVFileSystem.isDatasetLocal(datasetDirFile)){
				DatasetInfo di = new DatasetInfo();
				di.setName(datasetDirFile.getName());
				di.setProjectID("Project_"+datasetDirFile.getName());
				di.setID("ID_"+datasetDirFile.getName());
				result.add(di);
			}
		}
		return result;
	}

	@Override
	public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp,
			String processName) throws AvatolCVException {
		// nothing to do
	}

	@Override
	public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp,
			String processName) throws AvatolCVException {
		// nothing to do
	}

	@Override
	public String getDefaultUsername() {
		return "NA";
	}

	@Override
	public String getDefaultPassword() {
		return "NA";
	}

	@Override
	public void setChosenDataset(DatasetInfo di) throws AvatolCVException  {
		this.chosenDataset = di;
		//String datasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + di.getName();
		//String imageInfoPath = datasetPath + FILESEP + "normalized" + FILESEP + "imageInfo";
		//imageInfos = new NormalizedImageInfos(imageInfoPath);
	}
    
    /**
     * for local dataset, we won't prefill any values here, just show list or show dropdown depending on multiple_item vs single
     */
	@Override
	public List<ChoiceItem> getScoringConcernOptions(ScoringScope scoringScope,
			ScoringSessionFocus scoringFocus) throws AvatolCVException {
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
		List<NormalizedKey> scorableKeys = imageInfos.getScorableKeys();
	    for (NormalizedKey key : scorableKeys){
	    	ChoiceItem ci = new ChoiceItem(key, false, true, key.getName());
	    	result.add(ci);
	    }
	    return result;
	}

	@Override
	public String getInstructionsForScoringConcernScreen(ScoringScope scoringScope,
			ScoringSessionFocus focus) {
		if (scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
            return "Place a check mark next to the characters to score";
        }
        else {
            return "Select the character to score";
        }
	}

	@Override
	public void setChosenScoringConcerns(List<ChoiceItem> items) {
	    this.scoringConcerns = new ArrayList<NormalizedKey>();
	    for (ChoiceItem item : items){
	        this.scoringConcerns.add(item.getNormalizedKey());
	    }
	}
	@Override
	public void setChosenScoringConcern(ChoiceItem item) {
	    this.scoringConcerns = new ArrayList<NormalizedKey>();
	    this.scoringConcerns.add(item.getNormalizedKey());
	}

	@Override
	public String getDatasetSummaryText() {
		return "not yet implemented";
	}

	@Override
	public AvatolCVDataFiles getAvatolCVDataFiles() {//dead code
		return null;
	}

	@Override
	public void setSessionImages(SessionImages sessionImages) {
	}

	@Override
	public void acceptFilter() {
		
	}

	@Override
	public void downloadImages(ProgressPresenter pp, String processName)
			throws AvatolCVException {
		// NA
	}

	@Override
	public String getDatasetTitleText() {
		// TODO Auto-generated method stub
		return "localFileSystem";
	}

	@Override
	public void setNormalizedImageInfos(NormalizedImageInfos niis) {
		this.imageInfos = niis;
	}

	@Override
	public String getDefaultTrainTestConcern() {
		try {
			if (isCurrentDatasetCopyOfMorphobankDataset()){
				return "taxon";
			}
			return null;
		}
		catch (AvatolCVException ace){
			System.out.println("problem determining default trainTest concern for LocalDataSource." + ace.getMessage());
			return null;
		}
		
	}
	private boolean isCurrentDatasetCopyOfMorphobankDataset() throws AvatolCVException  {
		String currentDatasetName = chosenDataset.getName();
		String currentDatasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + currentDatasetName;
		File datasetFile = new File(currentDatasetPath);
		if (AvatolCVFileSystem.isMorphobankDatasetCopy(datasetFile)){
			return true;
		}
		return false;
	}



}
