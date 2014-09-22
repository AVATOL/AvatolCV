package edu.oregonstate.eecs.iis.avatolcv;


import org.w3c.dom.Document;
import org.w3c.dom.Node;

//<StateDefinition id='cs1168105'>
//    <Representation>
//         <Label>convex</Label>
//         <Detail xml:lang="en" role="number">2</Detail>
//    </Representation></StateDefinition>

public class CharacterState {
	private String id;
	private String name;
	private int stateNumber;
	private String charId;
	private Document document;
    public CharacterState(String charId, Node stateNode, Document document) throws MorphobankDataException {
    	this.charId = charId;
    	this.document = document;
    	try {
    		// see Character.java on why xpath was abandoned
    		this.id = stateNode.getAttributes().getNamedItem("id").getNodeValue();
    		Node representationNode = Character.getChildNodeNamed("Representation", stateNode, this.document);
    		Node labelNode = Character.getChildNodeNamed("Label", representationNode, this.document);
    		this.name = labelNode.getTextContent();
    		//System.out.println("charState name   : " + this.name);
    		Node detailNode = Character.getChildNodeNamed("Detail", representationNode, this.document);
    		String numberString = detailNode.getAttributes().getNamedItem("role").getNodeValue();
    		if (numberString.equals("number")){
    			String integerString = detailNode.getTextContent();
    			this.stateNumber = new Integer(integerString).intValue();
        		//System.out.println("charState number : " + this.stateNumber);
    		}
    		else {
    			throw new MorphobankDataException("non-numeric value for state number found for charId " + charId);
    		}
    		
    	}
    	catch(NumberFormatException nfe){
    		nfe.printStackTrace();
    		throw new MorphobankDataException("loading character state encountered non-numeric role attribute of StateDefinition/Representation/Detail " + nfe.getMessage());
    	}
    }
    public String getName(){
    	return this.name;
    }
    public int getStateNumber(){
    	return this.stateNumber;
    }
    public String getId(){
    	return this.id;
    }
    public String getCharId(){
    	return this.charId;
    }
}
