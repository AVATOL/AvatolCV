package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


//<CodedDescription id="row_t277664">
//    <Representation>
//          <Label>Erinaceus europaeus</Label>
//    </Representation>
//    <Scope>
//          <TaxonName ref="t277664"></TaxonName>
//    </Scope>
//    <SummaryData>
//          <Categorical ref="c427605">
//                 <MediaObject ref="m323538"/>
//                 <State ref="s945731"/>
//           </Categorical>
//           <Categorical ref="c427611">
//                  <MediaObject ref="m323536"/>
//                  <State ref="s945749"/>
//           </Categorical>
//           ...
//    </SummaryData>
//</CodedDescription>

public class MatrixRow {
	private String taxonName = "unknown";
	private String taxonId = "unknown";
	private String rowId = "unknown";
	private Hashtable<String, MatrixCell> matrixCellsForCharacter = new Hashtable<String, MatrixCell>();
	private ArrayList<String> charIds = new ArrayList<String>();
	
    public MatrixRow(Node codedDescriptionNode){
    	this.rowId = codedDescriptionNode.getAttributes().getNamedItem("id").getNodeValue();
    	NodeList nodes = codedDescriptionNode.getChildNodes();
    	// first, set the taxonName and Id as well need that for instantiating the MatrixCells
    	for (int i =0; i < nodes.getLength(); i++){
    		Node node = nodes.item(i);
    		String nodeName = node.getNodeName();
    		if (nodeName.equals("Representation")){
    			setTaxonNameFromRepresentationNode(node);
    		}
    		else if (nodeName.equals("Scope")){
    			setTaxonIdFromScopeNode(node);
    		} 
    		else {
    			// do nothing for now
    		}
    	}
    	//System.out.println("LOADING MATRIX ROW FOR " + this.taxonId);
    	// now we load up the MatrixCells and report unknown node names
    	for (int i =0; i < nodes.getLength(); i++){
    		Node node = nodes.item(i);
    		String nodeName = node.getNodeName();
    		if (nodeName.equals("SummaryData")){
    			extractMatrixCellsFromSummaryData(node);
    		}
    		else if (nodeName.equals("Representation")){
    			// already got this
    		}
    		else if (nodeName.equals("Scope")){
    			// already got this
    		}
    		else {
    			System.out.println("WARNING - unrecognized node name in CodedDescription " + nodeName);
    		}
    	}
    }
    public void overRideCellsWithActualTaxonId(String actualTaxonId){
    	for (String charId : charIds){
    		MatrixCell cell = matrixCellsForCharacter.get(charId);
    		cell.overRideTaxonId(actualTaxonId);
    	}
    }
    public List<String> getAllMediaIds(){
    	ArrayList<String> mediaIds = new ArrayList<String>();
    	for (String charId : this.charIds){
    		MatrixCell cell = this.matrixCellsForCharacter.get(charId);
    		List<String> cellMediaIds = cell.getMediaIds();
    		for (String cellMediaId : cellMediaIds){
    			if (!mediaIds.contains(cellMediaId)){
    				mediaIds.add(cellMediaId);
    			}
    		}
    	}
    	return mediaIds;
    }
    public void setTaxonNameFromRepresentationNode(Node node){
    	Node labelNode = node.getFirstChild();
    	this.taxonName = labelNode.getTextContent();
    }
    public void setTaxonIdFromScopeNode(Node node){
    	Node taxonNameNode = node.getFirstChild();
    	this.taxonId = taxonNameNode.getAttributes().getNamedItem("ref").getNodeValue();
    }
    public void extractMatrixCellsFromSummaryData(Node node){
    	NodeList nodes = node.getChildNodes();
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node categoricalNode = nodes.item(i);
    		MatrixCell matrixCell = new MatrixCell(taxonId, categoricalNode);
    		matrixCellsForCharacter.put(matrixCell.getCharId(), matrixCell);
    		this.charIds.add(matrixCell.getCharId());
    	}
    }
    public String getTaxonName(){
    	return this.taxonName;
    }
    public String getTaxonId(){
    	return this.taxonId;
    }
    public MatrixCell getCellForCharacter(String charId){
    	return matrixCellsForCharacter.get(charId);
    }
    public List<String> getCharacterIds(){
    	ArrayList<String> myCharIds = new ArrayList<String>();
    	for (String charId : this.charIds){
    		myCharIds.add(charId);
    	}
    	return myCharIds;
    }
    public int getColumnCount(){
    	return charIds.size();
    }
}

