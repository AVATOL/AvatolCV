package edu.oregonstate.eecs.iis.avatolcv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.mb.MatrixCellImageUnit;

public class PartitionRegister {
	public static final String PARTITION_REGISTER_FILENAME = "partitionRegister.txt";
	public static final String FILESEP = System.getProperty("file.separator");
	public static final String NL = System.getProperty("line.separator");
	private List<String> trainingMedia = new ArrayList<String>();
	private List<String> toScoreMedia = new ArrayList<String>();
	private List<String> unitInfos = new ArrayList<String>();
	private String persistDirectory = null;
	public PartitionRegister(){
		
	}
	public boolean isTrainingImage(String mediaId){
		for (String id : this.trainingMedia){
			if (id.equals(mediaId)){
				return true;
			}
		}
		return false;
	}
	public void setPersistDirectory(String dir){
		this.persistDirectory = dir;
	}
	public boolean isLegalTrainingUnit(MatrixCellImageUnit unit){
		String mediaId = unit.getMediaId();
		if (this.toScoreMedia.contains(mediaId)){
			return false;
		}
		return true;
	}
	public boolean isLegalToScoreUnit(MatrixCellImageUnit unit){
		String mediaId = unit.getMediaId();
		if (this.trainingMedia.contains(mediaId)){
			return false;
		}
		return true;
	}
	public void addComment(String s){
		unitInfos.add("# " + s);
	}
	public void registerToScoreUnit(MatrixCellImageUnit unit){
		String charId = unit.getCharId();
		String mediaId = unit.getMediaId();
		String stateId = unit.getStateId();
		String taxonId = unit.getTaxonId();
		String viewId = unit.getViewId();
		String key = getKey(charId,taxonId,viewId,mediaId);
		String status = "";
		if (!stateId.equals("?")){
			status = "heldoutToScore," + stateId;
		}
		else {
			status = "trueUnscoredCellUnit,?";
		}
		String info = key + "=" + status;
		if (!this.toScoreMedia.contains(mediaId)){
			this.toScoreMedia.add(mediaId);
		}
		unitInfos.add(info);
	}
	
	public String getKey(String charId, String taxonId, String viewId, String mediaId){
		return charId + "_" + taxonId + "_" + viewId + "_" + mediaId;
	}
	public void registerTrainingUnit(MatrixCellImageUnit unit) throws AvatolCVException {
		String charId = unit.getCharId();
		String mediaId = unit.getMediaId();
		String stateId = unit.getStateId();
		String taxonId = unit.getTaxonId();
		String viewId = unit.getViewId();
		String key = getKey(charId,taxonId,viewId,mediaId);
		if (stateId.equals("?")){
			throw new AvatolCVException("found evidence of trainingExample that lacks score");
		}
		String status = "trainingExample," + stateId;
		String info = key + "=" + status;
		if (!this.trainingMedia.contains(mediaId)){
		    this.trainingMedia.add(mediaId);
		}
		unitInfos.add(info);
	}
	public void persist() throws AvatolCVException {
		String persistPath = this.persistDirectory + FILESEP + PARTITION_REGISTER_FILENAME;
		File f = new File(persistPath);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(persistPath));
			for (String unitInfo : this.unitInfos){
				writer.write(unitInfo + NL);
			}
			writer.close();
		}
		catch(IOException ioe){
			throw new AvatolCVException("problem persisting partitionRegister " + persistPath);
		}
	}
	
	public void load(){
		
	}
}
