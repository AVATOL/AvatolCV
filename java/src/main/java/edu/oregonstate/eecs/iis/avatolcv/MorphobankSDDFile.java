package edu.oregonstate.eecs.iis.avatolcv;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MorphobankSDDFile {
	private String pathname;
	private Matrix matrix;
	private Characters characters;
	
	private Hashtable<String,String> viewsForImage = new Hashtable<String,String>();
	
	private Document document = null;
    public MorphobankSDDFile(String pathname, SPRTaxonIdMapper mapper) throws MorphobankDataException {
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
    public String getTaxonIdForMediaId(String mediaId) throws MorphobankDataException {
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
    public String getCharacterNameForId(String id){
    	return this.characters.getCharacterNameForId(id);
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
}
