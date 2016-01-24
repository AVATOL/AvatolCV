package edu.oregonstate.eecs.iis.obsolete.avatolcv.segmentation.files;

public class DarwinDriverFile {
	private static final String NL = System.getProperty("line.separator");
    private String imgDir = null;
    private String lblDir = null;
    private String segDir = null;
    private String cachedDir = null;
    private String modelsDir = null;
    private String outputDir = null;
    public void setImageDir(String s){
    	this.imgDir = s;
    }
    public void setLabelDir(String s){
    	this.lblDir = s;
    }
    public void setSegmentationDir(String s){
    	this.segDir = s;
    }
    public void setCachedDir(String s){
    	this.cachedDir = s;
    }
    public void setModelsDir(String s){
    	this.modelsDir = s;
    }
    public void setOutputDir(String s){
    	this.outputDir = s;
    }
	public String getXMLContentString(){
		String s = 
			"<drwn>" + NL +
		   "<drwnMultiSegConfig>" + NL +
		   "<!-- data options -->" + NL +
		   "<option name=\"baseDir\" value=\"./\" />" + NL +
		   "<option name=\"imgDir\" value=\"" + imgDir + "\" />" + NL + //   <-----------
		   "<option name=\"lblDir\" value=\"" + lblDir + "\" />" + NL + // <-----------
		   "<option name=\"segDir\" value=\"" + segDir + "\" />" + NL + // <-----------
		   "<option name=\"cacheDir\" value=\"" + cachedDir + "\" />" + NL + // <-----------
		   "<option name=\"modelsDir\" value=\"" + modelsDir + "\" />" + NL + // <-----------
		   "<option name=\"outputDir\" value=\"" + outputDir + "\" />" + NL + // <-----------
		   "<option name=\"imgExt\" value=\".jpg\" />" + NL +
		   "<option name=\"lblExt\" value=\".txt\" />" + NL +
		   "<option name=\"segExt\" value=\".sp\" />" + NL +
		   "<option name=\"useCache\" value=\"true\" />" + NL +
		   "<!-- region definitions -->" + NL +
		   "<regionDefinitions>  leaf, background, tag, other" + NL +
		      "<region id=\"-1\" name=\"other\" color=\"0 0 0\"/>" + NL +
		      "<region id=\"0\" name=\"leaf\" color=\"0 128 0\"/>" + NL +
		      "<region id=\"1\" name=\"background\" color=\"0 0 128\"/>" + NL +
		      "<region id=\"2\" name=\"tag\" color=\"128 0 0\"/>" + NL +
		   "</regionDefinitions>" + NL +
		  "</drwnMultiSegConfig>" + NL +
	      "<drwnSegImagePixelFeatures>" + NL +
	      "<!-- feature options -->" + NL +
	      "<option name=\"filterBandwidth\" value=\"1\" />" + NL +
	      "<option name=\"featureGridSpacing\" value=\"5\" />" + NL +
	      "<option name=\"includeRGB\" value=\"true\" />" + NL +
	      "<option name=\"includeHOG\" value=\"true\" />" + NL +
	      "<option name=\"includeLBP\" value=\"true\" />" + NL +
	      "<option name=\"includeRowCol\" value=\"true\" />" + NL +
	      "<option name=\"includeLocation\" value=\"true\" />" + NL +
	      "</drwnSegImagePixelFeatures>" + NL +
	      "<drwnCodeProfiler enabled=\"true\" />" + NL +
	      "<drwnLogger logLevel=\"VERBOSE\" logFile=\"msrc.log\" />" + NL +
		  " <drwnThreadPool threads=\"4\" />" + NL +
		  "<drwnConfusionMatrix colSep=\" || \" rowBegin=\"    || \" rowEnd=\" \\\" />" + NL +   
	      "<drwnHOGFeatures blockSize=\"1\" normClippingLB=\"0.1\" normClippingUB=\"0.5\" />" + NL +
	   "</drwn>";
		return s;
	}
}
