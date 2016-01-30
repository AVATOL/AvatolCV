package edu.oregonstate.eecs.iis.avatolcv.bisque;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSClientImpl;
import edu.oregonstate.eecs.iis.avatolcv.ws.BisqueWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public class SegTrainingExampleNameConverter {
	private static Hashtable<String, String> imageIdHash = new Hashtable<String,String>();
	private static final String FILESEP = System.getProperty("file.separator");
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BisqueWSClient client = new BisqueWSClientImpl();
		try {
			client.authenticate("jedirv", "Neton3plants**");
			List<BisqueDataset> datasets = client.getDatasets();
			String dsID = null;
			for (BisqueDataset d : datasets){
				String name = d.getName();
				if (name.equals("leafDev")){
					dsID = d.getResourceUniq();
				}
			}
			List<BisqueImage> images = client.getImagesForDataset(dsID);
			for (BisqueImage image : images){
				String name = ClassicSplitter.splitt(image.getName(),'.')[0];
				String id = image.getResourceUniq();
				System.out.println("registering name and id : " + id + "  " + name );
				imageIdHash.put(name, id);
			}
			String sourceDir = "C:\\avatol\\git\\avatol_cv\\sessionData\\leafDev\\seg\\trainingImages\\GT to Jed";
			String targetDir = "C:\\avatol\\git\\avatol_cv\\sessionData\\leafDev\\seg\\trainingImages";
			String unknownsDir = "C:\\avatol\\git\\avatol_cv\\sessionData\\leafDev\\seg\\trainingImages\\unknowns";
			File dir1 = new File(sourceDir);
			File[] files = dir1.listFiles();
			for (File f : files){
				String name = f.getName();
				String[] parts = ClassicSplitter.splitt(name,'.');
				String root = parts[0];
				String pureRoot = root.replace("_resize_GT", "");
				String idForName = imageIdHash.get(pureRoot);
				if (null == idForName){
					System.out.println("no id for name " + pureRoot);
					BufferedImage bi = ImageIO.read(f);
					System.out.println("copying " + f.getName() + " to unknowns");
					File unknown = new File(unknownsDir + FILESEP + f.getName());
					ImageIO.write(bi, "png", unknown);
				}
				else {
					root = root.replace("resize_GT", "1000_groundtruth");
					String newName = idForName + "_" + root.replaceAll("_","-") + ".jpg";
					String targetPath = targetDir + FILESEP + newName;
					
					File outFile = new File(targetPath);
					if (!outFile.exists()){
						BufferedImage bi = ImageIO.read(f);
						System.out.println("copying " + newName);
						ImageIO.write(bi, "jpeg", outFile);
					}
				}
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

}
