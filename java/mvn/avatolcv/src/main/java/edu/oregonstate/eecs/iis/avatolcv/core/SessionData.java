package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public interface SessionData {
    String getCharQuestionsSourcePath() throws AvatolCVException ;
    String getCharQuestionsAnsweredQuestionsPath() throws AvatolCVException ;
    void setChosenAlgorithm(String s);
    String getTrainingTestingDescriminatorName();
    List<MBTaxon> getTaxa();
    MBMatrix getChosenMatrix();
    MBCharacter getChosenCharacter();
    void setImagesForCell(String matrixID, String charID, String taxonID, List<MBMediaInfo> mediaInfos);
    List<MBMediaInfo> getImagesForCell(String matrixID, String charID, String taxonID);
    ScoringAlgorithms getScoringAlgorithms();
}
