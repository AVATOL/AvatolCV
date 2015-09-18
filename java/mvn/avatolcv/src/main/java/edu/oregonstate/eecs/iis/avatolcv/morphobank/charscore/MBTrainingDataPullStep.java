package edu.oregonstate.eecs.iis.avatolcv.morphobank.charscore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.ImageInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.ProgressPresenter;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBSessionData;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MBTrainingDataPullStep implements Step {
	private static final String NL = System.getProperty("line.separator");
    private MorphobankWSClient wsClient = null;
    private String view = null;
    private MBSessionData sessionData = null;
    public MBTrainingDataPullStep(String view, MorphobankWSClient wsClient, MBSessionData sessionData){
        this.wsClient = wsClient;
        this.view = view;
        this.sessionData = sessionData;
        
    }
    public List<MBTaxon> getTaxa() {
        return sessionData.getTaxa();
    }
   
    public List<MBCharStateValue> robustTrainingDataDownload(ProgressPresenter pp, String matrixID, String charID, String taxonID , String processName) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean dataNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && dataNotYetDownloaded){
            try {
                tries++;
                List<MBCharStateValue> statesForCell = this.wsClient.getCharStatesForCell(matrixID, charID, taxonID);
                dataNotYetDownloaded = false;
                return statesForCell;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage(processName, "download timed out - retrying trainingData : charID " + charID + " taxonID " + taxonID + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (dataNotYetDownloaded){
            throw new AvatolCVException("problem downloading data: " + mostRecentException);
        }
        return null;
    }
   
    public void downloadTrainingData(ProgressPresenter pp, String processName) throws AvatolCVException {
    	String matrixID = sessionData.getChosenMatrix().getMatrixID();
        List<MBCharacter> characters = sessionData.getChosenCharacters();
        double cellCountTotal = (double)getTaxa().size() * characters.size();
        double cellCountCurrent = 0.0;
        String path = this.sessionData.getSessionLogPath("trainingDataLoad");
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(path));

            for (MBCharacter character : characters){
            	String charID = character.getCharID();
            	List<MBTaxon> taxa = getTaxa();
                for (MBTaxon taxon : taxa){
                    String taxonID = taxon.getTaxonID();
                    cellCountCurrent += 1;
                    if (!this.sessionData.isStatesForCellOnDisk(charID, taxonID)){
                    	writer.write("loading from SITE : char " + charID + " taxon " + taxonID + NL);
                		List<MBCharStateValue> statesForCell = robustTrainingDataDownload(pp, matrixID, charID, taxonID, processName);
                    	this.sessionData.registerStatesForCell(statesForCell, charID, taxonID);
                    }
                    else {
                    	writer.write("loading from file : char " + charID + " taxon " + taxon.getTaxonID() + NL);
                    }
                   
                    double percentDone = cellCountCurrent / cellCountTotal;
                    pp.updateProgress(processName, percentDone);
                    if (percentDone == 1.0){
                        pp.setMessage(processName, "Training data download complete for " + (int)cellCountCurrent + " cells.");
                    }
                    else {
                        pp.setMessage(processName, "cell " + (int)cellCountCurrent + " of " + (int)cellCountTotal + " character " + character.getCharName() + " (taxon " + taxon.getTaxonName() + ")");
                    }
                }
            }
            writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem logging training data load " + ioe.getMessage());
        }
    }
    
    public List<MBAnnotation> robustAnnotationDataDownload(ProgressPresenter pp, String matrixID, String charID, String taxonID ,String mediaID, String processName) throws AvatolCVException {
        int maxRetries = 4;
        int tries = 0;
        boolean dataNotYetDownloaded = true;
        Exception mostRecentException = null;
        while (maxRetries > tries && dataNotYetDownloaded){
            try {
                tries++;
        		List<MBAnnotation> annotationsForCell = this.wsClient.getAnnotationsForCellMedia(matrixID, charID, taxonID, mediaID);

                dataNotYetDownloaded = false;
                return annotationsForCell;
            }
            catch(MorphobankWSException e){
                if (e.getMessage().equals("timeout")){
                    pp.setMessage(processName, "download timed out - retrying trainingData : charID " + charID + " taxonID " + taxonID + " - attempt " + (tries+1));
                }
                mostRecentException = e;
            }
        }
        if (dataNotYetDownloaded){
            throw new AvatolCVException("problem downloading data: " + mostRecentException);
        }
        return null;
    }
    public void downloadAnnotationData(ProgressPresenter pp, String processName) throws AvatolCVException {
    	String matrixID = sessionData.getChosenMatrix().getMatrixID();
        List<MBCharacter> characters = sessionData.getChosenCharacters();
        double cellCountTotal = (double)getTaxa().size() * characters.size();
        double cellCountCurrent = 0.0;
        String path = this.sessionData.getSessionLogPath("annotationsDataLoad");
        try {
        	BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        	for (MBCharacter character : characters){
            	String charID = character.getCharID();
            	List<MBTaxon> taxa = getTaxa();
                for (MBTaxon taxon : taxa){
                    String taxonID = taxon.getTaxonID();
                    cellCountCurrent += 1;

                    List<MBMediaInfo> mediaForCell = sessionData.getImagesForCell(matrixID, charID, taxonID);
                    for (MBMediaInfo mi : mediaForCell){
                        String mediaID = mi.getMediaID();
                    	if (!this.sessionData.isAnnotationOnDisk(charID, taxonID, mediaID)){
                    		writer.write("loading from SITE : char " + charID + " taxon " + taxonID + " media " + mediaID + NL);
                    		List<MBAnnotation> annotationsForCell = robustAnnotationDataDownload(pp, matrixID, charID, taxonID , mediaID, processName);
                    		this.sessionData.registerAnnotationsForCell(annotationsForCell, charID, taxonID, mediaID);
                    	}
                    	else {
                        	writer.write("loading from file : char " + charID + " taxon " + taxonID + " media " + mediaID + NL);
                    	}
                    	double percentDone = cellCountCurrent / cellCountTotal;
                        pp.updateProgress(processName, percentDone);
                        if (percentDone == 1.0){
                            pp.setMessage(processName, "Annotation data download complete for " + (int)cellCountCurrent + " cells.");
                        }
                        else {
                            pp.setMessage(processName, "cell " + (int)cellCountCurrent + " of " + (int)cellCountTotal + " character " + character.getCharName() + " (taxon " + taxon.getTaxonName() + ")");
                        }
                       
                    }
                }
            }
        	writer.close();
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem logging annotation data load " + ioe.getMessage());
        }
        
    }
    @Override
    public void init() throws AvatolCVException {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeProvidedData() throws AvatolCVException {
        // nothing to do - images already download

    }

}
