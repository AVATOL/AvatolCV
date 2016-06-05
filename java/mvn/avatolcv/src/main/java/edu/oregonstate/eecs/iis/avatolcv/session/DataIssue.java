package edu.oregonstate.eecs.iis.avatolcv.session;

import java.util.ArrayList;
import java.util.List;

public class DataIssue {
	private String description = "";
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
    	sb.append("ISSUE " + issueNumber + '\n');
    	sb.append(getDescription() + '\n');
    	List<String> options = getActionOptions();
    	for (int i = 0; i < options.size(); i++){
    		String option = options.get(i);
    		sb.append('\t' + "OPTION " + (i+1) + " :" + option + '\n');
    	}
    	return ""+sb;
	}
}
