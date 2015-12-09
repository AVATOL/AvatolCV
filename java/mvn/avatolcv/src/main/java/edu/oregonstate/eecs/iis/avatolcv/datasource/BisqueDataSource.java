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
import edu.oregonstate.eecs.iis.avatolcv.core.DataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedImageInfosToReview;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.core.NormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoreIndex;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringConcernDetails;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionImages;
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
    public BisqueDataSource(String sessionsRoot, SessionImages sessionImages){
        this.sessionImages = sessionImages;
        wsClient = new BisqueWSClientImpl();
        bisqueDataFiles = new BisqueDataFiles();
        //bisqueDataFiles.setSessionsRoot(sessionsRoot);
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
            List<BisqueDataset> bds = wsClient.getDatasets();
            
            for (BisqueDataset ds : bds){
                DatasetInfo di = new DatasetInfo();
                di.setName(ds.getName());
                di.setID(ds.getResourceUniq());
                di.setProjectID(DatasetInfo.NO_CONTAINING_PROJECT_ID);
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
        return "jedirv";
    }
    @Override
    public String getDefaultPassword() {
        return "Neton3plants**";
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
            List<BisqueAnnotation> annotations = annotationsForImageIdHash.get(imageID);
            for (BisqueAnnotation a : annotations){
                String annotationName = a.getName();
                String annotationId = a.getAnnotationID();
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
        if (scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM){
                return "Place a check mark next to annotations that refer to presence/absence of a part." +
                        "(AvatolCV has tried to deduce this from metadata.)";
            }
            else if (scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                    scoringScope == ScoringAlgorithm.ScoringScope.SINGLE_ITEM){
                return "Select the desired presence/absence part.";
            }
            else if (scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                    scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM) {
                return "Place a check mark next to annotations that refer to shape aspect of a specimen.";
            }

            else if (scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                    scoringScope == ScoringAlgorithm.ScoringScope.SINGLE_ITEM) {
                return "Select the desired shape aspect of the specimen.";
            }

            else if (scoringFocus == ScoringAlgorithm.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                    scoringScope == ScoringAlgorithm.ScoringScope.MULTIPLE_ITEM) {
                return "Place a check mark next to annotations that refer to texture aspects of a specimen.";
            }

            else {// (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                  //  sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
                return "Select the desired texture aspect of the specimen.";
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
    public DataFilter getDataFilter(String specificSessionDir)
            throws AvatolCVException {
        this.dataFilter = new DataFilter(AvatolCVFileSystem.getSessionDir());
        //this.dataFilter.
        return this.dataFilter;
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
            lines.add(keyString+"="+value);
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
}
