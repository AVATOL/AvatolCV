package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="tag1")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotationComboBoxProperty {
	/*
	 * <tag created="2014-03-26T21:38:54.464086" 
	 *      name="select" 
	 *      owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" 
	 *      permission="private" 
	 *      ts="2014-06-08T10:51:06.611575" 
	 *      type="select" 
	 *      uri="http://bovary.iplantcollaborative.org/data_service/00-i7YbFTj73epqcpQtXoXkyY/tag/5224002/template/5217794/tag/5224093" 
	 *      value="basal, suprabasal, mixed"/>
	 */

	@XmlAttribute
    private String created = null;
	@XmlAttribute
    private String name = null;
	@XmlAttribute
    private String owner = null;
	@XmlAttribute
    private String permission = null;
	@XmlAttribute
    private String ts = null;
	@XmlAttribute
    private String type = null;
	@XmlAttribute
    private String uri = null;
	@XmlAttribute
    private String value = null;
    
	public AnnotationComboBoxProperty(){
		
	}
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
	public void setType(String s){
		this.type = s;
	}
	public void setTs(String s){
		this.ts = s;
	}
	public void setUri(String s){
		this.uri = s;
	}
	public void setValue(String s){
		this.value = s;
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
	public String getType(){
		return this.type;
	}
	public String getTs(){
		return this.ts;
	}
	public String getUri(){
		return this.uri;
	}
	public String getValue(){
		return this.value;
	}
}
