package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

public class CharStateInfo {
//{"ok":true,"charStates":[{"charStateID":"inapplicable"}]}
	
// hmmmm... now it's {"ok":true,"charStates":[{"cellID":"33100198","charStateID":"1157845"},{"cellID":"33100199","charStateID":"1157844"}]}
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
		private String cellID;
		private String charStateID;
		
		public void setCharStateID(String s){
			this.charStateID = s;
		}
		public String getCharStateID(){
			return this.charStateID;
		}

		public void setCellID(String s){
			this.cellID = s;
		}
		public String getCellID(){
			return this.cellID;
		}
	}
}
