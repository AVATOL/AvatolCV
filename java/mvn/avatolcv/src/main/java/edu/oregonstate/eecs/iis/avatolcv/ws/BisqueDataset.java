package edu.oregonstate.eecs.iis.avatolcv.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BisqueDataset {
	@XmlElement
    private String created = null;
	@XmlElement
    private String name = null;
    @XmlElement
    private String owner = null;
    @XmlElement
    private String permission = null;
    @XmlElement
    private String resource_uniq = null;
    @XmlElement
    private String ts = null;
    @XmlElement
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
	public void setResource_uniq(String s){
		this.resource_uniq = s;
	}
	public void setTs(String s){
		this.ts = s;
	}
	public void setUri(String s){
		this.uri = s;
	}
	public String getName(){
		return this.name;
	}
}
