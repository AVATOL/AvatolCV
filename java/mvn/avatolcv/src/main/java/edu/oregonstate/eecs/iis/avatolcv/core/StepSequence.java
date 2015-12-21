package edu.oregonstate.eecs.iis.avatolcv.core;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.steps.Step;

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
		return true;
	}
	public boolean canBackUp(){
		if (currentStepIndex == 0){
			return false;
		}
		return true;
	}
	public boolean prev(){
		if (currentStepIndex == 0){
			return false;
		}
		currentStepIndex -= 1;
		return true;
	}
	public Step getCurrentStep(){
		return steps.get(currentStepIndex);
	}
	public List<Step> getAllSteps() {
		List<Step> result = new ArrayList<Step>();
		result.addAll(steps);
		return result;
	}
	public boolean hasMoreScreens() {
		int indexOfFinalStep = steps.size() - 1;
		if (currentStepIndex == indexOfFinalStep){
			return false;
		}
		return true;
	}
}
