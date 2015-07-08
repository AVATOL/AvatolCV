package edu.oregonstate.eecs.iis.avatolcv.ui.javafx;

import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.core.StepController;
import edu.oregonstate.eecs.iis.avatolcv.steps.LoginStep;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginStepController implements StepController {
	public TextField usernameTextField;
	public TextField passwordTextField;
	public Label badCredentialLabel;
	private LoginStep loginStep;
	private String fxmlDocName;
	private SessionInfo sessionInfo;
	public LoginStepController(LoginStep loginStep, SessionInfo sessionInfo, String fxmlDocName){
		this.loginStep = loginStep;
		this.fxmlDocName = fxmlDocName;
		this.sessionInfo = sessionInfo;
	}
	public void setDefaultCredentials(){
	    usernameTextField.setText(sessionInfo.getDataSource().getDefaultUsername());
        passwordTextField.setText(sessionInfo.getDataSource().getDefaultPassword());
	}
	public void tryCredentials(){
		this.loginStep.setUsername(usernameTextField.getText());
		this.loginStep.setPassword(passwordTextField.getText());
		try {
			this.loginStep.consumeProvidedData();
		}
		catch(AvatolCVException ace){
		    Platform.runLater(new BadCredentialMessageDisplayer());
		}
	}

	public void showBadCredentialMessage(){
	    badCredentialLabel.setText("username password combination not valid");
        clearUIFields();
	}
	public void clearBadCredentialWarning(){
		badCredentialLabel.setText("");
	}
	@Override
	public void clearUIFields(){
		usernameTextField.setText("");
		passwordTextField.setText("");
	}
	@Override
	public boolean consumeUIData() {
		tryCredentials();
		if (this.loginStep.isAuthenticated()){
			return true;
		}
		return false;
	}

	@Override
	public Node getContentNode() throws AvatolCVException {
        try {
        	System.out.println("trying to load" +  this.fxmlDocName);
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(this.fxmlDocName));
            loader.setController(this);
            Node content = loader.load();
            setDefaultCredentials();
            return content;
        }
        catch(IOException ioe){
            throw new AvatolCVException("problem loading ui " + fxmlDocName + " for controller " + this.getClass().getName());
        }
	}

	@Override
	public boolean delayEnableNavButtons() {
		return false;
	}

	public class BadCredentialMessageDisplayer implements Runnable {
	    public void run() {
	        showBadCredentialMessage();
	    }
	}

    @Override
    public void executeDataLoadPhase() throws AvatolCVException {
     // nothing to be done
    }
    @Override
    public void configureUIForDataLoadPhase() {
     // nothing to be done
    }
    @Override
    public boolean isDataLoadPhaseComplete() {
        // not relevant
        return true;
    }
}
