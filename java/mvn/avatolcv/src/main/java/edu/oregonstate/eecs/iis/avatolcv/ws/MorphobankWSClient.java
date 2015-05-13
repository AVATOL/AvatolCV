package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public interface MorphobankWSClient {
    boolean isAuthenticated();
	boolean authenticate(String name, String pw) throws MorphobankWSException;
	public List<MBMatrix> getMorphobankMatricesForUser() throws MorphobankWSException;
	public List<MBCharacter> getCharactersForMatrix(String matrixID)  throws MorphobankWSException ;
	public List<MBTaxon> getTaxaForMatrix(String matrixID)  throws MorphobankWSException ;
	public List<MBCharStateValue> getCharStatesForCell(String matrixID, String charID, String taxonID)  throws MorphobankWSException ;
	public List<MBMediaInfo> getMediaForCell(String matrixID, String charID, String taxonID)  throws MorphobankWSException;
	public List<MBAnnotation> getAnnotationsForCellMedia(String matrixID, String charID, String taxonID, String mediaID)  throws MorphobankWSException ;
	public List<MBView> getViewsForProject(String projectID)  throws MorphobankWSException ;
	public boolean downloadImageForMediaId(String dirToSaveTo, String mediaID, String mediaFileName, String type)  throws MorphobankWSException ;
}
