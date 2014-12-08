package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

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
    private static final String NL = System.getProperty("line.separator");
    private String id = "notYetSet";
    private String name = "notYetSet";
    private List<CharacterState> characterStates = null;
    private boolean hasStatePresent = false;
    private Document document = null;
    public Character(Node catCharNode, Document doc) throws MorphobankDataException {
    	this.document = doc;
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
    public boolean isStateIdRepresentingAbsent(String stateId) throws AvatolCVException{
    	if (!CharacterState.isCharacterStateIdCvFriendly(stateId)){
    		return false;
    	}
    	String normStateId = normalizeCharStateId(stateId);
    	CharacterState cs = getCharacterStateForId(normStateId);
    	if (cs.representsAbsent()){
    		return true;
    	}
    	return false;
    }
    public String normalizeCharStateIdBROKEN(String id) {
    	if (id.startsWith("cs")){
    		String s = id.substring(1);
    		return s;
    	}
    	else if (id.startsWith("s")){
    		return id;
    	}
    	else {
    		return id;
    	}
    }

    public String normalizeCharStateId(String id) throws AvatolCVException {
    	if (id.startsWith("cs")){
    		return id;
    	}
    	else if (id.startsWith("s")){
    		return "c" + id;
    	}
    	else {
    		throw new AvatolCVException("unrecognized format for stateId : " + id + " ... expecting id to start with cs or s");
    	}
    }
    public CharacterState getCharacterStateForId(String stateId) throws AvatolCVException {
    	String normStateId = normalizeCharStateId(stateId);
    	for (CharacterState characterState : this.characterStates){
    		if (characterState.getId().equals(normStateId)){
    			return characterState;
    		}
    	}
    	throw new AvatolCVException("No CharacterState entry with id " + stateId + " in Character " + this.id);
    }
    public String getName(){
    	return this.name;
    }
    public String getId(){
    	return this.id;
    }
    public static Node getChildNodeNamed(String name, Node node, Document doc) throws MorphobankDataException{
    	NodeList nodes = node.getChildNodes();
    	//System.out.println("seeking child node named " + name);
    	for (int i = 0; i < nodes.getLength(); i++){
    	    Node child = nodes.item(i);
    	    //System.out.println("getChildNodeNamed... node name " + child.getNodeName());
    	    if (child.getNodeName().equals(name)){
    	    	return child;
    	    }
    	}
    	DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
    	LSSerializer serializer = domImplLS.createLSSerializer();
    	String str = serializer.writeToString(node);
		throw new MorphobankDataException("could not find child node of type : " + name + " in " + str);
    }
    /*
     * for some reason the xpath evaluate fails under matlab where it succeeds under junit!.  SO, need to 
     * brute force the search.
     */
    public String loadName(Node catCharNode) throws MorphobankDataException {
    	//System.out.println("loading name for character..." + this.id);
    	Node representationNode = getChildNodeNamed("Representation", catCharNode, this.document);
    	Node labelNode = getChildNodeNamed("Label",representationNode, this.document);
    	return labelNode.getTextContent();	
    	
    }
    /*
     * for some reason the xpath evaluate below fails under matlab where it succeeds under junit!.  SO, need to 
     * brute force the search using the new loadName method.
     */
    public String loadNameOrig(Node catCharNode) throws MorphobankDataException {
    	//System.out.println("loading name for character..." + this.id);
    	DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
    	LSSerializer serializer = domImplLS.createLSSerializer();
    	String str = serializer.writeToString(catCharNode);
    	System.out.println(str);
    	try {
    		XPath xpath = XPathFactory.newInstance().newXPath();
        	String expression = "Representation/Label";
        	System.out.println("expression : " + expression);
        	Node labelNode = (Node) xpath.evaluate(expression, catCharNode, XPathConstants.NODE);
        	if (labelNode == null){
        		System.out.println("labelNode returned null!");
        	}
        	return labelNode.getTextContent();
    	}
    	catch(XPathExpressionException xee){
    		xee.printStackTrace();
    		throw new MorphobankDataException("could not load name for Character: " + xee.getMessage());
    	}
    }
    public List<CharacterState> loadStates(Node catCharNode) throws MorphobankDataException  {
		List<CharacterState> characterStates = new ArrayList<CharacterState>();
		Node statesNode = getChildNodeNamed("States", catCharNode, this.document);
    	NodeList candidates = statesNode.getChildNodes();
    	for (int i = 0; i < candidates.getLength(); i++){
    		Node candidate = candidates.item(i);
    		if (candidate.getNodeName().equals("StateDefinition")){
    			CharacterState state = new CharacterState(this.id, candidate, this.document);
        		characterStates.add(state);
    		}
    	}
    	return characterStates;
    }
    public boolean isPresentAbsentCharacter(){
    	if (characterStates.size() !=2 ){
    		return false;
    	}
    	boolean presentFound = false;
    	boolean absentFound = false;
    	for (CharacterState state : this.characterStates){
    		if (state.representsPresent()){
    			presentFound = true;
    		}
    		else if (state.representsAbsent()){
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
    public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append(this.name + "," + this.id + NL);
    	sb.append("states:" + NL);
    	for (CharacterState state : this.characterStates){
    		sb.append(state.getName() + NL);
    	}
    	return "" + sb;
    }
    public List<CharacterState> getCharacterStates(){
    	List result = new ArrayList<CharacterState>();
    	result.addAll(this.characterStates);
    	return result;
    }
}
