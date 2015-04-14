package edu.oregonstate.eecs.iis.avatolcv.bisque;

import edu.oregonstate.eecs.iis.avatolcv.core.SessionSequence;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;

public class BisqueLoginStep implements Step {
	private View view = null;
	private String username = null;
	private String password = null;
	private BisqueWSClient wsClient = null;
	private SessionSequence session = null;
	public BisqueLoginStep(SessionSequence session, BisqueWSClient wsClient){
		this.wsClient = wsClient;
		this.session = session;
	}
    public void setView(View view){
    	this.view = view;
    }
	public void activate() {
		// TODO Auto-generated method stub
		view.express();
	}
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
			this.session.next();
		}
		catch(BisqueWSException bwe){
			throw new BisqueSessionException("problem authenticating with username " + this.username + " and password " + password + ".\n\n" + bwe.getMessage());
		}
	}

}
