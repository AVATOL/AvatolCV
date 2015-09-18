package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public interface SessionDataInterface {
    String getCharQuestionsSourcePath() throws AvatolCVException ;
    String getCharQuestionsAnsweredQuestionsPath() throws AvatolCVException ;
    void setChosenAlgorithm(String s);
    String getTrainingTestingDescriminatorName();
    List<MBTaxon> getTaxa();
    MBMatrix getChosenMatrix();
    List<MBCharacter> getChosenCharacters();
    void setImagesForCell(String matrixID, String charID, String taxonID, List<MBMediaInfo> mediaInfos);
    List<MBMediaInfo> getImagesForCell(String matrixID, String charID, String taxonID);
    ScoringAlgorithms getScoringAlgorithms();
    void registerAnnotationsForCell(List<MBAnnotation> annotationsForCell, String charID, String taxonID, String mediaID) throws AvatolCVException ;
    void registerStatesForCell(List<MBCharStateValue> statesForCell, String charID, String taxonID) throws AvatolCVException;
    List<MBTaxon> getTrueTaxaForCurrentMatrix();
    TrainTestInfo getTrainTestInfo(String taxonID, String charID);
}
