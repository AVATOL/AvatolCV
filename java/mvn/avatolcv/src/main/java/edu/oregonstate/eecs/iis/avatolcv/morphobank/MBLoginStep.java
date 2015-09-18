package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.obsolete.View;
import edu.oregonstate.eecs.iis.avatolcv.steps.Step;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;

public class MBLoginStep implements Step {
    public String username = null;
    private String password = null;
    private MorphobankWSClient wsClient = null;
    private String view = null;
    public MBLoginStep(String view, MorphobankWSClient wsClient){
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
        catch(MorphobankWSException bwe){
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
