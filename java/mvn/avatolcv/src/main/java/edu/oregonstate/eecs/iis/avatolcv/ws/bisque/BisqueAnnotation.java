package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="tag")
@XmlAccessorType(XmlAccessType.FIELD)
public class BisqueAnnotation {
    /*<tag created="2014-04-30T14:04:21.335381" 
     *     name="straight percurrent vein orientation" 
     *     owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" 
     *     permission="private"ts="2014-06-08T12:26:58.698896" 
     *     type="/data_service/template/4921264/tag/5223999" 
     *     uri="http://bovary.iplantcollaborative.org/data_service/00-sYCwqbfmiErqLsHzpds6G4/tag/5382743" 
     *     value=""/>
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
	
	//@XmlAttribute(name="resource_uniq")
    //private String resourceUniq = null;
	//@XmlAttribute
    //private String ts = null;
	@XmlAttribute
    private String uri = null;
    
	public BisqueAnnotation(){
		
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
//	public void setResourceUniq(String s){
//		this.resourceUniq = s;
//	}
//	public void setTs(String s){
//		this.ts = s;
//	}
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
	/*public String getResourceUniq(){
		return this.resourceUniq;
	}
	public String getTs(){
		return this.ts;
	}*/
	public String getUri(){
		return this.uri;
	}
}