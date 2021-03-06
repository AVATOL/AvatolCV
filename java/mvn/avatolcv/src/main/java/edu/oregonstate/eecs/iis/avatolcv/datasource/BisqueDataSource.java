package edu.oregonstate.eecs.iis.avatolcv.datasource;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVFileSystem;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.ScoringAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.core.Defaults;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedImageInfosToReview;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoreIndex;
import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringConcernDetails;
import edu.oregonstate.eecs.iis.avatolcv.session.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.session.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.session.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionImages;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BisqueDataSource implements DataSource {
    private static final String FILESEP = System.getProperty("file.separator");
    private BisqueWSClient wsClient = null;
    private DatasetInfo chosenDataset = null;
    private List<BisqueImage> bisqueImagesForCurrentDataset = null;
    private Hashtable<String, List<BisqueAnnotation>> annotationsForImageIdHash = null;
    private List<NormalizedKey> scoringConcernAnnotations = null;
    private Hashtable<String, List<String>> valuesForEnumAnnotation = null;
    private BisqueDataFiles bisqueDataFiles = null;
    private DataFilter dataFilter = null;
    private BisqueImages bisqueImages = null;
    private NormalizedImageInfos niis = null;
    private SessionImages sessionImages = null;
    private List<BisqueDataset> allDataSets = null;
    public BisqueDataSource(){
        
        wsClient = new BisqueWSClientImpl();
        bisqueDataFiles = new BisqueDataFiles();
        //bisqueDataFiles.setSessionsRoot(sessionsRoot);
    }
    @Override
    public void setSessionImages(SessionImages sessionImages){
    	this.sessionImages = sessionImages;
    }
    @Override
    public boolean authenticate(String username, String password) throws AvatolCVException {
        boolean result = false;
        try {
            result = this.wsClient.authenticate(username, password);
        }
        catch(BisqueWSException e){
            throw new AvatolCVException(e.getMessage(), e);
        }
        return result;
    }
    @Override
    public boolean isAuthenticated() {
        return this.wsClient.isAuthenticated();
    }
    
    @Override
    public List<DatasetInfo> getDatasets() throws AvatolCVException {
        List<DatasetInfo> datasets = new ArrayList<DatasetInfo>();
        try {
            allDataSets = wsClient.getDatasets();
            
            for (BisqueDataset ds : allDataSets){
                DatasetInfo di = new DatasetInfo();
                di.setName(ds.getName());
                di.setID(ds.getResourceUniq());
                di.setProjectID(DatasetInfo.NO_CONTAINING_PROJECT_ID);
                di.setDatasetLabel("dataset");
                datasets.add(di);
            }
            Collections.sort(datasets);
            return datasets;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem loading datasets from Bisque ", e);
        }
    }
   
    @Override
    public String getDefaultUsername() {
        String result = Defaults.instance.getBisqueLogin();
        if (null == result){
            result = "";
        }
        return result;
    }
    @Override
    public String getDefaultPassword() {
        String result = Defaults.instance.getBisquePassword();
        if (null == result){
            result = "";
        }
        return result;
    }
    @Override
    public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        String datasetResource_uniq = this.chosenDataset.getID();
        annotationsForImageIdHash = new Hashtable<String,List<BisqueAnnotation>>();
        try {
            pp.setMessage(processName, "loading info about images...");
            pp.updateProgress(processName, 0.0);
            this.bisqueImagesForCurrentDataset = this.wsClient.getImagesForDataset(datasetResource_uniq);
            
            pp.setMessage(processName, "loading metadata for each image...");
            pp.updateProgress(processName, 0.1);
            double count = this.bisqueImagesForCurrentDataset.size();
            double percentProgressPerImage = 0.9 / count;
            int curCount = 0;
            for (BisqueImage bi : this.bisqueImagesForCurrentDataset){
                curCount++;
                pp.setMessage(processName, "loading metadata for image: " + bi.getName());
                String imageResource_uniq = bi.getResourceUniq();
                List<BisqueAnnotation> annotations = this.bisqueDataFiles.loadAnnotationsForImage(imageResource_uniq);
             
                if (null == annotations){
                    annotations = this.wsClient.getAnnotationsForImage(imageResource_uniq);
                    this.bisqueDataFiles.persistAnnotationsForImage(annotations, imageResource_uniq);
                }
                String niiFilename = createNormalizedImageInfoForSession(bi, annotations, scoringConcernAnnotations);
                if (!sessionImages.contains(niiFilename)){
                    this.sessionImages.add(niiFilename);
                }    
                annotationsForImageIdHash.put(imageResource_uniq, annotations);
                pp.updateProgress(processName, 0.1 + (percentProgressPerImage * curCount));
                
            }
            pp.setMessage(processName, "repair empty metadata files");
            this.niis.ensureAllKeysPresentInAllImageInfos();
            pp.setMessage(processName, "finished!");
        }
        catch(BisqueWSException e){
            e.printStackTrace();
            throw new AvatolCVException("problem loading primary metadata from Bisque: " + e.getMessage(), e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new AvatolCVException("problem loading primary metadata from Bisque: " + e.getMessage(), e);
        }
    }
    @Override
    public void setChosenDataset(DatasetInfo di) {
        this.chosenDataset = di;
        //this.bisqueDataFiles.setDatasetDirname(di.getName());
    }
    @Override
    public List<ChoiceItem> getScoringConcernOptions(ScoringAlgorithm.ScoringScope scoringScope, ScoringAlgorithm.ScoringSessionFocus scoringFocus)
            throws AvatolCVException {
        List<ChoiceItem> items = new ArrayList<ChoiceItem>();
        List<String> annotationKeys = new ArrayList<String>();
        Hashtable<String, String> annotationIDforNameHash = new Hashtable<String, String>();
        for (BisqueImage bi : this.bisqueImagesForCurrentDataset){
            String imageID = bi.getResourceUniq();
            //System.out.println(imageID);
            List<BisqueAnnotation> annotations = annotationsForImageIdHash.get(imageID);
            
            for (BisqueAnnotation a : annotations){
                String annotationName = a.getName();
                String annotationId = a.getAnnotationID();
                /*
                 * HACK TO DEAL WITH bisque data that was saved not having the ID correct
                if (annotationName.equalsIgnoreCase("leaf apex angle")){
                	annotationId = "7231166";
                }
                */
                String annotationKey = NormalizedTypeIDName.buildTypeIdName(NormalizedTypeIDName.TYPE_UNSPECIFIED, annotationId, annotationName);
                annotationIDforNameHash.put(annotationName, annotationId);
                if (annotationName.equals("filename") || annotationName.equals("upload_datetime")){
                    // don't present these as potential scoring concerns
                }
                else if (!annotationKeys.contains(annotationKey)){
                    annotationKeys.add(annotationKey);
                }
            }
        }
        for (String annotationKey : annotationKeys){
        	NormalizedKey normalizedAnnotationKey = new NormalizedKey(annotationKey);
        	String annotationName = normalizedAnnotationKey.getName();
            ChoiceItem ci = new ChoiceItem(normalizedAnnotationKey, false, false, new ScoringConcernDetailsImpl(annotationName, annotationIDforNameHash.get(annotationName)));
            items.add(ci);
        }
        return items;
    }
    public class ScoringConcernDetailsImpl implements ScoringConcernDetails {
        private String name = null;
        private String id = null;
        public ScoringConcernDetailsImpl(String name, String id){
            this.name = name;
            this.id = id;
        }
        @Override
        public String getType() {
            return NormalizedTypeIDName.TYPE_UNSPECIFIED;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
        
    }
    @Override
    public String getInstructionsForScoringConcernScreen(ScoringAlgorithm.ScoringScope scoringScope, ScoringAlgorithm.ScoringSessionFocus scoringFocus) {
        if (scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
            return "Place a check mark next to the characters to score";
        }
        else {
            return "Select the character to score";
        }
        
    }

    @Override
    public void setChosenScoringConcerns(List<ChoiceItem> items) {
        this.scoringConcernAnnotations = new ArrayList<NormalizedKey>();
        for (ChoiceItem item : items){
            this.scoringConcernAnnotations.add(item.getNormalizedKey());
        }
    }
    @Override
    public void setChosenScoringConcern(ChoiceItem item) {
        this.scoringConcernAnnotations = new ArrayList<NormalizedKey>();
        this.scoringConcernAnnotations.add(item.getNormalizedKey());
    }
    @Override
    public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        try {
            pp.setMessage(processName, "loading info about images...");
            pp.updateProgress(processName, 0.0);
            int totalCount = 0;
            for (BisqueImage image : this.bisqueImagesForCurrentDataset){
                String id = image.getResourceUniq();
                List<BisqueAnnotation> annotations = annotationsForImageIdHash.get(id);
                totalCount += annotations.size();
            }
            double increment = 1.0 / totalCount;
            List<String> typesSeen = new ArrayList<String>();
            this.valuesForEnumAnnotation = new Hashtable<String, List<String>>();
            int count = 0;
            for (BisqueImage image : this.bisqueImagesForCurrentDataset){
                String id = image.getResourceUniq();
                List<BisqueAnnotation> annotations = annotationsForImageIdHash.get(id);
                for (BisqueAnnotation annotation : annotations){
                    pp.setMessage(processName, "loading info about annotation " + annotation.getName());
                    if (annotation.hasTypeValueConsistentWithComboBox()){
                        String annotationTypeValue = annotation.getType();
                        if (typesSeen.contains(annotationTypeValue)){
                            // don't look it up again
                        }
                        else {
                            List<String> values = this.bisqueDataFiles.loadAnnotationValueOptions(annotation.getName(), annotationTypeValue);
                            if (null == values){
                                values = this.wsClient.getAnnotationValueOptions(annotation.getName(),annotationTypeValue);
                                this.bisqueDataFiles.persistAnnotationValueOptions(annotation.getName(), annotationTypeValue, values);
                            }
                            this.valuesForEnumAnnotation.put(annotation.getName(), values);
                        }
                    }
                    pp.updateProgress(processName, ++count * increment);

                }
            }
        }
        catch(BisqueWSException e){
            e.printStackTrace();
            throw new AvatolCVException("problem loading primary metadata from Bisque: " + e.getMessage(), e);
        }
        
    }
    private static final String NL = System.getProperty("line.separator");

    @Override
    public String getDatasetSummaryText() {        
        StringBuilder sb = new StringBuilder();
        sb.append("Dataset: " + this.chosenDataset.getName() + NL);
        // how many images in play
        //LEFT OFF HERE  NormalizedImageInfosToReview normalizedImageInfos = new NormalizedImageInfosToReview(runID);
        // list each character, it's possible values, and how many images have that associated
        
        // for each of those, how many are scored vs not
        
        
        sb.append(" add some more text" + NL);
        
        sb.append(NL);
        return "" + sb;
    }
    @Override
    public AvatolCVDataFiles getAvatolCVDataFiles() {
        return (AvatolCVDataFiles)this.bisqueDataFiles;
    }
    
    @Override
    public void acceptFilter() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public String getName() {
        return "bisque";
    }
   
    public String createNormalizedImageInfoForSession(BisqueImage bi,List<BisqueAnnotation> annotations, List<NormalizedKey> chosenScoringConcerns) throws AvatolCVException {
        String imageId = bi.getResourceUniq();
        List<String> lines = new ArrayList<String>();
        for (BisqueAnnotation ba : annotations){
            String name = ba.getName();
            String id = ba.getAnnotationID();
            //if (!id.equals("null") && !id.equals("datetime")){
            //	int foo = 3;
            //}
            String keyString = NormalizedTypeIDName.buildTypeIdName(NormalizedTypeIDName.TYPE_UNSPECIFIED, id, name);
            //System.out.println("KEY STRING WAS : " + keyString);
            /*
            // hack to weave together the entries that were saved that have non-existent type ID
            if (keyString.equals("?:|leaf apex angle")){
            	keyString = "?:7231166|leaf apex angle";
            }
            if (keyString.equals("?:?|leaf apex angle")){
            	keyString = "?:7231166|leaf apex angle";
            } 
            if (keyString.equals(":|leaf apex angle")){
            	keyString = "?:7231166|leaf apex angle";
            }
            if (keyString.contains("leaf apex angle")){
            	if (!keyString.equals("?:7231166|leaf apex angle")){
            		System.out.println("KEY STRING WRONG  : " + keyString);
            	}
            }
            

            //end hack
             */
            if (name.equals("filename")){
                keyString = NormalizedImageInfo.KEY_IMAGE_NAME;
            } 
            else if (name.equals("upload_datetime")){
                keyString = NormalizedImageInfo.KEY_TIMESTAMP;
            }
            // don't know scoring concern yet, so just use the ScoreIndex from the score file
            //for (String s : chosenScoringConcerns){
            //    if (name.equals(s)){
            //        p.setProperty(NormalizedImageInfo.KEY_SCORING_CONCERN_LOCATION, name+":key");
            //        p.setProperty(NormalizedImageInfo.KEY_SCORING_VALUE_LOCATION, name+":value");
            //    }
            //}
            
            String value = ba.getValue();
            String normalizedValueString = new NormalizedValue(value).toString();
            lines.add(keyString+"="+normalizedValueString);
        }
        String path = this.niis.createNormalizedImageInfoFromLines(imageId,lines);
        File f = new File(path);
        return f.getName();
    }
    @Override
    public void downloadImages(ProgressPresenter pp, String processName) throws AvatolCVException {
       this.bisqueImages = new BisqueImages(pp, wsClient, chosenDataset, processName);
    }
    @Override
    public String getDatasetTitleText() {
        return "Dataset";
    }
    @Override
    public void setNormalizedImageInfos(NormalizedImageInfos niis) {
        this.niis = niis;
    }
	@Override
	public String getDefaultTrainTestConcern() {
		return null;
	}
    @Override
    public NormalizedValue getValueForKeyAtDatasourceForImage(NormalizedKey normCharKey,
            String imageID, NormalizedKey trainTestConcern,
            NormalizedValue trainTestConcernValue) throws AvatolCVException {
        try {
            List<BisqueAnnotation> annotations = this.wsClient.getAnnotationsForImage(imageID);
            for (BisqueAnnotation annotation : annotations){
                String curKeyName = annotation.getName();
                if (curKeyName.equals(normCharKey.getName())){
                    return new NormalizedValue(annotation.getValue());
                }
            }
            return null;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem finding if key is present on image.", e);
        }
    }
    @Override
    public boolean reviseValueForKey(String provenanceString, String imageID, NormalizedKey key,
            NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException {
        try {
            boolean result = this.wsClient.reviseAnnotation(imageID, key.getName(), value.getName());
            return result;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem revising value for key " + key.getName() + " " + value.getName(), e);
        }
        
    }
    @Override
    public boolean addKeyValue(String provenanceString, String imageID, NormalizedKey key,
            NormalizedValue value, NormalizedKey trainTestConcern, NormalizedValue trainTestConcernValue) throws AvatolCVException {
        try {
            boolean result = this.wsClient.addNewAnnotation(imageID, key.getName(), value.getName());
            return result;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem adding new key/value " + key.getName() + " " + value.getName(), e);
        }
        
    }
    @Override
    public List<String> filterBadSortCandidates(List<String> list) {
        return list;
    }
    @Override
    public void prepForUpload(List<String> charIDs,
            List<String> trainTestConcernValueIDs) throws AvatolCVException {
        // nothing to do
        
    }
    @Override
    public boolean deleteScoreForKey(String imageID, NormalizedKey key,
            NormalizedKey trainTestConcern,
            NormalizedValue trainTestConcernValue) throws AvatolCVException {
        try {
            boolean result = this.wsClient.reviseAnnotation(imageID, key.getName(), "");
            return result;
        }
        catch(BisqueWSException e){
            throw new AvatolCVException("problem deleting value for key " + key.getName(), e);
        }
    }
    @Override
    public boolean groupByTrainTestConcernValueAndVoteForUpload() {
        return false;
    }
	@Override
	public String getDatasetIDforName(String name) throws AvatolCVException {
		if (null == this.allDataSets){
			throw new AvatolCVException("asked to lookup id for datasetname before this.allDataSets has been set");
		}
		for (BisqueDataset bd : allDataSets){
			String curName = bd.getName();
			if (curName.equals(name)){
				return bd.getResourceUniq();
			}
		}
		throw new AvatolCVException("no id found for datasetname " + name);
	}
	@Override
	public String getMandatoryTrainTestConcern() {
		// Bisque does not have one
		return null;
	}
	@Override
	public void forgetMetadata() throws AvatolCVException {
		AvatolCVFileSystem.deleteDirectory(BisqueDataFiles.getAnnotationInfoDir());
		AvatolCVFileSystem.deleteDirectory(AvatolCVFileSystem.getSpecializedImageInfoDir());
	}
	@Override
	public String getRepullPrompt() {
		return "dataset has been updated, pull in changes";
	}
}
