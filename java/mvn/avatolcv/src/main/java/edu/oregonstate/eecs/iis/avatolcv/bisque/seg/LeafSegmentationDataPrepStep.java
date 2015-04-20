package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.oregonstate.eecs.iis.avatolcv.bisque.BisqueSessionData;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueSessionException;

public class LeafSegmentationDataPrepStep implements Step {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
	private BisqueSessionData sessionData = null;
	private View view = null;
	private boolean filesHaveBeenGenerated = false;
	public LeafSegmentationDataPrepStep(View view, BisqueSessionData sessionData){
		this.sessionData = sessionData;
		this.view = view;
	}
	/*
	 * 		___DarwinDriverFileGenerator
			___SegRunConfigGenerator
			___SegInputFileGenerator

	 */
	public void createDarwinDriverFile() throws BisqueSessionException {
		SegmentationSessionData ssd = this.sessionData.getSegmentationSessionData();
		String segmentationRootDir = ssd.getSegmentationRootDir();
		DarwinDriverFile ddf = new DarwinDriverFile();
		ddf.setImageDir(this.sessionData.getImagesDir());
		ddf.setCachedDir(ssd.getSegmentationCacheDir());
		ddf.setLabelDir(ssd.getSegmentationLabelDir());
		ddf.setModelsDir(ssd.getSegmentationModelsDir());
		ddf.setOutputDir(ssd.getSegmentationOutputDir());
		ddf.setSegmentationDir(ssd.getSegmentationDir());
		String xml = ddf.getXMLContentString();
		String darwinDriverFilePath = segmentationRootDir + FILESEP + "darwinDriver.xml";
		File f = new File(darwinDriverFilePath);
		if (f.exists()){
			f.delete();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(darwinDriverFilePath));
			writer.write(xml + NL);
			writer.close();
		}
		catch(IOException ioe){
			throw new BisqueSessionException("could not create darwin driver xml file");
		}
		
	}
	
	public void createSegmentationInputFiles(){
		/*
trainingImages_segmentation.txt and testingImages_segmentation.txt have entries that are the root names of images
    00-5xayvrdPC3o5foKMpLbZ5H_imgXyz
    03-uietIOuerto5foKMhUHYUh_imgAbc
		 */
	}
	@Override
	public void consumeProvidedData() throws BisqueSessionException {
		createDarwinDriverFile();
		createSegmentationInputFiles();
		filesHaveBeenGenerated = true;
	}

	@Override
	public boolean needsAnswering() {
		if (filesHaveBeenGenerated){
			return false;
		}
		return true;
	}

	@Override
	public View getView() {
		return view;
	}

}
