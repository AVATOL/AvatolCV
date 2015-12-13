package edu.oregonstate.eecs.iis.avatolcv;

import java.io.File;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotationPoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharState;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestMorphobankWS extends TestCase {
/*
	public void testAuthenticate() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	
	public void testAuthenticateBad() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "foo";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			boolean result = wsClient.authenticate(username, pw);
			Assert.assertEquals(result, false);
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	
	
	 
	public void testGetMorphobankMatricesForUser() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBMatrix> matrices = wsClient.getMorphobankMatricesForUser();
			for (MBMatrix m : matrices){
    			System.out.println("adding matrix " + m.getName());
    		}
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	
	public void testGetMorphobankMatricesForUserBad() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "foo";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBMatrix> matrices = wsClient.getMorphobankMatricesForUser();
			for (MBMatrix m : matrices){
    			System.out.println("adding matrix " + m.getName());
    		}
		}
		catch(MorphobankWSException me){
			Assert.assertEquals(me.getMessage(),"Error listing matrices for user : invalid user");
		}
	}
	*/
	/*
	public void testGetCharactersForMatrix() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBCharacter> characters = wsClient.getCharactersForMatrix("1423");
			for (MBCharacter character : characters){
        		System.out.println("charId   : " + character.getCharID());
        		System.out.println("charName : " + character.getCharName());
        		List<MBCharState> charStates = character.getCharStates();
        		for (MBCharState state : charStates){
        			System.out.println("stateId " + state.getCharStateID() + " stateName " + state.getCharStateName() + " stateNum " + state.getCharStateNum());
        		}
        	}
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	
	public void testGetCharactersForMatrixBad() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBCharacter> characters = wsClient.getCharactersForMatrix("-1");
			
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			Assert.assertEquals(me.getMessage(),"Error getting characters for matrix : invalid matrixID");
		}
	}
	
	public void testGetTaxaForMatrix() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBTaxon> taxa = wsClient.getTaxaForMatrix("1423");
			for (MBTaxon taxon : taxa){
        		System.out.println("taxonId   : " + taxon.getTaxonID());
        		System.out.println("taxonName : " + taxon.getTaxonName());
        	}
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	

    public void testGetTaxaForMatrixBad() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBTaxon> taxa = wsClient.getTaxaForMatrix("-1");
			
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			Assert.assertEquals(me.getMessage(),"Error getting taxa for matrix : invalid matrixID");
		}
	}
    
	public void testGetCharStatesForCell() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBCharStateValue> csvs = wsClient.getCharStatesForCell("1423", "519541", "72002");
			for (MBCharStateValue csv : csvs){
        		System.out.println("charStateVal   : " + csv.getCharStateID());
        	}
        	
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}

	public void testGetMediaForCell() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBMediaInfo> media = wsClient.getMediaForCell("1423", "519541", "72002");
			for (MBMediaInfo mi : media){
        		System.out.println("mediaId " + mi.getMediaID() + " viewId " + mi.getViewID() );
        	}
        	
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}

	public void testGetAnnotationsForCellMedia() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBAnnotation> annotations = wsClient.getAnnotationsForCellMedia("1423", "519541", "72002","284045");
			for (MBAnnotation mba : annotations){
        		System.out.println("type " + mba.getType());
        		List<MBAnnotationPoint> points = mba.getPoints();
        		for (MBAnnotationPoint p : points){
        			System.out.println("x " + p.getX() + " y " + p.getY());
        		}
        	}
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}

	public void testGetViewsForProject() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBView> views = wsClient.getViewsForProject("139");
			for (MBView view : views){
        		System.out.println("viewID " + view.getViewID() + " ViewName " + view.getName());
        	}
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}

	public void testGetViewsForProjectBad() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBView> views = wsClient.getViewsForProject("-1");
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			Assert.assertEquals(me.getMessage(),"Error getting views for project : user does not have access to matrix");
		}
	}
	*/
	public void deleteIfExists(String path){
		File f = new File(path);
		if (f.exists()){
			f.delete();
		}
	}
	/*
	public void testDownload() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			String thumbnailDir = "C:\\\\avatol\\temp";
			File f = new File(thumbnailDir);
			if (!f.exists()){
				f.mkdirs();
			}
			String mediaID = "284045";
			deleteIfExists("C:\\\\avatol\\temp\\" + mediaID + "_thumbnail.jpg");
			deleteIfExists("C:\\\\avatol\\temp\\" + mediaID + "_small.jpg");
			deleteIfExists("C:\\\\avatol\\temp\\" + mediaID + "_large.jpg");
			boolean result1 = wsClient.downloadImageForMediaId(thumbnailDir,mediaID,"","thumbnail");
			boolean result2 = wsClient.downloadImageForMediaId(thumbnailDir,mediaID,"","small");
			boolean result3 = wsClient.downloadImageForMediaId(thumbnailDir,mediaID,"","large");
			
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}
	*/
	
	public void testDownloadBadMediaId() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClientImpl();
		try {
			wsClient.authenticate(username, pw);
			String thumbnailDir = "C:\\\\avatol\\temp";
			File f = new File(thumbnailDir);
			if (!f.exists()){
				f.mkdirs();
			}
			String mediaID = "-1";
			deleteIfExists("C:\\\\avatol\\temp\\" + mediaID + "_thumbnail.jpg");
			boolean result1 = wsClient.downloadImageForMediaId(thumbnailDir,mediaID,"","thumbnail","80");
			
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			Assert.assertEquals(me.getMessage(),"Error getting image for mediaID : invalid media id");
		}
	}
	
}
