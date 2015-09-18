package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class MBMatrixChoiceStep implements Step {
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private MBMatrix chosenMatrix = null;
    private List<MBMatrix> matrices = null;
    private MBSessionData sessionData = null;
    public MBMatrixChoiceStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }
    public List<String> getAvailableMatrices() throws AvatolCVException {
        try {
            List<String> result = new ArrayList<String>();
            this.matrices = wsClient.getMorphobankMatricesForUser();
            Collections.sort(this.matrices);
            for (MBMatrix mm : this.matrices){
                String name = mm.getName();
                result.add(name);
            }
            Collections.sort(result);
            return result;
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading datasets from Bisque ", e);
        }
    }
    public void setChosenMatrix(String s) throws AvatolCVException {
        this.chosenMatrix = null;
        for (MBMatrix mm : this.matrices){
            String name = mm.getName();
            if (name.equals(s)){
                this.chosenMatrix = mm;
                //this.sessionData.setChosenDataset(ds);
            }
        }
        if (this.chosenMatrix == null){
            throw new AvatolCVException("no MBMatrix match for name " + s);
        }
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        sessionData.setChosenMatrix(this.chosenMatrix);
        // pull in the characters and taxa for the matrix
        try {
            MBMatrix mm = this.sessionData.getChosenMatrix();
            List<MBCharacter> charactersForMatrix = this.wsClient.getCharactersForMatrix(mm.getMatrixID());
            sessionData.setCharactersForCurrentMatrix(charactersForMatrix);
            List<MBTaxon> taxaForMatrix = this.wsClient.getTaxaForMatrix(mm.getMatrixID());
            sessionData.setTaxaForCurrentMatrix(taxaForMatrix);
        }
        catch(MorphobankWSException e){
            throw new AvatolCVException("problem loading data for matrix. " + e.getMessage(), e);
        }
    }
}
