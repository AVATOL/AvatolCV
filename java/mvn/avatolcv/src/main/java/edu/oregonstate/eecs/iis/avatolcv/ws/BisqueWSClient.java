package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.util.List;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;

public interface BisqueWSClient {
	public boolean isAuthenticated();
	public boolean authenticate(String name, String password) throws BisqueWSException;
	
	public List<BisqueDataset> getDatasets() throws BisqueWSException;
	
	public List<BisqueImage> getImagesForDataset(String datasetResource_uniq) throws BisqueWSException;
	public List<BisqueAnnotation> getAnnotationsForImage(String imageResource_uniq) throws BisqueWSException;
    public List<String> getAnnotationValueOptions(String annotationName, String annotationTypeValue) throws BisqueWSException;

	public boolean downloadImageOfWidth(String imageResource_uniq, int width, String dirToSaveTo, String imageNameRoot) throws BisqueWSException ;
	
	public boolean addNewAnnotation(String imageResource_uniq, String key, String value) throws BisqueWSException;
	public boolean reviseAnnotation(String imageResource_uniq, String key, String value) throws BisqueWSException;
}


