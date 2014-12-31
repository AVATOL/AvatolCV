package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ScoredSetMetadata {
	public static final String MATRIX_KEY = "matrix";
	public static final String CHARACTER_KEY = "character";
	public static final String VIEW_KEY = "view";
	public static final String ALGORITHM_KEY = "algorithm";
	public static final String INPUT_FOLDER_KEY = "input_folder";
	public static final String OUTPUT_FOLDER_KEY = "output_folder";
	public static final String DETECTION_RESULTS_FOLDER_KEY = "detection_results_folder";
	public static final String FOCUS_CHARID_KEY = "focus_character_id";
	public static final String CHARACTER_TRAINED = "character_trained";
	public static final String SYSTEM_PROPERTY_PREFIX = "SYSTEM_PROPERTY:";
	public static final String SPLIT_KEY = "training_data_split_threshold";
	public static final String MATRIX_ROW_TYPE_KEY = "matrix_row_type";
	public static final String MATRIX_ROW_TYPE_SPECIMEN = "specimen";
	public static final String MATRIX_ROW_TYPE_TAXON = "taxon";

	private String matrix = null;
    private String character = null;
	private String view = null;
    private String alg = null;
    private String inputFolder = null;
    private String outputFolder = null;
    private String detectionResultsFolder = null;
    private String focusCharId = null;
    private List<String> charactersTrainedOn = new ArrayList<String>();
    private String split = null;
    private String matrixRowType = null;
    private String identifier = null;
    public ScoredSetMetadata(String identifier, String info){
    	this.identifier = identifier;
    	try {
    		BufferedReader reader = new BufferedReader(new StringReader(info));
        	String line = null;
        	while (null != (line = reader.readLine())){
        		String[] parts = line.split("=");
        		if (line.startsWith(MATRIX_KEY)){
        			this.matrix = parts[1];
        		} 
        		else if (line.startsWith(CHARACTER_TRAINED)){
        			this.charactersTrainedOn.add(parts[1]);
        		}
        		else if (line.startsWith(CHARACTER_KEY)){
        			this.character = parts[1];
        			System.out.println("Setting character to " + parts[1]);
        		}
        		else if (line.startsWith(VIEW_KEY)){
        			this.view = parts[1];
        		}
        		else if (line.startsWith(ALGORITHM_KEY)){
        			this.alg = parts[1];
        		}
        		else if (line.startsWith(INPUT_FOLDER_KEY)){
        			this.inputFolder = parts[1];
        		} 
        		else if (line.startsWith(OUTPUT_FOLDER_KEY)){
        			this.outputFolder = parts[1];
        		}
        		else if (line.startsWith(DETECTION_RESULTS_FOLDER_KEY)){
        			this.detectionResultsFolder = parts[1];
        		}
        		else if (line.startsWith(FOCUS_CHARID_KEY)){
        			this.focusCharId = parts[1];
        		}
        		else if (line.startsWith(SYSTEM_PROPERTY_PREFIX)){
        			line = line.replaceFirst(SYSTEM_PROPERTY_PREFIX, "");
        			parts = line.split("=");
        			if (line.startsWith(this.SPLIT_KEY)){
        				this.split = parts[1];
        			}
        			else if (line.startsWith(this.MATRIX_ROW_TYPE_KEY)){
        				this.matrixRowType = parts[1];
        			}
        			else {
        				System.out.println("WARNING - unrecognized metadata line : " + SYSTEM_PROPERTY_PREFIX + line);
        			}
        		}
        		else if (line.startsWith("#")){
        			// ignore comments
        		}
        		else {
        			System.out.println("WARNING - unrecognized metadata line : " + line);
        		}
        	}
        	
    	}
    	catch(IOException ioe){
    		ioe.printStackTrace();
    		System.out.println(ioe.getMessage());
    	}
    }

    public String getMatrix(){
    	return this.matrix;
    }
    public String getCharacter(){
    	System.out.println("returning character " + this.character);
    	return this.character;
    }
    public String getView(){
    	return this.view;
    }
    public String getAlgorithm(){
    	return this.alg;
    }
    public String getInputFolder(){
    	return this.inputFolder;
    }
    public String getOutputFolder(){
    	return this.outputFolder;
    }
    public String getDetectionResultsFolder(){
    	return this.detectionResultsFolder;
    }
    public String getFocusCharId(){
    	return this.focusCharId;
    }
    public List<String> getCharactersTrained(){
    	return this.charactersTrainedOn;
    }
    public String getSplit(){
    	return this.split;
    }
    public String getMatrixRowType(){
    	return this.matrixRowType;
    }
}
