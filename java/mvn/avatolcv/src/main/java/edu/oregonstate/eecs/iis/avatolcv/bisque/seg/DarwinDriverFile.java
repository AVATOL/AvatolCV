package edu.oregonstate.eecs.iis.avatolcv.bisque.seg;

public class DarwinDriverFile {
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
			"<drwn>" +
		   "<drwnMultiSegConfig>" +
		   "<!-- data options -->" +
		   "<option name=\"baseDir\" value=\"./\" />" +
		   "<option name=\"imgDir\" value=\"" + imgDir + "\" />" + //   <-----------
		   "<option name=\"lblDir\" value=\"" + lblDir + "\" />" + // <-----------
		   "<option name=\"segDir\" value=\"" + segDir + "\" />" + // <-----------
		   "<option name=\"cacheDir\" value=\"" + cachedDir + "\" />" + // <-----------
		   "<option name=\"modelsDir\" value=\"" + modelsDir + "\" />" + // <-----------
		   "<option name=\"outputDir\" value=\"" + outputDir + "\" />" + // <-----------
		   "<option name=\"imgExt\" value=\".jpg\" />" +
		   "<option name=\"lblExt\" value=\".txt\" />" +
		   "<option name=\"segExt\" value=\".sp\" />" +
		   "<option name=\"useCache\" value=\"true\" />" +
		   "<!-- region definitions -->" +
		   "<regionDefinitions>  leaf, background, tag, other" +
		      "<region id=\"-1\" name=\"other\" color=\"0 0 0\"/>" +
		      "<region id=\"0\" name=\"leaf\" color=\"0 128 0\"/>" +
		      "<region id=\"1\" name=\"background\" color=\"0 0 128\"/>" +
		      "<region id=\"2\" name=\"tag\" color=\"128 0 0\"/>" +
		   "</regionDefinitions>" +
		  "</drwnMultiSegConfig>" +
	      "<drwnSegImagePixelFeatures>" +
	      "<!-- feature options -->" +
	      "<option name=\"filterBandwidth\" value=\"1\" />" +
	      "<option name=\"featureGridSpacing\" value=\"5\" />" +
	      "<option name=\"includeRGB\" value=\"true\" />" +
	      "<option name=\"includeHOG\" value=\"true\" />" +
	      "<option name=\"includeLBP\" value=\"true\" />" +
	      "<option name=\"includeRowCol\" value=\"true\" />" +
	      "<option name=\"includeLocation\" value=\"true\" />" +
	      "</drwnSegImagePixelFeatures>" +
	      "<drwnCodeProfiler enabled=\"true\" />" +
	      "<drwnLogger logLevel=\"VERBOSE\" logFile=\"msrc.log\" />" +
		  " <drwnThreadPool threads=\"4\" />" +
		  "<drwnConfusionMatrix colSep=\" || \" rowBegin=\"    || \" rowEnd=\" \\\" />" +   
	      "<drwnHOGFeatures blockSize=\"1\" normClippingLB=\"0.1\" normClippingUB=\"0.5\" />" +
	   "</drwn>";
		return s;
	}
}
