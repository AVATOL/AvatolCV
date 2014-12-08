package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//<Categorical ref="c427605">
//  <MediaObject ref="m323538"/>
//  <State ref="s945731"/>
//</Categorical>
public class MatrixCell {
	private static final String FILESEP = System.getProperty("file.separator");
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
    	//for (String mediaId : this.mediaIds){
    	//	System.out.println("cell loaded: " + charId + " " + mediaId + " " + stateId);
    	//}
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
    /*
     * Due to specimen-per-row matrix, we need to convert the db's taxonId to our choice of the trueTaxonId
     */
    public void overRideTaxonId(String trueTaxonId){
    	this.taxonId = trueTaxonId;
    }
    public List<String> getMediaIds(){
    	List<String> myMediaIds = new ArrayList<String>();
    	for (String mediaId : this.mediaIds){
    		myMediaIds.add(mediaId);
    	}
    	return myMediaIds;
    }
    public boolean isUnscored(){
    	if (this.stateId.equals("?")){
    		return true;
    	}
    	return false;
    }
    public boolean hasWorkableScore(){
    	return CharacterState.isCharacterStateIdCvFriendly(this.stateId);
    }
    public boolean hasAnnotationFile(String annotationDir){
    	for (String mediaId : this.mediaIds){

        	String filename = mediaId + "_" + this.charId + ".txt";
        	String path = annotationDir + FILESEP + filename;
        	File f = new File(path);
        	if (f.exists()){
        		return true;
        	}
    	}
    	return false;
    }
}
