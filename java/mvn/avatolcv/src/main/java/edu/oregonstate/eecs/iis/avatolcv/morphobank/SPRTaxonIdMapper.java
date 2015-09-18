package edu.oregonstate.eecs.iis.avatolcv.morphobank;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class SPRTaxonIdMapper {
	Hashtable<String,String> map = new Hashtable<String, String>();
	Hashtable<String,List<String>> taxonIdsForTaxonName = new Hashtable<String,List<String>>();
	Hashtable<String, String> canonicalTaxonIdForName = new Hashtable<String,String>();
	List<String> taxonNames = new ArrayList<String>();
	Hashtable<String, String> canonicalTaxonNameForName = new Hashtable<String, String>();
    public SPRTaxonIdMapper(List<MBTaxon> taxa) {
    	loadMap(taxa);
    }
    //<TaxonNames>
	//<TaxonName id='t171198'><Label><Text>Artibeus jamaicensis 209619</Text></Label></TaxonName>
	//<TaxonName id='t171302'><Label><Text>Artibeus jamaicensis 209627</Text></Label></TaxonName>
    public void loadMap(List<MBTaxon> taxa){
    	
    	for (MBTaxon taxon : taxa){
    		String taxonId = taxon.getTaxonID();
			String fullTextName = taxon.getTaxonName();
			String identifierString = getIdentifierFromTaxonName(fullTextName);
			String suffixToTrim = " " + identifierString;
			String pureTaxonName = fullTextName.replace(suffixToTrim, "");
			this.canonicalTaxonNameForName.put(fullTextName, pureTaxonName);
			registerEntry(pureTaxonName, taxonId);
    		
    	}
    }
    public String getPureTaxonNameForName(String taxonName) throws AvatolCVException {
    	String pureName = this.canonicalTaxonNameForName.get(taxonName);
    	if (null == pureName){
    		throw new AvatolCVException("no canonical taxon name for given name " + taxonName);
    	}
    	return pureName;
    }
    public void registerEntry(String taxonName, String taxonId){
    	if (taxonNames.contains(taxonName)){
    		// another instance of previously seen taxonName
    		String canonicalTaxonId = canonicalTaxonIdForName.get(taxonName);
    		map.put(taxonId,canonicalTaxonId);
    	}
    	else {
    		// new taxon name
    		taxonNames.add(taxonName);
    		canonicalTaxonIdForName.put(taxonName, taxonId);
    		map.put(taxonId,taxonId);
    	}
    	List<String> taxonIds = taxonIdsForTaxonName.get(taxonName);
    	if (null == taxonIds){
    		taxonIds = new ArrayList<String>();
    		taxonIdsForTaxonName.put(taxonName, taxonIds);
    	}
    	if (!taxonIds.contains(taxonId)){
    		taxonIds.add(taxonId);
    	}
    }
    public String getIdentifierFromTaxonName(String s){
    	String[] parts = s.split(" ");
    	int length = parts.length;
    	String identifier = parts[length - 1];
    	return identifier;
    }
    public String getNormalizedTaxonId(String taxonId){
    	String mapped = this.map.get(taxonId);
    	if (null == mapped){
    		return taxonId;
    	}
    	return mapped;
    }
}
