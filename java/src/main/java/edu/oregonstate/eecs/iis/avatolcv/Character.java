package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 *  %    <CategoricalCharacter id='c524104'>
	%	      <Representation><Label>GEN skull, dorsal margin, shape at juncture of braincase and rostrum in lateral view</Label></Representation>
	%		  <States>
	%		        <StateDefinition id='cs1168103'><Representation><Label>concave</Label><Detail xml:lang="en" role="number">0</Detail><MediaObject ref="m151258"/></Representation></StateDefinition>
    %                <StateDefinition id='cs1168104'><Representation><Label>flat</Label><Detail xml:lang="en" role="number">1</Detail><MediaObject ref="m151267"/></Representation></StateDefinition>
    %                <StateDefinition id='cs1168105'><Representation><Label>convex</Label><Detail xml:lang="en" role="number">2</Detail></Representation></StateDefinition>
    %          </States><MediaObject ref="m307684"/>
	%      </CategoricalCharacter>
 */
public class Character {
    private String id = "notYetSet";
    private String name = "notYetSet";
    private List<CharacterState> characterStates = null;
    private boolean hasStatePresent = false;
    
    public Character(Node catCharNode) throws MorphobankDataException {
    	this.id = catCharNode.getAttributes().getNamedItem("id").getNodeValue();
        this.name = loadName(catCharNode);
        this.characterStates = loadStates(catCharNode);
       
        if (this.name.equals("notYetSet")){
            String message = "Matrix character error - No representation label for character\n\n" + catCharNode;
            throw new MorphobankDataException(message);
        }
        if (this.id.equals("notYetSet")){
        	String message = "Matrix character error - No id attribute for character for character\n\n" + catCharNode;
            throw new MorphobankDataException(message);
        }
    }
    public String getName(){
    	return this.name;
    }
    public String getId(){
    	return this.id;
    }
    public String loadName(Node catCharNode) throws MorphobankDataException {
    	try {
    		XPath xpath = XPathFactory.newInstance().newXPath();
        	String expression = "Representation/Label";
        	Node labelNode = (Node) xpath.evaluate(expression, catCharNode, XPathConstants.NODE);
        	return labelNode.getTextContent();
    	}
    	catch(XPathExpressionException xee){
    		xee.printStackTrace();
    		throw new MorphobankDataException("could not load name for Character: " + xee.getMessage());
    	}
    	
    }
    public List<CharacterState> loadStates(Node catCharNode) throws MorphobankDataException {
    	try {
    		List<CharacterState> characterStates = new ArrayList<CharacterState>();
        	XPath xpath = XPathFactory.newInstance().newXPath();
        	String expression = "States/StateDefinition";
        	NodeList stateNodes = (NodeList) xpath.evaluate(expression, catCharNode, XPathConstants.NODESET);
        	for (int i = 0; i < stateNodes.getLength(); i++){
        		Node stateNode = stateNodes.item(i);
        		CharacterState state = new CharacterState(this.id, stateNode);
        		characterStates.add(state);
        	}
        	return characterStates;
    	}
    	catch(XPathExpressionException xee){
    		xee.printStackTrace();
    		throw new MorphobankDataException("could not load states for Character: " + xee.getMessage());
    	}
    	
    }
    public boolean isPresentAbsentCharacter(){
    	if (characterStates.size() !=2 ){
    		return false;
    	}
    	boolean presentFound = false;
    	boolean absentFound = false;
    	for (CharacterState state : this.characterStates){
    		String name = state.getName();
    		name = name.trim();
    		name = name.toLowerCase();
    		if ("present".equals(name)){
    			presentFound = true;
    		}
    		else if ("absent".equals(name)){
    			absentFound = true;
    		}
    	}
    	if (presentFound && absentFound){
    		return true;
    	}
    	else {
    		return false;
    	}
    }
}
