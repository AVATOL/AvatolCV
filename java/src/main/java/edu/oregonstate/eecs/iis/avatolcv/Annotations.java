package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Annotations {
	private String bundleDir = null;
	private Media media = null;
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private Hashtable<String, List<Annotation>> annotationsForCharacterHash = new Hashtable<String, List<Annotation>>();
	private List<String> charactersTrained = new ArrayList<String>();
	private Hashtable<String, String> inputFilepathForCharacterName = new Hashtable<String,String>();
	private Hashtable<String, String> outputFilepathForCharacterName = new Hashtable<String,String>();
	
	public Annotations(List<MatrixCell> matrixCellsOfInterest, String bundleDir, MorphobankSDDFile sddFile, Media media) throws MorphobankDataException {
		this.bundleDir = bundleDir;
		this.media = media;
        for (MatrixCell matrixCell : matrixCellsOfInterest){
        	List<String> mediaIds = matrixCell.getMediaIds();
        	for (String mediaId : mediaIds){
        		String charId = matrixCell.getCharId();
        		String annotationPath = getAnnotationFilePathname(charId, mediaId);
        		File f = new File(annotationPath);
        		if (f.exists()){
        			
        			if (!charactersTrained.contains(charId)){
        				charactersTrained.add(charId);
        			}
        			List<Annotation> annotationsForCharacter = annotationsForCharacterHash.get(charId);
            		if (null == annotationsForCharacter){
            			annotationsForCharacter = new ArrayList<Annotation>();
            			annotationsForCharacterHash.put(charId,annotationsForCharacter);
            		}
            		List<Annotation> annotations = loadAnnotations(annotationPath, mediaId);
            		for (Annotation annotation : annotations){
            			annotationsForCharacter.add(annotation);
            		}
        		}
        	}
        }
        generateInputDataFiles(sddFile);
	}
	public boolean doesAnnotationInputFileExistForCharacterName(String name){
		String pathname = inputFilepathForCharacterName.get(name);
		if (null == pathname){
			return false;
		}
		return true;
	}
	
	public boolean isMatrixCellRepresentedByAnyAnnotation(MatrixCell cell, List<Annotation> annotations){
		String charIdFromCell = cell.getCharId();
		List<String> mediaIds = cell.getMediaIds();
		for (String mediaId : mediaIds){
			for (Annotation annotation : annotations){
				String annotationCharId = annotation.getCharId();
				String annotationMediaId = annotation.getMediaId();
				if (annotationCharId.equals(charIdFromCell) && annotationMediaId.equals(mediaId)){
					return true;
				}
			}
		}
		return false;
	}
	public List<MatrixCell> getCellsToScore(List<MatrixCell> allMatrixCellsForTrainedCharacter, List<Annotation> annotationsForCharacter){
		List<MatrixCell> cellsToScore = new ArrayList<MatrixCell>();
		for (MatrixCell cell : allMatrixCellsForTrainedCharacter){
			if (isMatrixCellRepresentedByAnyAnnotation(cell, annotationsForCharacter)){
				// it's a training example	
			}
			else {
				// it's for scoring
				cellsToScore.add(cell);
			}
		}
		return cellsToScore;
	}
	public String getInputFilepathForCharacter(String charName) throws MorphobankDataException {
		String path = inputFilepathForCharacterName.get(charName);
		if (null == path){
			throw new MorphobankDataException("no inputFile pathname for character name: " + charName);
		}
		return path;
	}
	public String getOutputFilepathForCharacter(String charName) throws MorphobankDataException {
		String path = outputFilepathForCharacterName.get(charName);
		if (null == path){
			throw new MorphobankDataException("no outputFile pathname for character name: " + charName);
		}
		return path;
	}
	  // sorted_input_data_<charID>_charName.txt
    // for each annotation file, add line
    // training_data:media/<name_of_mediafile>:char_state:<pathname_of_annotation_file>:taxonID:lineNumber
    // for each media file which needs scoring, add line
    // image_to_score:media/<name_of_mediafile>:taxonID 
    public void generateInputDataFiles(MorphobankSDDFile sddFile) throws MorphobankDataException {
    	for (String charId : this.charactersTrained){
    		List<String> trainingDataLines = new ArrayList<String>();
    		List<String> scoringDataLines = new ArrayList<String>();
    		List<Annotation> annotationsForCharacter = this.annotationsForCharacterHash.get(charId);
    		for (Annotation annotation : annotationsForCharacter){
    			String mediaFilename = this.media.getMediaFilenameForMediaId(annotation.getMediaId());
    			if (null != mediaFilename){
    				String taxonId = sddFile.getTaxonIdForMediaId(annotation.getMediaId());
        			String trainingDataLine = annotation.getTrainingDataLine(mediaFilename, taxonId);
        			trainingDataLines.add(trainingDataLine);
    			}
    		}
    		List<MatrixCell> allMatrixCellsForTrainedCharacter = sddFile.getPresenceAbsenceCellsForCharacter(charId);
    		List<MatrixCell> matrixCellsToScore = getCellsToScore(allMatrixCellsForTrainedCharacter, annotationsForCharacter);
    		for (MatrixCell cell : matrixCellsToScore){
    			List<String> mediaIds = cell.getMediaIds();
    			for (String mediaId : mediaIds){
    				String mediaFilename = this.media.getMediaFilenameForMediaId(mediaId);
    				if (null != mediaFilename){
    					String taxonId = sddFile.getTaxonIdForMediaId(mediaId);
            			String scoringDataLine = "image_to_score|media/" + mediaFilename + "|" + taxonId;
            			scoringDataLines.add(scoringDataLine);
    				}
    				
    			}
    		}
    		if (trainingDataLines.size() > 0){
    			String charName = sddFile.getCharacterNameForId(charId);
                String inputFilename = "sorted_input_data_" + charId + "_" + charName + ".txt";
                String inputFilePathname = this.bundleDir + FILESEP + MorphobankBundle.INPUT_DIRNAME + FILESEP + inputFilename;
                String outputFilename = "sorted_output_data_" + charId + "_" + charName + ".txt";
                String outputFilePathname = this.bundleDir + FILESEP + MorphobankBundle.INPUT_DIRNAME + FILESEP + outputFilename;
                this.inputFilepathForCharacterName.put(charName, inputFilePathname);
                this.outputFilepathForCharacterName.put(charName, outputFilePathname);
                File f = new File(inputFilePathname);
                if (f.exists()){
                    f.delete();
                }
                try {
                	BufferedWriter writer = new BufferedWriter(new FileWriter(inputFilePathname));
                    
                    for (String trainingLine : trainingDataLines){
                    	writer.write(trainingLine + NL);
                    }
                    for (String scoringLine : scoringDataLines){
                    	writer.write(scoringLine + NL);
                    }
                    writer.close();
                }
                catch(IOException ioe){
                	ioe.printStackTrace();
                	throw new MorphobankDataException("could not write input file " + inputFilePathname);
                }
    		}
    	}
    }
	public List<Annotation> loadAnnotations(String path, String mediaId) throws MorphobankDataException {
		List<Annotation> annotations = new ArrayList<Annotation>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			int lineNumber = 1;
			while ((line = reader.readLine()) != null){
				if (!"".equals(line)){
					Annotation annotation = new Annotation(line, lineNumber, mediaId, path);
					annotations.add(annotation);
					lineNumber += 1;
				}
			}
			reader.close();
			return annotations;
		}
		catch(IOException ioe){
			ioe.printStackTrace();
			throw new MorphobankDataException("problem loading annotation info " + ioe.getMessage());
		}
		
	}
	public String getAnnotationFilePathname(String charId, String mediaId){
        return this.bundleDir + FILESEP + "annotations" + FILESEP + charId + "_" + mediaId + ".txt";
	}
}