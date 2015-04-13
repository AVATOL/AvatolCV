package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@SuppressWarnings("restriction")
@XmlRootElement(name="resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatasetResource {

	@XmlElement
    private List<BisqueDataset> dataset = null;
	public DatasetResource(){
		
	}
	public List<BisqueDataset> getDataset(){
		return dataset;
	}
	public void setDataset(List<BisqueDataset> d){
		this.dataset = d;
	}
	
}
