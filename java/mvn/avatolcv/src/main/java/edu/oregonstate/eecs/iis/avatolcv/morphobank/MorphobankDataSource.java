package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public MorphobankDataSource(){
        wsClient = new MorphobankWSClientImpl();
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
            this.wsClient.getViewsForProject(projectID);
            
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
    public List<ChoiceItem> getScoringConcernItems(ScoringAlgorithms sa) throws AvatolCVException{
        if (sa.getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            // select all appropriate for case
            if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE){
                return getPresenceAbsenceScoringConcernItems();
            }
            else if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT){
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
    public String getInstructionsForScoringConcernScreen(ScoringAlgorithms sa) {
        if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
            sa.getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM){
            return "Place a check mark next to characters that refer to presence/absence of a part." +
                    "(AvatolCV has tried to deduce this from metadata.)";
        }
        else if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_PART_PRESENCE_ABSENCE &&
                sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM){
            return "Select the desired presence/absence part.";
        }
        else if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                sa.getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
            return "Place a check mark next to characters that refer to shape aspect of a specimen.";
        }

        else if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_SHAPE_ASPECT &&
                sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
            return "Select the desired shape aspect of the specimen.";
        }

        else if (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
                sa.getScoringScope() == ScoringAlgorithms.ScoringScope.MULTIPLE_ITEM) {
            return "Place a check mark next to characters that refer to texture aspects of a specimen.";
        }

        else {// (sa.getSessionScoringFocus() == ScoringAlgorithms.ScoringSessionFocus.SPECIMEN_TEXTURE_ASPECT &&
              //  sa.getScoringScope() == ScoringAlgorithms.ScoringScope.SINGLE_ITEM) {
            return "Select the desired texture aspect of the specimen.";
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
}
