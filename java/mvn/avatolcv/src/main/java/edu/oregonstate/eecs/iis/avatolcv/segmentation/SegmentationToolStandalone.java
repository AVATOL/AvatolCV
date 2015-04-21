package edu.oregonstate.eecs.iis.avatolcv.segmentation;


public class SegmentationToolStandalone {
	private static final String FILESEP = System.getProperty("file.separator");
	SegmentationContainerStep segStep = null;
	SegmentationSessionData ssd = null;
	public static void main(String[] args) {
		String parentDataDir = "C:\\avatol\\git\\avatol_cv\\sessionData\\jedHome";
		SegmentationToolStandalone sts = new SegmentationToolStandalone(parentDataDir);
	}
	public SegmentationToolStandalone(String parentDataDir){
		this.ssd = new SegmentationSessionData(parentDataDir);
		this.ssd.setSourceImageDir(parentDataDir + FILESEP + "images" + FILESEP + "large");
		segStep = new SegmentationContainerStep(this.ssd);
		SegStep1_TrainingExamplesCheck checkStep = new SegStep1_TrainingExamplesCheck(null, this.ssd);
		SegStep2_LabelTrainingExamples labelStep = new SegStep2_LabelTrainingExamples(null, this.ssd);
		SegStep3_FilePrep dataPrepStep = new SegStep3_FilePrep(null, this.ssd);
		SegStep4_Run segRunStep = new SegStep4_Run(null, this.ssd);
		SegStep5_Review reviewStep = new SegStep5_Review(null, this.ssd);
		segStep.appendStep(checkStep);
		segStep.appendStep(labelStep);
		segStep.appendStep(dataPrepStep);
		segStep.appendStep(segRunStep);
		segStep.appendStep(reviewStep);
	}

}
