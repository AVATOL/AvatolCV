package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@SuppressWarnings("restriction")
@XmlRootElement(name="dataset")
@XmlAccessorType(XmlAccessType.FIELD)
public class BisqueDataset implements Comparable {

	@XmlAttribute
    private String created = null;
	@XmlAttribute
    private String name = null;
	@XmlAttribute
    private String owner = null;
	@XmlAttribute
    private String permission = null;
	@XmlAttribute(name="resource_uniq")
    private String resourceUniq = null;
	@XmlAttribute
    private String ts = null;
	@XmlAttribute
    private String uri = null;
    
	public BisqueDataset(){
		
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
	public void setResourceUniq(String s){
		this.resourceUniq = s;
	}
	public void setTs(String s){
		this.ts = s;
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
	public String getResourceUniq(){
		return this.resourceUniq;
	}
	public String getTs(){
		return this.ts;
	}
	public String getUri(){
		return this.uri;
	}
	@Override
	public int compareTo(Object arg0) {
		BisqueDataset other = (BisqueDataset)arg0;
		String otherName = other.getName();
		String thisName = this.getName();
		return thisName.compareTo(otherName);
	}
}
