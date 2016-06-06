package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.session.DataIssue;
import edu.oregonstate.eecs.iis.avatolcv.session.SessionInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;

public class LoginStep  extends Answerable implements Step {
    public String username = null;
    private String password = null;
    private SessionInfo sessionInfo = null;
    private static final Logger logger = LogManager.getLogger(LoginStep.class);

    public LoginStep(SessionInfo sessionInfo){
        this.sessionInfo = sessionInfo;
    }
    @Override
    public void init() {
        // nothing to do
    }
    public boolean isAuthenticated(){
        if (this.sessionInfo.getDataSource().isAuthenticated()){
            return true;
        }
        return false;
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        if (null == username || "".equals(username)){
            throw new AvatolCVException("username must be specified");
        }
        if (null == password || "".equals(password)){
            throw new AvatolCVException("password must be specified");
        }
        boolean authenticated = this.sessionInfo.getDataSource().authenticate(this.username, this.password);
        if (!authenticated){
            throw new AvatolCVException("username " + this.username + " and password " + this.password + " not valid combination.");
        }
        logger.info("user is authenticated into data source");
    }
    public void setUsername(String s){
        this.username = s;
    }
    public void setPassword(String s){
        this.password = s;
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isEnabledByPriorAnswers() {
    	if (this.sessionInfo.getDataSource().isAuthenticated()){
    		return false;
    	}
        return true;
    }
    @Override
	public boolean shouldRenderIfBackingIntoIt() {
    	if (!this.sessionInfo.getDataSource().isAuthenticated()){
    		return true;
    	}
		return false;
	}
	@Override
	public List<DataIssue> getDataIssues() throws AvatolCVException{
		return null;
	}
	@Override
	public SessionInfo getSessionInfo() {
		return this.getSessionInfo();
	}
}
