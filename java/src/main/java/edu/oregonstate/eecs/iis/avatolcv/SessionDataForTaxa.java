package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SessionDataForTaxa {
    private List<SessionDataForTaxon> sessionDatas = new ArrayList<SessionDataForTaxon>();
    private Hashtable<String, SessionDataForTaxon> taxonSessionDataForTaxonId = new Hashtable<String, SessionDataForTaxon>();
	public SessionDataForTaxa(String charId, String charName,
			List<ResultImage> trainingImages,
			List<ResultImage> scoredImages,
			List<ResultImage> unscoredImages){
		List<String> taxonIds = getTaxonIdsFromResultImages(trainingImages);
		for (String taxonId : taxonIds){
			List<ResultImage> trainingImagesForTaxon = getResultImagesForTaxon(taxonId, trainingImages);
			List<ResultImage> scoredImagesForTaxon = getResultImagesForTaxon(taxonId, scoredImages);
			List<ResultImage> unscoredImagesForTaxon = getResultImagesForTaxon(taxonId, unscoredImages);
			SessionDataForTaxon sdft = new SessionDataForTaxon(taxonId, 
					charId, 
					charName,
					trainingImagesForTaxon,
					scoredImagesForTaxon,
					unscoredImagesForTaxon);
			sessionDatas.add(sdft);
			taxonSessionDataForTaxonId.put(taxonId, sdft);
		}
	}
	public SessionDataForTaxon getSessionDataForTaxonId(String taxonId) throws AvatolCVException {
		SessionDataForTaxon sdft =  taxonSessionDataForTaxonId.get(taxonId);
		if (null == sdft){
			throw new AvatolCVException("no session data for taxonId " + taxonId);
		}
		return sdft;
	}
	public SessionDataForTaxon getSessionDataForTaxonAtIndex(int index) throws AvatolCVException {
		if (index >= sessionDatas.size()){
			throw new AvatolCVException("bad index for SessionDataForTaxon " + index + " where max is " + (sessionDatas.size() - 1));
		}
		return sessionDatas.get(index);
	}
	public int getTaxonCount(){
		return sessionDatas.size();
	}
	public static List<String> getTaxonIdsFromResultImages(List<ResultImage> resultImages){
		List<String> taxonIds = new ArrayList<String>();
		for (ResultImage ri : resultImages){
			String taxonId = ri.getTaxonId();
			if (!(taxonIds.contains(taxonId))){
				taxonIds.add(taxonId);
			}
		}
		return taxonIds;
	}
	public static List<ResultImage> getResultImagesForTaxon(String taxonId, List<ResultImage> resultImages){
    	List<ResultImage> filteredList = new ArrayList<ResultImage>();
    	for (ResultImage ri : resultImages){
    		if (ri.getTaxonId().equals(taxonId)){
    			filteredList.add(ri);
    		}
    	}
    	return filteredList;
    }
}
