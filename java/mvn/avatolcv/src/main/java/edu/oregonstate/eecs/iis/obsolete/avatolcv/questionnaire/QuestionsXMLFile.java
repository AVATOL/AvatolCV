package edu.oregonstate.eecs.iis.obsolete.avatolcv.questionnaire;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;

public class QuestionsXMLFile {
	private Document doc = null;
    public QuestionsXMLFile(String path) throws AvatolCVException {
    	try {
    		File file = new File(path);
    		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    		this.doc = documentBuilder.parse(file);
    	}
    	catch(ParserConfigurationException pce){
    		throw new AvatolCVException("problem with parser configuration trying to load questions xml file");
    	}
    	catch(SAXException se){
    		throw new AvatolCVException("problem with sax parser trying to load questions xml file");
    	}
    	catch(IOException ioe){
    		throw new AvatolCVException("problem trying to read questions xml file");
    	}
    }
    
    public Node getDomNode(){
    	return this.doc.getFirstChild();
    }

}