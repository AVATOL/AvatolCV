package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVDataFiles;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ChoiceItem;
import edu.oregonstate.eecs.iis.avatolcv.core.DataSource;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.core.ScoringAlgorithms;
import edu.oregonstate.eecs.iis.avatolcv.generic.DatasetInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MorphobankDataSource implements DataSource {
    private MorphobankWSClient wsClient = null;
    private List<MBCharacter> charactersForMatrix = null;
    private List<MBCharacter> chosenCharacters = null;
    private List<MBTaxon> taxaForMatrix = null;
    private DatasetInfo chosenDataset = null;
    private List<MBView> viewsForProject = null;
    private Hashtable<String,List<MBCharStateValue>> charStateValuesForCellHash = new Hashtable<String, List<MBCharStateValue>>();
    private Hashtable<String,List<MBMediaInfo>> mediaInfoForCellHash = new Hashtable<String, List<MBMediaInfo>>();
    private MorphobankDataFiles mbDataFiles = null;
    
    public MorphobankDataSource(String sessionDataRoot){
        wsClient = new MorphobankWSClientImpl();
        mbDataFiles = new MorphobankDataFiles();
        mbDataFiles.setSessionDataRoot(sessionDataRoot);
    }
    @Override
    public boolean authenticate(String username, String password) throws AvatolCVException {
        boolean result = false;
        try {
            result = this.wsClient.authenticate(username, password);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException(e.getMessage(),e);
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
        List<MBMatrix> matrices = null;
        try {
            matrices = wsClient.getMorphobankMatricesForUser();
            for (MBMatrix mm : matrices){
                DatasetInfo di = new DatasetInfo();
                di.setName(mm.getName());
                di.setID(mm.getMatrixID());
                di.setProjectID(mm.getProjectID());
                datasets.add(di);
            }
            Collections.sort(datasets);
            return datasets;
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading matrices from Morphobank ", e);
        }
    }
    
    @Override
    public String getDefaultUsername() {
        return "irvine@eecs.oregonstate.edu";
        //return "jedirv@gmail.com";
    }
    @Override
    public String getDefaultPassword() {
        return "squonkmb";
    }
    @Override
    public void loadPrimaryMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        try {
            String matrixID = this.chosenDataset.getID();
            String projectID = this.chosenDataset.getProjectID();
            pp.setMessage(processName, "loading info on characters...");
            pp.updateProgress(processName, 0.0);
            
            this.charactersForMatrix = this.wsClient.getCharactersForMatrix(matrixID);
            pp.setMessage(processName, "loading info on taxa...");
            pp.updateProgress(processName, 0.4);
            this.taxaForMatrix = this.wsClient.getTaxaForMatrix(matrixID);
            
            pp.setMessage(processName, "loading info on views...");
            pp.updateProgress(processName, 0.8);
            this.viewsForProject = this.wsClient.getViewsForProject(projectID);
            
            pp.setMessage(processName, "finished.  Click Next to continue.");
            pp.updateProgress(processName, 1.0);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading data for matrix. " + e.getMessage(), e);
        }
    }
    @Override
    public void setChosenDataset(DatasetInfo di) {
        this.chosenDataset = di;
        this.mbDataFiles.setDatasetDirname(di.getName());
    }
    public List<ChoiceItem> getScoringConcernItemsNoneSelected(){
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
            ChoiceItem ci = new ChoiceItem(character.getCharName(), false, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
    public List<ChoiceItem> getPresenceAbsenceScoringConcernItems(){
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
            boolean isPresenceAbsence = character.isPresenceAbsence();
            ChoiceItem ci = new ChoiceItem(character.getCharName(), isPresenceAbsence, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
    public List<ChoiceItem> getShapeScoringConcernItems(){
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
            boolean isShapeAspect = character.isShapeAspect();
            ChoiceItem ci = new ChoiceItem(character.getCharName(), isShapeAspect, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
    public List<ChoiceItem> getTextureScoringConcernItems(){
        List<ChoiceItem> result = new ArrayList<ChoiceItem>();
        for (MBCharacter character : this.charactersForMatrix){
            boolean isTextureAspect = character.isTextureAspect();
            ChoiceItem ci = new ChoiceItem(character.getCharName(), isTextureAspect, character);
            result.add(ci);
        }
        Collections.sort(result);
        return result;
    }
    @Override
    public List<ChoiceItem> getScoringConcernItems(ScoringAlgorithms.ScoringScope scoringScope, ScoringAlgorithms.ScoringSessionFocus scoringFocus) throws AvatolCVException{
        if (scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            // select all appropriate for case
            if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE){
                return getPresenceAbsenceScoringConcernItems();
            }
            else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT){
                return getShapeScoringConcernItems();
            }
            else {
                // must be ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT
                return getTextureScoringConcernItems();
            }
        }
        else {
            // must be single item choice screen - don't select any - first in list will appear as default selection
            return getScoringConcernItemsNoneSelected();
            
        }
    }
    @Override
    public String getInstructionsForScoringConcernScreen(ScoringAlgorithms.ScoringScope scoringScope, ScoringAlgorithms.ScoringSessionFocus scoringFocus) {
        if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
            scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            return "Place a check mark next to characters that refer to presence/absence of a part." +
                    "(AvatolCV has tried to deduce this from metadata.)";
        }
        else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                scoringScope == ScoringAlgorithms.ScoringScope.SINGLE_ITEM){
            return "Select the desired presence/absence character.";
        }
        else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
            return "Place a check mark next to characters that refer to shape aspect of a specimen.";
        }

        else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                scoringScope == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
            return "Select the desired shape aspect character.";
        }

        else if (scoringFocus == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                scoringScope == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
            return "Place a check mark next to characters that refer to texture aspects of a specimen.";
        }

        else {// (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
              //  sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
            return "Select the desired texture aspect character.";
        }
    }
    @Override
    public void setChosenScoringConcerns(List<ChoiceItem> items) {
        this.chosenCharacters = new ArrayList<MBCharacter>();
        for (ChoiceItem ci : items){
            this.chosenCharacters.add((MBCharacter)ci.getBackingObject());
        }
    }
    @Override
    public void setChosenScoringConcern(ChoiceItem item) {
        this.chosenCharacters = new ArrayList<MBCharacter>();
        this.chosenCharacters.add((MBCharacter)item.getBackingObject());
    }
    @Override
    public void loadRemainingMetadataForChosenDataset(ProgressPresenter pp,
            String processName) throws AvatolCVException {
        try {
            int rowCount = this.taxaForMatrix.size();
            int colCount = this.charactersForMatrix.size();
            String matrixID = this.chosenDataset.getID();
            int totalItemCount = colCount * rowCount;
            double increment = 1.0 / totalItemCount;
            int curCount = 0;
            for (MBCharacter character : this.charactersForMatrix){
                for (MBTaxon taxon : this.taxaForMatrix){
                    String charID = character.getCharID();
                    String taxonID = taxon.getTaxonID();
                    pp.setMessage(processName, "loading info for cell: character " + character.getCharName() + " taxon " + taxon.getTaxonName());
                    String key = getKeyForCell( charID,taxonID);
                    List<MBCharStateValue> charStatesForCell = this.mbDataFiles.loadMBCharStatesFromDisk(charID, taxonID);
                    if (charStatesForCell.isEmpty()){
                        charStatesForCell = this.wsClient.getCharStatesForCell(matrixID, charID, taxonID);
                        this.mbDataFiles.persistMBCharStatesForCell(charStatesForCell, charID, taxonID);
                    }
                    charStateValuesForCellHash.put(key, charStatesForCell);
                    
                    List<MBMediaInfo> mediaInfosForCell = this.mbDataFiles.loadMBMediaInfosForCell(charID, taxonID);
                    if (mediaInfosForCell.isEmpty()){
                        mediaInfosForCell = this.wsClient.getMediaForCell(matrixID, charID, taxonID);
                        this.mbDataFiles.persistMBMediaInfosForCell(mediaInfosForCell, charID, taxonID);
                    }
                    mediaInfoForCellHash.put(key, mediaInfosForCell);
                    curCount++;
                    pp.updateProgress(processName, curCount * increment);
                }
            }
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading data for matrix. " + e.getMessage(), e);
        }
        
    }
    
    public static String getKeyForCell(String charID, String taxonID){
        return "c" + charID + "_t" + taxonID;
    }
    private static final String NL = System.getProperty("line.separator");
    @Override
    public String getDatasetSummaryText() {        
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix: " + this.chosenDataset.getName() + NL);
        sb.append(NL);
        sb.append("Taxa:" + NL);
        for (MBTaxon taxon : taxaForMatrix){
            sb.append("    " + taxon.getTaxonName() + NL);
        }
        sb.append(NL);
        sb.append("Selected Characters: " + NL);
        for (MBCharacter character : chosenCharacters){
            sb.append("    " + character.getCharName() + NL);
        }
        sb.append("Views in project: " + NL);
        for (MBView view : this.viewsForProject){
            sb.append("    " + view.getName() + NL);
        }
        sb.append(NL);
        return "" + sb;
    }
    @Override
    public AvatolCVDataFiles getAvatolCVDataFiles() {
        return this.mbDataFiles;
    }
}
