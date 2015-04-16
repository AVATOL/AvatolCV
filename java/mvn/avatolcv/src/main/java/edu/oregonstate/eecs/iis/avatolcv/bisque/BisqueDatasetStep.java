package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;

public class BisqueDatasetStep implements Step {

	private BisqueWSClient wsClient = null;
	private View view = null;
	private BisqueDataset chosenDataset = null;
	private List<BisqueDataset> datasets = null;
	private BisqueSessionData sessionData = null;
	public BisqueDatasetStep(View view, BisqueWSClient wsClient, BisqueSessionData sessionData){
		this.wsClient = wsClient;
		this.view = view;
		this.sessionData = sessionData;
	}
	public List<String> getAvailableDatasets() throws BisqueSessionException {
		try {
			List<String> result = new ArrayList<String>();
			this.datasets = wsClient.getDatasets();
			Collections.sort(this.datasets);
			for (BisqueDataset ds : this.datasets){
				String name = ds.getName();
				result.add(name);
			}
			Collections.sort(result);
			return result;
		}
		catch(BisqueWSException e){
			throw new BisqueSessionException("problem loading datasets from Bisque ", e);
		}
	}
	public void setChosenDataset(String s) throws BisqueSessionException {
		this.chosenDataset = null;
		for (BisqueDataset ds : datasets){
			String name = ds.getName();
			if (name.equals(s)){
				this.chosenDataset = ds;
				//this.sessionData.setChosenDataset(ds);
			}
		}
		if (this.chosenDataset == null){
			throw new BisqueSessionException("no BisqueDataset match for name " + s);
		}
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		if (null == this.chosenDataset){
			throw new BisqueSessionException("chosenDataset not yet specified.");
		}
		this.sessionData.setChosenDataset(this.chosenDataset);

	}

	@Override
	public boolean needsAnswering() {
		if (null == this.chosenDataset){
			return true;
		}
		return false;
	}
	@Override
	public View getView() {
		return this.view;
	}

}
