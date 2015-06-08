package edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class MBTrainingExampleCheckStep implements Step {
    private String view = null;
    private SessionData sessionData = null;
    MorphobankWSClient wsClient = null;
    public MBTrainingExampleCheckStep(String view, SessionData sessionData, MorphobankWSClient wsClient){
        this.view = view;
        this.sessionData = sessionData;
        this.wsClient = wsClient;
    }
    public String getTrainingTestingDescriminatorName(){
        return sessionData.getTrainingTestingDescriminatorName();
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getView() {
        // TODO Auto-generated method stub
        return null;
    }
    public List<MBTaxon> getTaxa() {
        return sessionData.getTaxa();
    }
    public void downloadTrainingInfo() throws AvatolCVException {
        String matrixID = sessionData.getChosenMatrix().getMatrixID();
        String charID = sessionData.getChosenCharacters().getCharID();
        List<MBTaxon> taxa = getTaxa();
        for (MBTaxon taxon : taxa){
            String taxonID = taxon.getTaxonID();
            List<MBMediaInfo> mediaForCell = sessionData.getImagesForCell(matrixID, charID, taxonID);
            for (MBMediaInfo mi : mediaForCell){
                String mediaID = mi.getMediaID();
                try {
                    List<MBAnnotation> annotationsForCell = this.wsClient.getAnnotationsForCellMedia(matrixID, charID, taxonID, mediaID);
                    List<MBCharStateValue> statesForCell = this.wsClient.getCharStatesForCell(matrixID, charID, taxonID);
                    
                }
                catch(MorphobankWSException e){
                    throw new AvatolCVException("problem downloading annotation info for cell media " + matrixID + "_" + charID + "_" + taxonID + "_" + mediaID + e.getMessage());
                }
            }
        }
        
       
        
    }
    
}
