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
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
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
    private SessionImages sessionImages = null;

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
		    if (!datasetDirFile.getName().equals("unitTest") && datasetDirFile.isDirectory()){ // avoid files laid down by unitTesting
		        if (AvatolCVFileSystem.isDatasetLocal(datasetDirFile)){
	                DatasetInfo di = new DatasetInfo();
	                di.setName(datasetDirFile.getName());
	                di.setProjectID("Project_"+datasetDirFile.getName());
	                di.setID("ID_"+datasetDirFile.getName());
	                result.add(di);
	            }
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
	    	ChoiceItem ci = new ChoiceItem(key, false, true, key);
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
	public void setChosenScoringConcerns(List<ChoiceItem> items) throws AvatolCVException {
	    this.scoringConcerns = new ArrayList<NormalizedKey>();
	    for (ChoiceItem item : items){
	        this.scoringConcerns.add(item.getNormalizedKey());
	    }
	    narrowNormalizedImageInfos(this.scoringConcerns);
	}
	@Override
	public void setChosenScoringConcern(ChoiceItem item)  throws AvatolCVException {
	    this.scoringConcerns = new ArrayList<NormalizedKey>();
	    this.scoringConcerns.add(item.getNormalizedKey());
	    narrowNormalizedImageInfos(this.scoringConcerns);
	}
    
	private void narrowNormalizedImageInfos(List<NormalizedKey> scoringConcernKeys) throws AvatolCVException {
	    /*
	     * what I found was that 
                    a. Stemona image (ex) has no keys in Bisque DB
                    b. imageInfo file has no keys
                    c. scoredOutput file does - that is the file is scored regardless of lack of keys 
                    where are they added?
                    
                    this.niis.ensureAllKeysPresentInAllImageInfos(); is called in BisqueDS
                    
        Bisque downloads all images in the dataset
        MB loads images for just the chosen characters, and makes in-memory nii's just for those
        but if there is another session where different chars are chosen then additional images downloaded
        the only ramification for local copies is that character choices will cover the aggregate of what has been downloaded, so not a problem
        
        So, for copies of Bisque datasets, it will always have all the images from the original , which don't change from session to session
        AND, for copies of MB datasets, we may have images from combined sessions in play.  Since in MB we don't automatically add keys if 
        they aren't present in imageInfo, we MUST revise sessionImages to  only those containing keys corresponding to the chosen scoringConcers,
        Further, since this will be a noop for Bisque datasets, safe to do it there, too, nd thus for all local datasets - copies and orig's
        
        Seems like the most efficient thing to do is amend the NormalizedImageInfos after the scoringCOncerns are chosen, and at the same time adjust the sessionImages
            KEY FINDING -> thus sessionImages reduction only needs to happen for local datasets
        
         */ 
	    List<NormalizedImageInfo> niis = this.imageInfos.getNormalizedImageInfosForDataset();
	    List<String> niiFilenamesContainingScoringConcernKeys = new ArrayList<String>();
	    for (NormalizedImageInfo nii : niis){
	        for (NormalizedKey scoringConcern : this.scoringConcerns){
	            if (nii.hasKey(scoringConcern)){
	                if (!niiFilenamesContainingScoringConcernKeys.contains(nii.getNiiFilename())){
	                    niiFilenamesContainingScoringConcernKeys.add(nii.getNiiFilename());
	                }
	            }
	        }
	    }
	    this.imageInfos.focusToSession(niiFilenamesContainingScoringConcernKeys);
	    this.sessionImages.clear();
	    this.sessionImages.addAll(niiFilenamesContainingScoringConcernKeys);
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
	    this.sessionImages = sessionImages;
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
	public void setNormalizedImageInfos(NormalizedImageInfos niis) throws AvatolCVException {
		this.imageInfos = niis;
		if (isCurrentDatasetCopyOfBisqueDataset()){
		    this.imageInfos.ensureAllKeysPresentInAllImageInfos();
		}
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
	private boolean isCurrentDatasetCopyOfBisqueDataset() throws AvatolCVException  {
		String currentDatasetName = chosenDataset.getName();
		String currentDatasetPath = AvatolCVFileSystem.getSessionsRoot() + FILESEP + currentDatasetName;
		File datasetFile = new File(currentDatasetPath);
		if (AvatolCVFileSystem.isBisqueDatasetCopy(datasetFile)){
			return true;
		}
		return false;
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
    
    public String getImageStatusSummary(NormalizedImageInfos imageInfos) throws AvatolCVException {
        int imageCount = imageInfos.getDistinctImageCountForSession();
        List<NormalizedImageInfo> niis = imageInfos.getNormalizedImageInfosForSession();
        int scorableItemCount = niis.size();
        if (isCurrentDatasetCopyOfMorphobankDataset()){
            return scorableItemCount + " cells represented in this dataset for this session involving " + imageCount + " images";
        }
        else {
            return imageCount + " images present in dataset for this session";
        }
    }
	@Override
	public NormalizedValue getValueForKeyAtDatasourceForImage(
			NormalizedKey normCharKey, String imageID,
			NormalizedKey trainTestConcern,
			NormalizedValue trainTestConcernValue) throws AvatolCVException {
		// TODO Auto-generated method stub
		return null;
	}
    @Override
    public boolean reviseValueForKey(String imageID, NormalizedKey key,
            NormalizedValue value, NormalizedKey trainTestConcern,
            NormalizedValue trainTestConcernValue) throws AvatolCVException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean addKeyValue(String imageID, NormalizedKey key,
            NormalizedValue value, NormalizedKey trainTestConcern,
            NormalizedValue trainTestConcernValue) throws AvatolCVException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public List<String> filterBadSortCandidates(List<String> list) {
        return list;
    }

}
