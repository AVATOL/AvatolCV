package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class BogusBisqueWSClient implements BisqueWSClient {
	private static final String FILESEP = System.getProperty("file.separator");
    private String goodPassword = "Neton3plants**";
    private String goodUsername = "jedirv";
    private boolean authenticated = false;
    Hashtable<String, List<BisqueAnnotation>> annotationHash = new Hashtable<String, List<BisqueAnnotation>>();
    public BogusBisqueWSClient(){
    	List<BisqueAnnotation> annotationsNeph = new ArrayList<BisqueAnnotation>();
    	BisqueAnnotation nephName = new BisqueAnnotation();
    	
    	nephName.setName("name");
    	nephName.setValue("Neph");
    	annotationsNeph.add(nephName);

		BisqueAnnotation nephGender = new BisqueAnnotation();
		nephGender.setName("gender");
		nephGender.setValue("male");
		annotationsNeph.add(nephGender);
		
		annotationHash.put("nephUniq", annotationsNeph);
		//
		List<BisqueAnnotation> annotationsFo = new ArrayList<BisqueAnnotation>();
    	BisqueAnnotation foName = new BisqueAnnotation();
    	
    	foName.setName("name");
    	foName.setValue("Fo");
    	annotationsFo.add(foName);

		BisqueAnnotation foGender = new BisqueAnnotation();
		foGender.setName("gender");
		foGender.setValue("male");
		annotationsFo.add(foGender);
		
		annotationHash.put("foUniq", annotationsFo);
		//
		List<BisqueAnnotation> annotationsEw = new ArrayList<BisqueAnnotation>();
    	BisqueAnnotation ewName = new BisqueAnnotation();
    	
    	ewName.setName("name");
    	ewName.setValue("ew");
    	annotationsEw.add(ewName);

		BisqueAnnotation ewGender = new BisqueAnnotation();
		ewGender.setName("gender");
		ewGender.setValue("male");
		annotationsEw.add(ewGender);
		
		annotationHash.put("ewUniq", annotationsEw);
		//
		List<BisqueAnnotation> annotationsLb = new ArrayList<BisqueAnnotation>();
    	BisqueAnnotation lbName = new BisqueAnnotation();
    	
    	lbName.setName("name");
    	lbName.setValue("lb");
    	annotationsLb.add(lbName);

		BisqueAnnotation lbGender = new BisqueAnnotation();
		lbGender.setName("gender");
		lbGender.setValue("female");
		annotationsLb.add(lbGender);
		
		annotationHash.put("lbUniq", annotationsLb);
		//
		List<BisqueAnnotation> annotationsPree = new ArrayList<BisqueAnnotation>();
    	BisqueAnnotation preeName = new BisqueAnnotation();
    	
    	preeName.setName("name");
    	preeName.setValue("pree");
    	annotationsPree.add(preeName);

		BisqueAnnotation preeGender = new BisqueAnnotation();
		preeGender.setName("gender");
		preeGender.setValue("male");
		annotationsPree.add(preeGender);
		
		annotationHash.put("preeUniq", annotationsPree);
    }
	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return this.authenticated;
	}

	@Override
	public boolean authenticate(String name, String password)
			throws BisqueWSException {
		// TODO Auto-generated method stub
		if (name.equals(this.goodUsername) && password.equals(goodPassword)){
			authenticated = true;
		}
		else {
			throw new BisqueWSException("bad username / password combo");
		}
		return this.authenticated;
	}

	@Override
	public List<BisqueDataset> getDatasets() throws BisqueWSException {
		List<BisqueDataset> datasets = new ArrayList<BisqueDataset>();
		BisqueDataset jedFlow = new BisqueDataset();
		jedFlow.setName("jedFlow");
		jedFlow.setResourceUniq("jedFlowUniq");
		datasets.add(jedFlow);
		BisqueDataset home = new BisqueDataset();
		home.setName("jedHome");
		home.setResourceUniq("jedHomeUniq");
		datasets.add(home);
		return datasets;
	}

	@Override
	public List<BisqueImage> getImagesForDataset(String datasetResource_uniq)
			throws BisqueWSException {
		if (!datasetResource_uniq.equals("jedHomeUniq")){
			throw new BisqueWSException("only coded to handle dataset jedHome");
		}
		List<BisqueImage> images = new ArrayList<BisqueImage>();
		BisqueImage fo = new BisqueImage();
		fo.setResourceUniq("foUniq");
		fo.setName("fo");
		images.add(fo);
		BisqueImage lb = new BisqueImage();
		lb.setResourceUniq("lbUniq");
		lb.setName("lb");
		images.add(lb);
		BisqueImage pree = new BisqueImage();
		pree.setResourceUniq("preeUniq");
		pree.setName("pree");
		images.add(pree);
		BisqueImage neph = new BisqueImage();
		neph.setResourceUniq("nephUniq");
		neph.setName("neph");
		images.add(neph);
		BisqueImage ew = new BisqueImage();
		ew.setResourceUniq("ewUniq");
		ew.setName("ew");
		images.add(ew);
		return images;
	}

	public String getTestSupportDataDir(){
		String laptopDir = "C:\\avatol\\git\\avatol_cv\\testSupportData";
		String desktopDir = "C:\\jed\\avatol\\git\\avatol_cv\\testSupportData";
		File f = new File(laptopDir);
		File f2 = new File(desktopDir);
		if (f.exists()){
			return laptopDir;
		}
		else if (f2.exists()){
			return desktopDir;
		}
		else {
			return null;
		}
	}
	@Override
	public boolean downloadImageOfWidth(String imageResource_uniq, int width, String dirToSaveTo, String imageRootName)
			throws BisqueWSException {
		// we have the files in the source dir as just the name, no id, so strip the id out of the imageRootName
		String sourceImageRootName = imageRootName.replaceFirst(imageResource_uniq + "_", "");
		String sourceDir = getTestSupportDataDir();
		String sourcePath = sourceDir + FILESEP + sourceImageRootName + ".jpg";
		String destPath = dirToSaveTo + FILESEP + imageRootName + "_" + width + ".jpg";
		File destFile = new File(destPath);
		if (destFile.exists()){
			System.out.println(destPath + " already downloaded.");
			return true;
		}
		File f = new File(sourcePath);
		try {
		    FileInputStream is = new FileInputStream(f);
		    OutputStream os = new FileOutputStream(destPath);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            is.close();
		}
		catch(FileNotFoundException fnfe){
			throw new BisqueWSException("file not found " + sourcePath, fnfe);
		}
		catch(IOException ioe){
			throw new BisqueWSException("problem writing file " + destPath, ioe);
		}
		return false;
	}

	@Override
	public List<BisqueAnnotation> getAnnotationsForImage(
			String imageResource_uniq) throws BisqueWSException {
		return annotationHash.get(imageResource_uniq);
	}

	@Override
	public List<String> getAnnotationValueOptions(String annotationTypeValue)
			throws BisqueWSException {
		throw new BisqueWSException("haven't implemented getAnnotationValueOptions in bogus WS client");
		//List<String> values = new ArrayList<String>();
		//values.add("a1Value");
		//values.add("a2Value");
		//return values;
	}

	@Override
	public boolean addNewAnnotation(String imageResource_uniq, String key,
			String value) {
		// TODO Auto-generated method stub
		List<BisqueAnnotation> annotations = annotationHash.get(imageResource_uniq);
		BisqueAnnotation a = new BisqueAnnotation();
		a.setName(key);
		a.setValue(value);
		annotations.add(a);
		return true;
	}

	@Override
	public boolean reviseAnnotation(String imageResource_uniq, String key,
			String value) {

		List<BisqueAnnotation> annotations = annotationHash.get(imageResource_uniq);
		for (BisqueAnnotation a : annotations){
			if (a.getName().equals(key)){
				a.setValue(value);
				return true;
			}
		}
		return false;
	}

}
