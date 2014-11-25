package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class MorphobankSDDFile {
	private static final String FILESEP = System.getProperty("file.separator");
	private Media media;
	private String pathname;
	private Matrix matrix;
	private Characters characters;
	private ArrayList<String> viewIds = new ArrayList<String>();
	private Hashtable<String,String> viewNamesForId = new Hashtable<String, String>();
	private Hashtable<String,String> viewIdsForName = new Hashtable<String, String>();
	private Hashtable<String,String> viewsForImage = new Hashtable<String,String>();
	private SPRTaxonIdMapper mapper = null;
	private Document document = null;
    public MorphobankSDDFile(String pathname, SPRTaxonIdMapper mapper, Media media) throws MorphobankDataException {
    	this.mapper = mapper;
    	this.media = media;
    	this.pathname = pathname;
    	this.document = getDocumentFromPathname(pathname);
    	if (null != mapper){
    		this.matrix = new SPRMatrix(this.document, mapper);
    	}
    	else {
    		this.matrix = new Matrix(this.document);
    	}
    	this.matrix.loadTaxonsForMedia();
    	this.characters = new Characters(this.document);
    	loadViewsForImage(this.document);
    	loadViewsForDocument(this.document);
    }
    public SPRTaxonIdMapper getTaxonIdMapper(){
    	return this.mapper;
    }
    public String getTaxonNameForId(String taxonId) throws AvatolCVException {
    	return this.matrix.getTaxonNameForId(taxonId);
    }
    public String getTaxonIdForName(String taxonName) throws AvatolCVException {
    	return this.matrix.getTaxonIdForName(taxonName);
    }
    public List<String> getViewNames(){
    	ArrayList<String> list = new ArrayList<String>();
    	for (String id : this.viewIds){
    		String name = this.viewNamesForId.get(id);
    		list.add(name);
    	}
    	return list;
    }
    public String getViewIdForName(String name) throws AvatolCVException {
    	String id = this.viewIdsForName.get(name);
    	if (null == id){
    		throw new AvatolCVException("no viewId for name " + name);
    	}
    	return id;
    }
    public void feedMessageDigestSDDContent(MessageDigest m) throws AvatolCVException {
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader(this.pathname));
    		String line = null;
    		while((line = reader.readLine()) != null){
    			m.update(line.getBytes());
    		}
    		reader.close();
    	}
    	catch(FileNotFoundException fnfe){
    		throw new AvatolCVException("SDD not found : " + this.pathname);
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("Problem loading SDD file : " + this.pathname);
    	}
    }
    /*
     * <MediaObject id='m151500'>
     *       <Representation>
     *           <Label>Artibeus jamaicensis 210891 (AMNH/Mammalogy:210891)</Label>
     *           <DescriptiveConcept ref="v6825"/>
     *       </Representation>
     *       <Type>image/jpeg</Type>
     *       <Data href="file:/media/M151500_"/>
     * </MediaObject>
     */
    
    public String getViewIdFromMediaObject(Node mediaObjectNode)  {
    	NodeList nodes = mediaObjectNode.getChildNodes();
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node node = nodes.item(i);
    		if (node.getNodeName().equals("Representation")){
    			NodeList children = node.getChildNodes();
    			for (int j = 0; j < children.getLength(); j++){
    				Node candidate = children.item(j);
    				if (candidate.getNodeName().equals("DescriptiveConcept")){
    					String viewId = candidate.getAttributes().getNamedItem("ref").getNodeValue();
    					return viewId;
    				}
    			}
    		}
    	}
    	return null;
    }
    public Node getChildNodeNamed(Node n, String name) throws MorphobankDataException {
    	NodeList list = n.getChildNodes();
    	for (int i = 0; i < list.getLength(); i++){
			Node curN = list.item(i);
			String thisNodeName = curN.getNodeName();
			if (thisNodeName.equals(name)){
				return curN;
			}
		}
    	throw new MorphobankDataException("no child node of " + n.getNodeName() + " called " + name + " found.");
    }
 
    public void loadViewsForDocument(Document doc) throws MorphobankDataException {
    	NodeList descriptiveConceptsNodes = doc.getElementsByTagName("DescriptiveConcepts");
    	Node descriptiveConceptsNode = descriptiveConceptsNodes.item(0);
    	NodeList dcNodes = descriptiveConceptsNode.getChildNodes();
    	for (int i = 0; i < dcNodes.getLength(); i++){
    		Node dcNode = dcNodes.item(i);
    		String name = dcNode.getNodeName();
    		if ("DescriptiveConcept".equals(name)){
    			//<DescriptiveConcept id="v3538"><Representation><Label>Skull - dorsal</Label></Representation></DescriptiveConcept>
        		NamedNodeMap map = dcNode.getAttributes();
        		Node attrNode = map.getNamedItem("id");
        	    String viewId =	attrNode.getNodeValue();
        		Node representationNode = getChildNodeNamed(dcNode, "Representation");
        		Node labelNode = getChildNodeNamed(representationNode, "Label");
        		String viewName = labelNode.getTextContent();
        		this.viewIds.add(viewId);
        		this.viewNamesForId.put(viewId, viewName);
        		this.viewIdsForName.put(viewName, viewId);
    		}
    	}
    }
    public void loadViewsForImage(Document doc){
    	NodeList nodes = doc.getElementsByTagName("MediaObjects");
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node mediaObjectsNode = nodes.item(i);
    		NodeList children = mediaObjectsNode.getChildNodes();
    		for (int j = 0; j < children.getLength(); j++){
    			Node candidate = children.item(j);
    			if (candidate.getNodeName().equals("MediaObject")){
    				String mediaId = candidate.getAttributes().getNamedItem("id").getNodeValue();
    				String viewId = getViewIdFromMediaObject(candidate);
    				if (null == viewId){
    					viewsForImage.put(mediaId, "viewNotSpecified");
    				}
    				else {
    					viewsForImage.put(mediaId, viewId);
    				}
    			}
    		}
    	}
    }
    public String getTaxonIdForMediaId(String mediaId) throws AvatolCVException {
    	return this.matrix.getTaxonIdForMediaId(mediaId);
    }
    public Matrix getMatrix(){
    	return this.matrix;
    }
    
    /*
     * <Specimens>
		<Specimen id='s25176'>
		    <Representation><Label>AMNH/Mammalogy:248750</Label><Detail xml:lang="en" role="description">Female</Detail>
		        <MediaObject ref='m151254'/><MediaObject ref='m151257'/><MediaObject ref='m151258'/><MediaObject ref='m309494'/>
		        <MediaObject ref='m309496'/><MediaObject ref='m309497'/><MediaObject ref='m309500'/><MediaObject ref='m309518'/>
		        <MediaObject ref='m309521'/><MediaObject ref='m309676'/><MediaObject ref='m309678'/><MediaObject ref='m309679'/>
		        <MediaObject ref='m309680'/><MediaObject ref='m309681'/><MediaObject ref='m309689'/><MediaObject ref='m309693'/>
		        <MediaObject ref='m309694'/><MediaObject ref='m309859'/><MediaObject ref='m309862'/><MediaObject ref='m309867'/>
		        <MediaObject ref='m309932'/><MediaObject ref='m309934'/><MediaObject ref='m309938'/><MediaObject ref='m310134'/>
		        <MediaObject ref='m323843'/>
		    </Representation>
		    <TaxonName ref='t171056'/>
		</Specimen>
     
    // this was found to not always work and so Matrix now handles this association
    public void loadTaxonsForMediaOld()throws MorphobankDataException{
    	NodeList specimens = this.document.getElementsByTagName("Specimen");
        for (int i=0; i < specimens.getLength(); i++){
        	Node specimenNode = specimens.item(i);
        	
        	NodeList taxonNodes = ((Element)specimenNode).getElementsByTagName("TaxonName");
        	Node taxonNode = taxonNodes.item(0);// should be only one
        	String taxonId = taxonNode.getAttributes().getNamedItem("ref").getNodeValue();
        	NodeList mediaNodes = ((Element)specimenNode).getElementsByTagName("MediaObject");
            for (int j=0; j < mediaNodes.getLength(); j++){
            	Node mediaNode = mediaNodes.item(j);
                String mediaId = mediaNode.getAttributes().getNamedItem("ref").getNodeValue();
                System.out.println("associating media and taxon : " + taxonId + " - " + mediaId);
                this.taxonsForMediaId.put(mediaId,taxonId);
            }
        }
    }*/
    public static Document getDocumentFromPathname(String path) throws MorphobankDataException {
    	try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder(); 
        	Document doc = db.parse(new File(path));
        	return doc;
    	}
    	catch (IOException ioe){
    		ioe.printStackTrace();
    		throw new MorphobankDataException("problem opening sdd xml file " + path);
    	}
    	catch(ParserConfigurationException pce){
    		pce.printStackTrace();
    		throw new MorphobankDataException("problem with parser of sdd xml file " + path);
    	}
    	catch(SAXException se){
    		se.printStackTrace();
    		throw new MorphobankDataException("problem parsing sdd xml file " + path);
    	}
    }
    public List<String> getPresenceAbsenceCharacterNames(){
    	System.out.println("collecting presence absence char names ");
    	List<String> charNames = new ArrayList<String>();
    	List<Character> chars = characters.getPresenceAbsenceCharacters();
    	for (Character aChar : chars){
    		String name = aChar.getName();
    		//System.out.println("name : " + name);
    		charNames.add(name);
    	}
    	return charNames;
    }
    public List<Character> getPresenceAbsenceCharacters(){
    	return characters.getPresenceAbsenceCharacters();
    }
    public List<MatrixCell> getPresenceAbsenceCharacterCells() {
    	List<Character> chars = characters.getPresenceAbsenceCharacters();
    	List<MatrixCell> allMatrixCells = new ArrayList<MatrixCell>();
    	for (Character aChar: chars){
    		String charId = aChar.getId();
    		List<MatrixCell> matrixCells = this.matrix.getCellsForCharacter(charId);
    		allMatrixCells.addAll(matrixCells);
    	}
    	return allMatrixCells;
    }
    public List<MatrixCell> getPresenceAbsenceCellsForCharacter(String charId) {
    	List<Character> chars = characters.getPresenceAbsenceCharacters();
    	List<MatrixCell> allMatrixCells = new ArrayList<MatrixCell>();
    	for (Character aChar: chars){
    		String thisCharId = aChar.getId();
    		if (charId.equals(thisCharId)){
    			List<MatrixCell> matrixCells = this.matrix.getCellsForCharacter(charId);
        		allMatrixCells.addAll(matrixCells);
    		}
    	}
    	return allMatrixCells;
    }
    public List<String> getNotPresentTrainingDataLines(MatrixCell notPresentCell) throws AvatolCVException {
    	List<String> trainingLines = new ArrayList<String>();
    	String delim = Annotation.ANNOTATION_DELIM;
    	List<String> mediaIds = notPresentCell.getMediaIds();
    	for (String mediaId : mediaIds){
    		String mediaFilename = this.media.getMediaFilenameForMediaId(mediaId);
    		if (null != mediaFilename){
    			String charId = notPresentCell.getCharId();
        		Character character = this.characters.getCharacterForId(charId);
        		String charStateId = notPresentCell.getState();
        		CharacterState characterState = character.getCharacterStateForId(charStateId);
        		String charStateText = characterState.getName();
        		String taxonId = getTaxonIdForMediaId(mediaId);
        		String trainingLine = "training_data" + delim + "media" + FILESEP + mediaFilename + delim + 
                        charStateId + delim + charStateText + delim + "NA" + delim + taxonId + delim + "NA";
        		trainingLines.add(trainingLine);
    		}
    	}
    	return trainingLines; 
    }
    public String getCharacterNameForId(String id){
    	return this.characters.getCharacterNameForId(id);
    }
    public String getCharacterIdForName(String id){
    	return this.characters.getCharacterIdForName(id);
    }
    public Character getCharacterForId(String id) throws AvatolCVException {
    	return this.characters.getCharacterForId(id);
    }
    public boolean isMediaOfTaxon(String mediaId, String taxonId) throws AvatolCVException {
    	String actualTaxonId = this.matrix.getTaxonIdForMediaId(mediaId);
    	if (taxonId.equals(actualTaxonId)){
    		return true;
    	}
    	return false;
    }
    public boolean isMediaOfView(String mediaId, String viewId){
    	String mappedView = this.viewsForImage.get(mediaId);
    	if (null == mappedView){
    		return false;
    	}
    	else {
    		if (mappedView.equals(viewId)){
    			return true;
    		}
    	}
    	return false;
    }
    public String getViewIdForMediaId(String mediaId){
    	String viewId = this.viewsForImage.get(mediaId);
    	return viewId;
    }
    public String getViewNameForId(String viewId){
    	String viewName = this.viewNamesForId.get(viewId);
    	return viewName;
    }
}
