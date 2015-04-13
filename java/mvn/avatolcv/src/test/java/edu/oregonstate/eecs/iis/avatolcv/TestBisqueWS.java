package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;



import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationComboBoxTemplateResource;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBisqueWS extends TestCase {
/*
	public void testAuthenticate(){
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean result = bc.authenticate("avatol-nybg","Monocots123");
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	*/
	/*
	public void testGetDatasets() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			
			List<BisqueDataset> datasets = bc.getDatasets();
			for (BisqueDataset ds : datasets){
				System.out.println("");
				System.out.println("name          " + ds.getName());
				System.out.println("resource_uniq " + ds.getResourceUniq());
				System.out.println("created       " + ds.getCreated());
				System.out.println("owner         " + ds.getOwner());
				System.out.println("permission    " + ds.getPermission());
				System.out.println("ts            " + ds.getTs());
				System.out.println("uri           " + ds.getUri());
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	*/
	/*
	public void testGetImages() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			
			List<BisqueImage> images = bc.getImagesForDataset("00-kutq3fez25ntEkp5pdXhMM");
			for (BisqueImage image : images){
				System.out.println("");
				System.out.println("name          " + image.getName());
				System.out.println("resource_uniq " + image.getResourceUniq());
				System.out.println("created       " + image.getCreated());
				System.out.println("owner         " + image.getOwner());
				System.out.println("permission    " + image.getPermission());
				System.out.println("ts            " + image.getTs());
				System.out.println("uri           " + image.getUri());
				System.out.println("value         " + image.getValue());
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	*/
	

	/*
	public void testDownloadImage() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			String imageDir = "C:\\\\avatol\\temp";
			File f = new File(imageDir);
			if (!f.exists()){
				f.mkdirs();
			}
			boolean result = bc.downloadImageOfWidth("00-LJTH5L79oY5zdS6PFaYbcS", 400, imageDir, "medium", "imageX");
			Assert.assertEquals(result, true);
		}
		catch(Exception ex){
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}
	*/
	/*
	public void testAnnotations() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			
			List<BisqueAnnotation> annotations = bc.getAnnotationsForImage("00-LJTH5L79oY5zdS6PFaYbcS");
			for (BisqueAnnotation ds : annotations){
				System.out.println("");
				System.out.println("name          " + ds.getName());
				System.out.println("created       " + ds.getCreated());
				System.out.println("owner         " + ds.getOwner());
				System.out.println("permission    " + ds.getPermission());
				System.out.println("uri           " + ds.getUri());
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}
	*/
	
	
	public void testAnnotationValues() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			String annotationTypeValue = "/data_service/template/4921264/tag/5224002";
			List<String> annotationValues = bc.getAnnotationValueOptions(annotationTypeValue);
			for (String s : annotationValues){
				System.out.println("annotation value " + s);
			}
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}

}
