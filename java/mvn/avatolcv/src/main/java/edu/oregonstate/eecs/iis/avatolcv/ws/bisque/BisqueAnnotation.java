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
	@XmlAttribute
    private String value = null;
	@XmlAttribute
    private String type = "";
	
	//@XmlAttribute(name="resource_uniq")
    //private String resourceUniq = null;
	//@XmlAttribute
    //private String ts = null;
	@XmlAttribute
    private String uri = null;
    
	public BisqueAnnotation(){
		
	}
	public void setCreated(String s){
		this.created = s.trim();
	}

	public void setName(String s){
		this.name = s.trim();
	}
	public void setOwner(String s){
		this.owner = s.trim();
	}
	public void setPermission(String s){
		this.permission = s.trim();
	}
	public void setValue(String s){
		this.value = s.trim();
	}
	public void setType(String s){
		this.type = s.trim();
	}
	public void setUri(String s){
		this.uri = s.trim();
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
	public String getType(){
		return this.type;
	}
	public String getUri(){
		return this.uri;
	}
	public String getAnnotationID(){
	    String[] parts = this.type.split("/");
	    String annotationID = parts[parts.length - 1];
	    return annotationID;
	}
	public boolean hasTypeValueConsistentWithComboBox(){
	    if (null == type) {
	        return false;
	    }
	    String[] parts = this.type.split("/");
	    if (!(parts.length == 5)){
	        return false;
	    }
	    if (!(parts[1].equals("data_service"))){
	        return false;
	    }
	    if (!(parts[3].equals("tag"))){
            return false;
        }
	    String idNumberString = parts[4];
	    try {
	        Integer idNumberInteger = new Integer(idNumberString);
	    }
	    catch(NumberFormatException e){
	        return false;
	    }
	    return true;
	}
}