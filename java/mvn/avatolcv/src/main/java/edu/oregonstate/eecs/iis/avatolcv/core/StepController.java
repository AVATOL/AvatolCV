package edu.oregonstate.eecs.iis.avatolcv.core;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import javafx.scene.Node;

public interface StepController {
    boolean consumeUIData();
    void clearUIFields();
    Node getContentNode() throws AvatolCVException ;
    boolean delayEnableNavButtons();
    void executeFollowUpDataLoadPhase() throws AvatolCVException;
    void configureUIForFollowUpDataLoadPhase();
    boolean isFollowUpDataLoadPhaseComplete();
}
