package edu.oregonstate.eecs.iis.obsolete.avatolcv.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.SessionDataInterface;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.TrainTestInfo;

public class MBTrainingExampleCheckStep implements Step {
    private String view = null;
    private SessionDataInterface sessionData = null;
    MorphobankWSClient wsClient = null;
    public MBTrainingExampleCheckStep(String view, SessionDataInterface sessionData, MorphobankWSClient wsClient){
        this.view = view;
        this.sessionData = sessionData;
        this.wsClient = wsClient;
    }
    public String getTrainingTestingDescriminatorName(){
        return sessionData.getTrainingTestingDescriminatorName();
    }
    public List<MBTaxon> getTaxa(){
    	return sessionData.getTaxa();
    }
    public List<MBTaxon> getTrueTaxaForCurrentMatrix(){
    	return sessionData.getTrueTaxaForCurrentMatrix();
    }
    public List<MBCharacter> getCharacters(){
    	return sessionData.getChosenCharacters();
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

	public TrainTestInfo getTrainTestInfo(String taxonID, String charID) {
		return this.sessionData.getTrainTestInfo(taxonID, charID);
	}
   
    
}
