package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//<Categorical ref="c427605">
//  <MediaObject ref="m323538"/>
//  <State ref="s945731"/>
//</Categorical>
public class MatrixCell {
	private String taxonId;
	private String charId;
	private List<String> mediaIds = new ArrayList<String>();
	private String stateId;
    public MatrixCell(String taxonId, Node categoricalNode){
    	this.taxonId = taxonId;
    	this.charId = categoricalNode.getAttributes().getNamedItem("ref").getNodeValue();
    	NodeList nodes = categoricalNode.getChildNodes();
    	// first, set the taxonName and Id as well need that for instantiating the MatrixCells
    	for (int i =0; i < nodes.getLength(); i++){
    		Node node = nodes.item(i);
    		String nodeName = node.getNodeName();
    		if (nodeName.equals("MediaObject")){
    			this.mediaIds.add(node.getAttributes().getNamedItem("ref").getNodeValue());
    		}
    		else if (nodeName.equals("State")){
    			this.stateId = node.getAttributes().getNamedItem("ref").getNodeValue();
    		} 
    		else {
    			// do nothing for now
    		}
    	}
    	for (String mediaId : this.mediaIds){
    		System.out.println("cell loaded: " + charId + " " + mediaId + " " + stateId);
    	}
    }
    public boolean isScored(){
    	if (stateId.equals("s")){
    		return false;
    	}
    	return true;
    }
    public boolean hasMedia(){
    	if (this.mediaIds.size() == 0){
    		return false;
    	}
    	return true;
    }
   
    public String getState(){
    	return this.stateId;
    }
    public String getCharId(){
    	return this.charId;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
    public List<String> getMediaIds(){
    	List<String> myMediaIds = new ArrayList<String>();
    	for (String mediaId : this.mediaIds){
    		myMediaIds.add(mediaId);
    	}
    	return myMediaIds;
    }
}
