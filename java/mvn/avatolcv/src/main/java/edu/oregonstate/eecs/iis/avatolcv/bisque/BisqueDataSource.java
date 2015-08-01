package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class BisqueDataSource implements DataSource {
    private BisqueWSClient wsClient = null;
    private DatasetInfo chosenDataset = null;
    private List<BisqueImage> bisqueImagesForCurrentDataset = null;
    private Hashtable<String, List<BisqueAnnotation>> annotationsForImageIdHash = null;
    private List<String> scoringConcernAnnotations = null;
    private Hashtable<String, List<String>> valuesForEnumAnnotation = null;
    public BisqueDataSource(){
        wsClient = new BisqueWSClientImpl();
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
                List<BisqueAnnotation> annotations = this.wsClient.getAnnotationsForImage(imageResource_uniq);
                annotationsForImageIdHash.put(imageResource_uniq, annotations);
                pp.updateProgress(processName, 0.1 + (percentProgressPerImage * curCount));
                
            }
            pp.setMessage(processName, "finished.  Click Next to continue.");
        }
        catch(BisqueWSException e){
            e.printStackTrace();
            throw new AvatolCVException("problem loading primary metadata from Bisque: " + e.getMessage(), e);
        }
    }
    @Override
    public void setChosenDataset(DatasetInfo di) {
        this.chosenDataset = di;
    }
    @Override
    public List<ChoiceItem> getScoringConcernItems(ScoringAlgorithms.ScoringScope scoringScope, ScoringAlgorithms.ScoringSessionFocus scoringFocus)
            throws AvatolCVException {
        List<ChoiceItem> items = new ArrayList<ChoiceItem>();
        List<String> annotationNames = new ArrayList<String>();
        for (BisqueImage bi : this.bisqueImagesForCurrentDataset){
            String imageID = bi.getResourceUniq();
            List<BisqueAnnotation> annotations = annotationsForImageIdHash.get(imageID);
            for (BisqueAnnotation a : annotations){
                String annotationName = a.getName();
                if (!annotationNames.contains(annotationName)){
                    annotationNames.add(annotationName);
                }
            }
        }
        for (String annotationName : annotationNames){
            ChoiceItem ci = new ChoiceItem(annotationName, false, annotationName);
            items.add(ci);
        }
        return items;
    }
    @Override
    public String getInstructionsForScoringConcernScreen(ScoringAlgorithms.ScoringScope scoringScope, ScoringAlgorithms.ScoringSessionFocus scoringFocus) {
        if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
                return "Place a check mark next to annotations that refer to presence/absence of a part." +
                        "(AvatolCV has tried to deduce this from metadata.)";
            }
            else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                    scoringScope == ScoringAlgorithms.ScoringScope.SINGLE_ITEM){
                return "Select the desired presence/absence part.";
            }
            else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                    scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
                return "Place a check mark next to annotations that refer to shape aspect of a specimen.";
            }

            else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                    scoringScope == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
                return "Select the desired shape aspect of the specimen.";
            }

            else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                    scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
                return "Place a check mark next to annotations that refer to texture aspects of a specimen.";
            }

            else {// (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                  //  sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
                return "Select the desired texture aspect of the specimen.";
            }
    }
    @Override
    public void setChosenScoringConcerns(List<ChoiceItem> items) {
        this.scoringConcernAnnotations = new ArrayList<String>();
        for (ChoiceItem item : items){
            this.scoringConcernAnnotations.add(item.getName());
        }
    }
    @Override
    public void setChosenScoringConcern(ChoiceItem item) {
        this.scoringConcernAnnotations = new ArrayList<String>();
        this.scoringConcernAnnotations.add(item.getName());
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
                for (BisqueAnnotation annotation : annotations){
                    totalCount++;
                }
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
                            List<String> values = this.wsClient.getAnnotationValueOptions(annotationTypeValue);
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
        sb.append(" add some more text" + NL);
        
        sb.append(NL);
        return "" + sb;
    }
}
