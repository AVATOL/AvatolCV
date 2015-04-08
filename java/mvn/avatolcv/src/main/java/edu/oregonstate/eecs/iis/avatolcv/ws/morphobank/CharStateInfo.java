package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

public class CharStateInfo {
//{"ok":true,"charStates":[{"charStateID":"inapplicable"}]}
	private String ok;
	private List<MBCharStateValue> charStates;
	
	public void setOk(String s){
		this.ok = s;
	}
	public void setCharStates(List<MBCharStateValue> s){
		this.charStates = s;
	}
	public List<MBCharStateValue> getCharStates(){
		return this.charStates;
	}
	public static class MBCharStateValue {
		//{"charStateID":"inapplicable"}
		private String charStateID;
		
		public void setCharStateID(String s){
			this.charStateID = s;
		}
		public String getCharStateID(){
			return this.charStateID;
		}
	}
}
