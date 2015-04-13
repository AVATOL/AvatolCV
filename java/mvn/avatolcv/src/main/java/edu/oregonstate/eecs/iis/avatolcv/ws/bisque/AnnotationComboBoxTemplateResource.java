package edu.oregonstate.eecs.iis.avatolcv.ws.bisque;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/*
 *
 *<tag created="2014-03-26T21:38:54.464086" name="longitudinal vein origin" owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" permission="private" ts="2014-06-08T10:51:06.611575" uri="http://bovary.iplantcollaborative.org/data_service/00-i7YbFTj73epqcpQtXoXkyY/tag/5224002" value="ComboBox">
      <template created="2014-03-24T21:36:54.741938" owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" permission="private" ts="2014-06-08T10:51:06.611575" uri="http://bovary.iplantcollaborative.org/data_service/00-i7YbFTj73epqcpQtXoXkyY/tag/5224002/template/5217794">
          ...
        <tag created="2014-03-26T21:38:54.464086" name="select" owner="http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj" permission="private" ts="2014-06-08T10:51:06.611575" type="string" uri="http://bovary.iplantcollaborative.org/data_service/00-i7YbFTj73epqcpQtXoXkyY/tag/5224002/template/5217794/tag/5224093" value="basal, suprabasal, mixed"/>
          ...
      </template>
</tag>

 */
@SuppressWarnings("restriction")
@XmlRootElement(name="tag")
@XmlAccessorType(XmlAccessType.FIELD)
public class AnnotationComboBoxTemplateResource {

	@XmlElement
    private AnnotationComboBoxTemplate template = null;
	
	public void setTemplate(AnnotationComboBoxTemplate s){
		this.template = s;
	}

	public AnnotationComboBoxTemplate getTemplate(){
		return this.template;
	} 
}
