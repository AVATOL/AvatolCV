package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SPRTaxonIdMapper {
	Hashtable<String,String> map = new Hashtable<String, String>();
	Hashtable<String,List<String>> taxonIdsForTaxonName = new Hashtable<String,List<String>>();
	Hashtable<String, String> canonicalTaxonIdForName = new Hashtable<String,String>();
	List<String> taxonNames = new ArrayList<String>();
    public SPRTaxonIdMapper(String path) throws MorphobankDataException {
    	Document doc = MorphobankSDDFile.getDocumentFromPathname(path);
    	loadMap(doc);
    }
    //<TaxonNames>
	//<TaxonName id='t171198'><Label><Text>Artibeus jamaicensis 209619</Text></Label></TaxonName>
	//<TaxonName id='t171302'><Label><Text>Artibeus jamaicensis 209627</Text></Label></TaxonName>
    public void loadMap(Document doc){
    	// find taxonNames elements in the document - pick the first one
    	NodeList nodes = doc.getElementsByTagName("TaxonNames");
    	Node taxonNamesNode = nodes.item(0);
    	// from it, pick all the TaxonName elements
    	NodeList taxonNameNodes = taxonNamesNode.getChildNodes();
    	for (int i = 0; i < taxonNameNodes.getLength(); i++){
    		Node taxonNameNode = taxonNameNodes.item(i);
    		if (taxonNameNode.getNodeName().equals("TaxonName")){
    			String taxonId = taxonNameNode.getAttributes().getNamedItem("id").getNodeValue();
    			Node textNode = taxonNameNode.getFirstChild().getFirstChild();
    			String fullTextName = textNode.getTextContent();
    			String identifierString = getIdentifierFromTaxonName(fullTextName);
    			String suffixToTrim = " " + identifierString;
    			String pureTaxonName = fullTextName.replace(suffixToTrim, "");
    			registerEntry(pureTaxonName, taxonId);
    		}
    	}
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
