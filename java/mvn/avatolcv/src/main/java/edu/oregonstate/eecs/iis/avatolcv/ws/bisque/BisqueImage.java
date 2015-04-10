package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="image")
@XmlAccessorType(XmlAccessType.FIELD)
public class BisqueImage {
	/*<image created="2013-06-28T14:43:14.858817" 
	 *       name="pso_4380.tiff" 
	 *       owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" 
	 *       permission="private" 
	 *       resource_uniq="00-a8QoRrW6EEZD4UuRsdjBQW" 
	 *       ts="2013-06-28T14:43:14.858817" 
	 *       uri="http://bovary.iplantcollaborative.org/data_service/00-a8QoRrW6EEZD4UuRsdjBQW"
	 *       value="irods://data.iplantcollaborative.org/iplant/home/avatol-nybg/bisque_data/uploads/a/a8QoRrW6EEZD4UuRsdjBQW-pso_4380.tiff"/>
	*
	*/
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
	@XmlAttribute
    private String value = null;
    
	public BisqueImage(){
		
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
	public String getResourceUniq(){
		return this.resourceUniq;
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
