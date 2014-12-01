package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.oregonstate.eecs.iis.avatolcv.mb.Annotation;
import edu.oregonstate.eecs.iis.avatolcv.mb.CharacterState;
import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCell;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;
import edu.oregonstate.eecs.iis.avatolcv.mb.Character;

public class BogusAnnotationGenerator {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	MorphobankBundle mb = null;
	public static void main(String[] args){
		try {
			MorphobankData md = new MorphobankData("C:\\avatol\\git\\avatol_cv\\matrix_downloads");
		    md.loadMatrix("BAT");
		    MorphobankBundle bundle = md.getBundle("BAT");
		    BogusAnnotationGenerator bag = new BogusAnnotationGenerator(bundle);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	public String getBogusDetectionResultsPath(String rootDir){
		String path = rootDir + FILESEP + "detection_results" + FILESEP + "DPM" + FILESEP + "c427749c427751c427753c427754c427760" + FILESEP + "v3540";
		return path;
	}
	public void cleanDir(String dirPath){
		File bogusDir = new File(dirPath);
		bogusDir.mkdirs();
		File[] files = bogusDir.listFiles();
		int deleteCount = 0;
		for (File f : files){
			if (f.getName().endsWith(".txt")){
				f.delete();
				deleteCount += 1;
			}
		}
		System.out.println("deleted this many files:  " + deleteCount + " from " + dirPath);
	}
	public BogusAnnotationGenerator(MorphobankBundle mb) throws AvatolCVException {
		this.mb = mb;
		String bogusAnnotationDir = this.mb.getRootDir() + FILESEP + "annotationsBogus";
		String bogusDetectionResultsDir = getBogusDetectionResultsPath(this.mb.getRootDir());
		cleanDir(bogusAnnotationDir);
		cleanDir(bogusDetectionResultsDir);

		MorphobankSDDFile sdd = mb.getSDDFile();
		
		List<MatrixCell> cells = new ArrayList<MatrixCell>();
		cells.addAll(sdd.getPresenceAbsenceCellsForCharacter("c427760"));
		cells.addAll(sdd.getPresenceAbsenceCellsForCharacter("c427754"));
		cells.addAll(sdd.getPresenceAbsenceCellsForCharacter("c427753"));
		cells.addAll(sdd.getPresenceAbsenceCellsForCharacter("c427751"));
		cells.addAll(sdd.getPresenceAbsenceCellsForCharacter("c427749"));
		System.out.println("presenceAbsence char cells count = " + cells.size());
		int scoredCount = 0;
		int unscoredCount = 0;
		int mediaZeroCount = 0;
		int mediaOneCount = 0;
		int mediaTwoCount = 0;
		int mediaThreeCount = 0;
		int mediaFourOrMoreCount = 0;
		int cellCount = 0;
		for (MatrixCell cell : cells){
			cellCount += 1;
			String charId = cell.getCharId();
			String charName = sdd.getCharacterNameForId(charId);
			Character character = sdd.getCharacterForId(charId);
			String stateId = cell.getState();
			String stateName = null;
			if (cell.isScored()){
				CharacterState charState = character.getCharacterStateForId(stateId);
				stateName = charState.getName();
			}
			else {
				stateName = "present";
			}
			
			
			List<String> mediaIdsForCell = cell.getMediaIds();
			int mediaCount = mediaIdsForCell.size();
			
			if (mediaCount == 0){
				mediaZeroCount += 1;
			}
			else if (mediaCount == 1){
				mediaOneCount += 1;
			}
			else if (mediaCount == 2){
				mediaTwoCount += 1;
			}
			else if (mediaCount == 3){
				mediaThreeCount += 1;
			}
			else {
				mediaFourOrMoreCount += 1;
			}
			for (String mediaId : mediaIdsForCell){
				String filePath = "";
				
				if (cellCount %2 == 0){
					scoredCount += 1;
					filePath = bogusAnnotationDir + FILESEP + charId + "_" + mediaId + ".txt";
					
				}
				else {
					// unscored cell
					unscoredCount += 1;
					filePath = bogusDetectionResultsDir + FILESEP + charId + "_" + mediaId + ".txt";
				}
				generateAnnotationFile(filePath, charId, charName, stateId, stateName);
			}
		}
		    

		System.out.println("mediaZeroCount = " + mediaZeroCount); 
		System.out.println("mediaOneCount = " + mediaOneCount); 
		System.out.println("mediaTwoCount = " + mediaTwoCount); 
		System.out.println("mediaThreeCount = " + mediaThreeCount); 
		System.out.println("mediaFourOrMoreCount = " + mediaFourOrMoreCount);
		
		System.out.println("unscoredCount = " + unscoredCount); 
		System.out.println("scoredCount = " + scoredCount);  
	}
	public void generateAnnotationFile(String filePath, String charId, String charName, String stateId, String stateName) throws AvatolCVException {
		try {
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			//create a line that has a point coordinate, chaqrId, charName, stateId StateName
			Random rand = new Random();

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomX = rand.nextInt(100 + 1);
		    int randomY = rand.nextInt(100 + 1);
		    String delim = Annotation.ANNOTATION_FILE_DELIM;
			String line = randomX + "," + randomY + delim + charId + delim + charName + delim + stateId + delim + stateName + NL; 
			writer.write(line);
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem writing bogus annotation file " + ioe.getMessage());
		}
	}
}
