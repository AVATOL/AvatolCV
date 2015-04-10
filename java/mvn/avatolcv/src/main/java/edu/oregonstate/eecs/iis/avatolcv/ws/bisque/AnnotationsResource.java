package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="image")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotationsResource {
	@XmlElement
    private List<BisqueAnnotation> tag = null;
	public AnnotationsResource(){
		
	}
	public List<BisqueAnnotation> getTag(){
		return tag;
	}
	public void setTag(List<BisqueAnnotation> s){
		this.tag = s;
	}
}

