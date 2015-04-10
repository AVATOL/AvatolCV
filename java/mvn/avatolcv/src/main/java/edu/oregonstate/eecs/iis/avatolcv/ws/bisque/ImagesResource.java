package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImagesResource {
	@XmlElement
    private List<BisqueImage> image = null;
	public ImagesResource(){
		
	}
	public List<BisqueImage> getImage(){
		return image;
	}
	public void setImage(List<BisqueImage> s){
		this.image = s;
	}
}
