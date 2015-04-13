package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="tag")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotationComboBox {
	/*
	 * <tag created="2014-03-26T21:38:54.464086" 
	 * name="longitudinal vein origin" 
	 * owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" 
	 * permission="private" ts="2014-06-08T10:51:06.611575" 
	 * uri="http://bovary.iplantcollaborative.org/data_service/00-i7YbFTj73epqcpQtXoXkyY/tag/5224002" 
	 * value="ComboBox"/>
	 */
	public AnnotationComboBox(){
		
	}

	@XmlAttribute
    private String created = null;
	@XmlAttribute
    private String name = null;
	@XmlAttribute
    private String owner = null;
	@XmlAttribute
    private String permission = null;
	@XmlAttribute
    private String uri = null;
	@XmlAttribute
    private String value = null;


	public void setCreated(String s){
		this.created = s;
	}

	public void setName(String s){
		this.name = s;
	}
	public void setOwner(String s){
		this.owner = s;
	}
	public void setPermission(String s){
		this.permission = s;
	}
	public void setValue(String s){
		this.value = s;
	}
	public void setUri(String s){
		this.uri = s;
	}
	
	
	public String getCreated(){
		return this.created;
	}

	public String getName(){
		return this.name;
	}
	
	public String getOwner(){
		return this.owner;
	}
	public String getPermission(){
		return this.permission;
	}
	public String getValue(){
		return this.value;
	}
	public String getUri(){
		return this.uri;
	}
}

