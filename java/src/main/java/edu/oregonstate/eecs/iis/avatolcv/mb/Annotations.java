package edu.oregonstate.eecs.iis.avatolcv.mb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Annotations {
	public static final String ANNOTATIONS_DIR = "annotations";
	private String bundleDir = null;
	private Media media = null;
	//private String annotationsForTrainingDir;
	private static final String FILESEP = System.getProperty("file.separator");
	private Hashtable<String, List<Annotation>> annotationsForCharacterHash = new Hashtable<String, List<Annotation>>();
	private List<String> charactersTrained = new ArrayList<String>();
	
	
	public Annotations(List<MatrixCell> matrixCellsOfInterest, String bundleDir, MorphobankSDDFile sddFile, Media media) throws MorphobankDataException {
		//this.annotationsForTrainingDir = annotationsForTrainingDir;
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
	}
	public List<String> getMediaAnnotated(){
		ArrayList<String> result = new ArrayList<String>();
		for (String character : this.charactersTrained){
			List<Annotation> annotations = this.annotationsForCharacterHash.get(character);
			for (Annotation annotation: annotations){
				String mediaId = annotation.getMediaId();
				if (!result.contains(mediaId)){
					result.add(mediaId);
				}
			}
		}
		return result;
	}
	public List<String> getCharactersTrained(){
		ArrayList<String> result = new ArrayList<String>();
		result.addAll(this.charactersTrained);
		return result;
	}
	public List<Annotation> getAnnotationsForCharacter(String charId){
		return this.annotationsForCharacterHash.get(charId);
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
	


	public static List<Annotation> loadAnnotations(String path, String mediaId) throws MorphobankDataException {
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
        return this.bundleDir + FILESEP + ANNOTATIONS_DIR + FILESEP + mediaId + "_" + charId + ".txt";
	}
}