package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.junit.Test;

import edu.oregonstate.eecs.iis.avatolcv.mb.Matrix;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixRow;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;
import edu.oregonstate.eecs.iis.avatolcv.mb.SPRTaxonIdMapper;

public class MatrixAssessor {
	@Test
	public void testAssessMatrix() {
		try {
		    MorphobankData md = new MorphobankData("C:\\avatol\\git\\avatol_cv\\matrix_downloads");
		    //md.loadMatrix("BOGUS");
		    //md.loadMatrix("BAT");
		    //MorphobankBundle mb = md.getBundle("BAT");
		    md.loadMatrix("NEMATOCYST");
		    MorphobankBundle mb = md.getBundle("NEMATOCYST");
		    MorphobankSDDFile sdd = mb.getSDDFile();
		    Matrix matrix = sdd.getMatrix();
		    List<String> scoredCharacters = matrix.getScoredCharacterIds();
		    System.out.println("=============================================");		
		    
		    SPRTaxonIdMapper mapper = sdd.getTaxonIdMapper();
		    if (mapper == null){
		    	List<Character> scoredPACharacters = new ArrayList<Character>();
			    List<Character> paCharacters = sdd.getPresenceAbsenceCharacters();
			    for (Character ch : paCharacters){
			    	String curCharId = ch.getId();
			    	if (scoredCharacters.contains(curCharId)){
			    		scoredPACharacters.add(ch);
			    	}
			    }
			    System.out.println("Scored Presence/AbsenceCharacter count : " + scoredPACharacters.size());
			    for (Character ch : scoredPACharacters){
			    	String charName = ch.getName();
			    	if (charName.indexOf("between") == -1){
			    		List<MatrixCell> cells = sdd.getPresenceAbsenceCellsForCharacter(ch.getId());
				    	List<MatrixCell> scoredCells = new ArrayList<MatrixCell>();
				    	List<MatrixCell> unScoredCells = new ArrayList<MatrixCell>();
				    	for (MatrixCell cell : cells){
				    		if (cell.isScored()){
				    			scoredCells.add(cell);
				    		}
				    		else {
				    			unScoredCells.add(cell);
				    		}
				    	}
				    	System.out.println("Character : " + charName + "\t\tscored: " + scoredCells.size() + "\t\tunscored: " + unScoredCells.size());
			    	}
			    }
		    }
		    else {
		    	// specimen per row matrix
		    	List<String> trueTaxonNames = new ArrayList<String>();
		    	Hashtable<String,List<String>> taxonMap = new Hashtable<String,List<String>>();
		    	List<MatrixRow> rows = matrix.getRows();
			    for (MatrixRow row : rows){
			    	String taxonId = row.getTaxonId();
			    	String taxonName = row.getTaxonName();
			    	String trueName = mapper.getPureTaxonNameForName(taxonName);
			    	if (!trueTaxonNames.contains(trueName)){
			    		trueTaxonNames.add(trueName);
			    	}
			    	List<String> givenTaxonIds = taxonMap.get(trueName);
			    	if (givenTaxonIds == null){
			    		givenTaxonIds = new ArrayList<String>();
			    		taxonMap.put(trueName, givenTaxonIds);
			    	}
			    	givenTaxonIds.add(taxonId);
			    }

			    List<Character> scoredPACharacters = new ArrayList<Character>();
			    List<Character> paCharacters = sdd.getPresenceAbsenceCharacters();
			    for (Character ch : paCharacters){
			    	String curCharId = ch.getId();
			    	if (scoredCharacters.contains(curCharId)){
			    		scoredPACharacters.add(ch);
			    	}
			    }
			    System.out.println("Scored Presence/AbsenceCharacter count : " + scoredPACharacters.size());
			    int taxonCount = 0;
			    for (String trueName : trueTaxonNames){
			    	taxonCount++;
			    	System.out.println("\n\nTAXON " + taxonCount + " of " + trueTaxonNames.size() + " : " + trueName + ":");
			    	for (Character ch : scoredPACharacters){
				    	String charName = ch.getName();
				    	if (charName.indexOf("between") == -1){
				    		List<MatrixCell> cells = sdd.getPresenceAbsenceCellsForCharacter(ch.getId());
				    		List<MatrixCell> scoredCells = new ArrayList<MatrixCell>();
				    		List<MatrixCell> unScoredCells = new ArrayList<MatrixCell>();
				    		List<String> givenTaxonIds = taxonMap.get(trueName);
				    		for (String tId : givenTaxonIds){
						    	for (MatrixCell cell : cells){
						    		if (cell.getTaxonId().equals(tId)){
						    			if (cell.isScored()){
							    			scoredCells.add(cell);
							    		}
							    		else {
							    			unScoredCells.add(cell);
							    		}
						    		}
						    	}
						    }
				    		System.out.println("Character : " + charName +  "\t\tscored: " + scoredCells.size() + "\t\tunscored: " + unScoredCells.size());
				    	}
			    	}
			    }
		    }
		}
		catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
}
