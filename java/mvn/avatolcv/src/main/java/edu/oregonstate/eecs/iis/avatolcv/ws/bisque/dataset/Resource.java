package edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resource {

	@XmlElement
    private List<Dataset> dataset = null;
	public Resource(){
		
	}
	public List<Dataset> getDataset(){
		return dataset;
	}
	public void setDataset(List<Dataset> d){
		this.dataset = d;
	}
	
}
