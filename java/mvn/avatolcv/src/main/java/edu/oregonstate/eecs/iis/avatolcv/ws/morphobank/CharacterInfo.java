package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.scoring.ScoringConcernDetails;

public class CharacterInfo {
//{"ok":true,"characters":[{"charID":"383114","charName":"Tube material!!!","charStates":[{"charStateID":"821248","charStateName":"mucus???","charStateNum":"0"},{"charStateID":"821249","charStateName":"chitinous","charStateNum":"1"},{"charStateID":"821250","charStateName":"calcareous","charStateNum":"2"}]},{"charID":"555957","charName":"meow","charStates":[{"charStateID":"1245629","charStateName":"New state","charStateNum":"0"},{"charStateID":"1245630","charStateName":"New state","charStateNum":"1"},{"charStateID":"1245631","charStateName":"New state","charStateNum":"2"}]},{"charID":"519541","charName":"test task.","charStates":[{"charStateID":"1157844","charStateName":"state 1","charStateNum":"0"},{"charStateID":"1157845","charStateName":"state 2","charStateNum":"1"}]}]}
    private String ok;
    private List<MBCharacter> characters;
    
    public void setOk(String s){
		this.ok = s;
	}
	public String getOk(){
		return this.ok;
	}

	public void setCharacters(List<MBCharacter> s){
		this.characters = s;
	}
	public List<MBCharacter> getCharacters(){
		return this.characters;
	}
	
    public static class MBCharacter implements ScoringConcernDetails {
    	//{"charID":"383114","charName":"Tube material!!!","charStates":[{"charStateID":"821248","charStateName":"mucus???","charStateNum":"0"},{"charStateID":"821249","charStateName":"chitinous","charStateNum":"1"},{"charStateID":"821250","charStateName":"calcareous","charStateNum":"2"}]}
    	private String charID;
    	private String charName;
    	private List<MBCharState> charStates;
    	
    	public void setCharID(String s){
    		this.charID = s;
    	}
    	public String getCharID(){
    		return this.charID;
    	}

    	public void setCharName(String s){
    		this.charName = s;
    	}
    	public String getCharName(){
    		return this.charName;
    	}

    	public void setCharStates(List<MBCharState> s){
    		this.charStates = s;
    	}
    	public List<MBCharState> getCharStates(){
    		return this.charStates;
    	}
    	public boolean isShapeAspect(){
    	    return false;
    	    //TODO would there be a way to deduce that character has a shape aspect?
    	}
        public boolean isTextureAspect(){
            return false;
            //TODO would there be a way to deduce that character has a texture aspect?
        }
    	public boolean isPresenceAbsence(){
    		boolean absentFound = false;
    		boolean presentFound = true;
    		for (MBCharState mcs : charStates){
    			if (mcs.representsAbsent() && !this.charName.contains("between") && !this.charName.contains("and")){
    				absentFound = true;
    			}
    			else if (mcs.representsPresent() && !this.charName.contains("between") && !this.charName.contains("and")){
    				presentFound = true;
    			}
    		}
    		if (presentFound && absentFound){
        		return true;
        	}
        	return false;
    	}
        @Override
        public String getType() {
            return "character";
        }
        @Override
        public String getID() {
            return this.getCharID();
        }
        @Override
        public String getName() {
            return this.getCharName();
        }
    }
    
    public static class MBCharState {
    	// {"charStateID":"821248","charStateName":"mucus???","charStateNum":"0"}
    	private String charStateID;
    	private String charStateName;
    	private String charStateNum;
    	
    	public void setCharStateID(String s){
    		this.charStateID = s;
    	}
    	public String getCharStateID(){
    		return this.charStateID;
    	}

    	public void setCharStateName(String s){
    		this.charStateName = s;
    	}
    	public String getCharStateName(){
    		return this.charStateName;
    	}

    	public void setCharStateNum(String s){
    		this.charStateNum = s;
    	}
    	public String getCharStateNum(){
    		return this.charStateNum;
    	}
    	public boolean representsPresent(){
        	String checkableName = this.charStateName.trim().toLowerCase();
        	if (("present".equals(checkableName) || 
        		  checkableName.endsWith("present") || 
        		  checkableName.contains(" present")) &&
        		!(checkableName.contains("between")) && 
        		!(checkableName.contains("and")))	{
    			return true;
    		}
        	return false;
        }
        public boolean representsAbsent(){
        	String checkableName = this.charStateName.trim().toLowerCase();
        	if (("absent".equals(checkableName) || checkableName.endsWith("absent")  || checkableName.contains(" absent")) &&
    		!(checkableName.contains("between")) && !(checkableName.contains("and")))	{
    			return true;
        	}
        	return false;
        }
    }
}
