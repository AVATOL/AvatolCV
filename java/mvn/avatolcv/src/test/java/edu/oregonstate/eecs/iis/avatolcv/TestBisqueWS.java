package edu.oregonstate.eecs.iis.avatolcv;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestBisqueWS extends TestCase {
/*
	public void testAuthenticate(){
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean result = bc.authenticate("foo","bar");
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}
	}*/
	
	public void testGetDatasets() {
		BisqueWSClient bc = new BisqueWSClient();
		try {
			boolean authResult = bc.authenticate("avatol-nybg","Monocots123");
			
			List<edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Dataset> datasets = bc.getDatasets();
			for (edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Dataset ds : datasets){
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
	
}
