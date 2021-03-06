package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

public class DataIssue {
	private static final String NL = "\n";
	private static final String TAB = "\t";
	private String description = "";
	private String type = "?";
	private List<String> actionOptions = new ArrayList<String>();
	public String getDescription(){
		return this.description;
	}
	public List<String> getActionOptions(){
		return actionOptions;
	}
	public void setDescription(String s){
		this.description = s;
	}
	public void addActionOption(String s){
		actionOptions.add(s);
	}
	public String getIssueText(int issueNumber){
		StringBuilder sb = new StringBuilder();
    	sb.append("ISSUE " + issueNumber + TAB);
    	sb.append(getDescription() + NL);
    	List<String> options = getActionOptions();
    	for (int i = 0; i < options.size(); i++){
    		String option = options.get(i);
    		sb.append("action option " + (i+1) + " :" + option + NL);
    	}
    	sb.append(NL);
    	return ""+sb;
	}
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
}
