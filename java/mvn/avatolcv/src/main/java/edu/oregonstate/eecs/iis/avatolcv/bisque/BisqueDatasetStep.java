package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;

public class BisqueDatasetStep implements Step {

	private BisqueWSClient wsClient = null;
	private String view = null;
	private BisqueDataset chosenDataset = null;
	private List<BisqueDataset> datasets = null;
	private BisqueSessionData sessionData = null;
	public BisqueDatasetStep(String view, BisqueWSClient wsClient, BisqueSessionData sessionData){
		this.wsClient = wsClient;
		this.view = view;
		this.sessionData = sessionData;
	}
	@Override
    public void init() {
        // nothing to do
    }
	public List<String> getAvailableDatasets() throws AvatolCVException {
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
			throw new AvatolCVException("problem loading datasets from Bisque ", e);
		}
	}
	public void setChosenDataset(String s) throws AvatolCVException {
		this.chosenDataset = null;
		for (BisqueDataset ds : datasets){
			String name = ds.getName();
			if (name.equals(s)){
				this.chosenDataset = ds;
				//this.sessionData.setChosenDataset(ds);
			}
		}
		if (this.chosenDataset == null){
			throw new AvatolCVException("no BisqueDataset match for name " + s);
		}
	}
	@Override
	public void consumeProvidedData() throws AvatolCVException {
		if (null == this.chosenDataset){
			throw new AvatolCVException("chosenDataset not yet specified.");
		}
		this.sessionData.setChosenDataset(this.chosenDataset);

	}

	@Override
	public String getView() {
		return this.view;
	}

}
