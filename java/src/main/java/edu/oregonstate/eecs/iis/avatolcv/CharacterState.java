package edu.oregonstate.eecs.iis.avatolcv;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
    public CharacterState(String charId, Node stateNode) throws MorphobankDataException {
    	this.charId = charId;
    	try {
    		this.id = stateNode.getAttributes().getNamedItem("id").getNodeValue();
    		XPath xpath = XPathFactory.newInstance().newXPath();
    		String expression = "Representation/Label";
    		Node labelNode = (Node) xpath.evaluate(expression, stateNode, XPathConstants.NODE);
    		this.name = labelNode.getTextContent();
    		
    		expression = "Representation/Detail";
    		Node detailNode = (Node) xpath.evaluate(expression, stateNode, XPathConstants.NODE);
    		String numberString = detailNode.getAttributes().getNamedItem("role").getNodeValue();
    		if (numberString.equals("number")){
    			String integerString = detailNode.getTextContent();
    			this.stateNumber = new Integer(integerString).intValue();
    		}
    		else {
    			throw new MorphobankDataException("non-numeric value for state number found for charId " + charId);
    		}
    		
    	}
    	catch(NumberFormatException nfe){
    		nfe.printStackTrace();
    		throw new MorphobankDataException("loading character state encountered non-numeric role attribute of StateDefinition/Representation/Detail " + nfe.getMessage());
    	}
    	catch(XPathExpressionException xee){
    		xee.printStackTrace();
    		throw new MorphobankDataException("problem loading character state " + xee.getMessage());
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
