package edu.oregonstate.eecs.iis.obsolete.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.obsolete.avatolcv.View;

public class BisqueLoginStep implements Step {
	private String username = null;
	private String password = null;
	private BisqueWSClient wsClient = null;
	private String view = null;
	public BisqueLoginStep(String view, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.view = view;
	}
	@Override
    public void init() {
        // nothing to do
    }
	public boolean isAuthenticated(){
		if (wsClient.isAuthenticated()){
			return true;
		}
		return false;
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		// TODO Auto-generated method stub
		if (null == username || "".equals(username)){
			throw new AvatolCVException("username must be specified");
		}
		if (null == password || "".equals(password)){
			throw new AvatolCVException("password must be specified");
		}
		try {
			boolean authenticated = wsClient.authenticate(this.username, this.password);
			if (!authenticated){
				throw new AvatolCVException("username " + this.username + " and password " + this.password + " not valid combination.");
			}
		}
		catch(BisqueWSException bwe){
			throw new AvatolCVException("problem authenticating with username " + this.username + " and password " + password + ".\n\n" + bwe.getMessage());
		}
	}
	public void setUsername(String s){
		this.username = s;
	}
	public void setPassword(String s){
		this.password = s;
	}

}
