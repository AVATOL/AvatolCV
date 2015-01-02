package edu.oregonstate.eecs.iis.avatolcv.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankData;
import edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankSDDFile;

public class ImagePruner {
	private static final String FILESEP = System.getProperty("file.separator");
	public static void main(String[] args) {
		try {
		    MorphobankData md = new MorphobankData("C:\\avatol\\git\\avatol_cv\\matrix_downloads");
		    //md.loadMatrix("BOGUS");
		    md.loadMatrix("BAT2");
		    MorphobankBundle bundle = md.getBundle("BAT2");
		    List<String> names = bundle.getScorableCharacterNames();
		    ArrayList<String> charIds = new ArrayList<String>();
		    charIds.add("c427749");
		    charIds.add("c427753");
		    charIds.add("c427754");
		    charIds.add("c427760");
		    charIds.add("c427751");

		    ArrayList<String> viewIds = new ArrayList<String>();
		    viewIds.add("v3540");
		    viewIds.add("v3539");
		    ImagePruner ip = new ImagePruner(bundle, charIds, viewIds);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}

	}
	public ImagePruner(MorphobankBundle bundle, List<String> charIds, List<String> viewIds){
		MorphobankSDDFile sddFile = bundle.getSDDFile();
		String mediaFilePath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\mediaScaled";
		File mediaDir = new File(mediaFilePath);
		File[] mediaFiles = mediaDir.listFiles();
		
		String annotationsPath = "C:\\avatol\\git\\avatol_cv\\matrix_downloads\\BAT2\\annotations";
		File annotationsDir = new File(annotationsPath);
		File[] annotationFiles = annotationsDir.listFiles();
		List<String> annotationRoots = new ArrayList<String>();
		for (File f : annotationFiles){
			String filename = f.getName();
			String[] parts = filename.split("\\.");
			String root = parts[0];
			annotationRoots.add(root);
		}
		for (File f : mediaFiles){
			boolean keepFile = false;
			boolean isMediaFileAssociatedWithChar = false;
			boolean mediaFileIsOfGoodView = false;
			String filename = f.getName();
			String mediaIdCapM = getMediaIdFromFilename(filename);
			String mediaId = mediaIdCapM.replace("M","m");
			for (String charId : charIds){
				if (annotationRoots.contains(mediaId + "_" + charId)){
					isMediaFileAssociatedWithChar = true;
				}
			}
			if (isMediaFileAssociatedWithChar){
				for (String viewId : viewIds){
					if (sddFile.isMediaOfView(mediaId, viewId)){
						mediaFileIsOfGoodView = true;
					}
				}
			}
			if (isMediaFileAssociatedWithChar && mediaFileIsOfGoodView){
				// keep the file
			}
			else {
				File parentFile = f.getParentFile();
				String grandParentDir = parentFile.getParent();
				String destDir = grandParentDir + FILESEP + "mediaScaledUnrelated";
				File destDirFile = new File(destDir);
				destDirFile.mkdirs();
				String destPath = destDir + FILESEP + filename;
				File destFile = new File(destPath);
				f.renameTo(destFile);
			}
		}
		
    }
	public String getMediaIdFromFilename(String filename){
		String[] parts = filename.split("\\.");
		return parts[0];
	}
}
