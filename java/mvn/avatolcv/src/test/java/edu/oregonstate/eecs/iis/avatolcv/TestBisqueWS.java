package edu.oregonstate.eecs.iis.avatolcv;

import java.util.List;

import edu.oregonstate.eecs.iis.avatol.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatol.ws.bisque.BisqueImage;
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
	
	public void testGetDatasets() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			bc.logout();
			
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
}
