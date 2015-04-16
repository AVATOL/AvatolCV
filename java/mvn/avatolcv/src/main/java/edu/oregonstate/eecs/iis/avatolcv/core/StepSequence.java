package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

public class StepSequence {
	private List<Step> steps = new ArrayList<Step>();
	private int currentStepIndex = 0;
	
	public void appendStep(Step s){
		steps.add(s);
	}
	public boolean next(){
		int indexOfFinalStep = steps.size() - 1;
		if (currentStepIndex == indexOfFinalStep){
			return false;
		}
		currentStepIndex += 1;
		Step nextStep = steps.get(currentStepIndex);
		return true;
	}
	public Step getCurrentStep(){
		return steps.get(currentStepIndex);
	}
}
