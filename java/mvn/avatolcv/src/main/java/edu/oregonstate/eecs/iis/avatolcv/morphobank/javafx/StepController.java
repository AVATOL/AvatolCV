package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import javafx.scene.Node;

public interface StepController {
    boolean consumeUIData();
    void clearUIFields();
    Node getContentNode() throws AvatolCVException ;
    boolean hasActionToAutoStart();
    void startAction() throws AvatolCVException ;
}
