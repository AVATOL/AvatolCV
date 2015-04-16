package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;

public class BisqueLoginStep implements Step {
	private String username = null;
	private String password = null;
	private BisqueWSClient wsClient = null;
	private View view = null;
	public BisqueLoginStep(View view, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.view = view;
	}
	@Override
	public View getView(){
		return this.view;
	}
	public boolean isAuthenticated(){
		if (wsClient.isAuthenticated()){
			return true;
		}
		return false;
	}
	@Override
	public boolean needsAnswering() {
		return !isAuthenticated();
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		// TODO Auto-generated method stub
		if (null == username || "".equals(username)){
			throw new BisqueSessionException("username must be specified");
		}
		if (null == password || "".equals(password)){
			throw new BisqueSessionException("password must be specified");
		}
		try {
			boolean authenticated = wsClient.authenticate(this.username, this.password);
			if (!authenticated){
				throw new BisqueSessionException("username " + this.username + " and password " + this.password + " not valid combination.");
			}
		}
		catch(BisqueWSException bwe){
			throw new BisqueSessionException("problem authenticating with username " + this.username + " and password " + password + ".\n\n" + bwe.getMessage());
		}
	}
	public void setUsername(String s){
		this.username = s;
	}
	public void setPassword(String s){
		this.password = s;
	}

}
