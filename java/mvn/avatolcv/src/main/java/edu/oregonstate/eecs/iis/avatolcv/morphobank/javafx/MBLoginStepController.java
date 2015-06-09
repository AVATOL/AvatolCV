package edu.oregonstate.eecs.iis.avatolcv.morphobank.javafx;

import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBLoginStep;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MorphobankSessionJavaFX;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

public class MBLoginStepController implements StepController {
	public TextField usernameTextField;
	public TextField passwordTextField;
	public Label badCredentialLabel;
	private MBLoginStep loginStep;
	private String fxmlDocName;
	public MBLoginStepController(MBLoginStep loginStep, String fxmlDocName){
		this.loginStep = loginStep;
		this.fxmlDocName = fxmlDocName;
	}
	
	public void tryCredentials(){
		this.loginStep.setUsername(usernameTextField.getText());
		this.loginStep.setPassword(passwordTextField.getText());
		try {
			this.loginStep.consumeProvidedData();
		}
		catch(AvatolCVException ace){
			badCredentialLabel.setText("username password combination not valid");
			clearUIFields();
		}
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

}
