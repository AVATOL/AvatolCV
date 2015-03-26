package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.glassfish.jersey.SslConfigurator;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;


public class BisqueWSClient {

	public boolean authenticate(String name, String password) throws BisqueWSException{
		//ssl :   https://jersey.java.net/nonav/documentation/2.0/client.html  - Securing a client
		//  response.getStringHeaders()
		//  client.target(someurl).header("some-header", "true")
		//  response.getCookies()
		//  
		// http://tutorials.jenkov.com/oauth2/authorization-code-request-response.html
		// https://ssl.trustwave.com/support/support-how-ssl-works.php
		// http://agaveapi.co/client-registration/
		try {
			Client client = ClientBuilder.newClient();
	        
	        WebTarget webTarget = client.target("http://bisque.iplantc.org/auth_service/");
	        WebTarget resourceWebTarget = webTarget.path("login");
	        Invocation.Builder invocationBuilder =
	        		resourceWebTarget.request(MediaType.TEXT_HTML);
	       
	        Response response = invocationBuilder.get();
	        dumpResponse(response);
	        
	        Client client2 = ClientBuilder.newClient();
	        String redirectLocation = "" + response.getLocation();
	        WebTarget webTarget2 = client2.target(redirectLocation);
	        Invocation.Builder invocationBuilder2 =
	        		webTarget2.request();
	        System.out.println("trying redirect...");
	        Response response2 = invocationBuilder2.get();
	        dumpResponse(response2);

	        String htmlForm = response2.readEntity(String.class);
	        System.out.println("response2.readEntity(String.class) yields " + htmlForm);
	        
	        
	        /*
	         Need to parse the form to ensure the input called username is there, the input called password is there,
	         and find every <input type="hidden" "lt", "els1", "_eventId" and its value
	         
	        <input id="username" name="username" class="required" tabindex="1" accesskey="i" type="text" value="" size="25" autocomplete="false"/>
			
			<input id="password" name="password" class="required" tabindex="2" accesskey="p" type="password" value="" size="25" autocomplete="off"/>
       
			<input type="hidden" name="lt" value="LT-10848-VOmmDRWxeKEpnxEOmdLfG0tKjvGnCJ" />
			<input type="hidden" name="execution" value="e1s1" />
			<input type="hidden" name="_eventId" value="submit" />
			*/
	        
	        Hashtable<String,String> authKeyVal = new Hashtable<String,String>();
	        
	        System.out.println("Looking for hidden fields");
	        Document doc = Jsoup.parse(htmlForm);
	        
	        if (!isLoginFieldPresent("username", doc)){
	        	throw new BisqueWSException("no element with id 'username' on bisque login page.");
	        }
	        if (!isLoginFieldPresent("password", doc)){
	        	throw new BisqueWSException("no element with id 'password' on bisque login page.");
	        }
	        authKeyVal.put("username", "avatol-nybg");
	        authKeyVal.put("password", "Monocots123");
	        Elements elements = doc.select("input");
	        for (Element element : elements){
	        	if (element.hasAttr("type")){
	        		String typeValue = element.attr("type");
	        		if (typeValue.equals("hidden")){
	        			System.out.println("" + element);
	        			String hname = element.attr("name");
	        			String hval = element.attr("value");
	        			authKeyVal.put(hname, hval);
	        		}
	        	}
	        }
	        //http://auth.iplantc.org/cas?service=http://bisque.iplantc.org/auth_service/cas_login_handler
	        
	        SslConfigurator sslConfig = SslConfigurator.newInstance();
	        SSLContext sslContext = sslConfig.createSSLContext();
	        Client client3 = ClientBuilder.newBuilder().sslContext(sslContext).build();
	        
	        
	        
	        //Client client3 = ClientBuilder.newClient();
	        WebTarget webTarget3 = client3.target("https://auth.iplantc.org/cas?service=http://bisque.iplantc.org/auth_service/cas_login_handler");
	        //Invocation.Builder invocationBuilder3 =
	        //		webTarget3.request();

	        Enumeration<String> keysEnum = authKeyVal.keys();
	        Form form = new Form();

	        while (keysEnum.hasMoreElements()){
	        	String key = keysEnum.nextElement();
	        	form.param(key, authKeyVal.get(key));
	        }
	        
	        Response formPostResponse =
	        		webTarget3.request(MediaType.TEXT_PLAIN_TYPE)
	                        .post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
	        
	        dumpResponse(formPostResponse);
	        
	        	
	        	
	        // xmlString = response.readEntity(String.class);
            //System.out.println("xml string -" + xmlString + "-");
	        //JAXBContext context = JAXBContext.newInstance(edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource.class);
	        //StringReader reader = new StringReader(xmlString);

		    //Unmarshaller unmarshaller = context.createUnmarshaller();
		    //edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource resource = (edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource) unmarshaller.unmarshal(reader);
		    //datasetList = resource.getDataset();
		}
		catch(Exception je){
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
		return false;
	}
	
	public void dumpResponse(Response response){
		 System.out.println("response.getStatus() yields " + response.getStatus());
	     System.out.println("response.toString() yields " + response.toString());
	     System.out.println("response.getLocation() yields " + response.getLocation());
	     System.out.println("response.getMediaType() yields " + response.getMediaType());
	     System.out.println("response.getStringHeaders() yields " + response.getStringHeaders());
	     System.out.println("response.getCookies() yields " + response.getCookies());
	     System.out.println("response.getCookiesHeaders() yields " + response.getHeaders());
	     System.out.println("response.getLinks() yields " + response.getLinks());
	     System.out.println("response.getMetadata() yields " + response.getMetadata());
	     System.out.println("");
	     System.out.println("response.getHeaderString('Date') yields " + response.getHeaderString("Date"));
	     System.out.println("response.getHeaderString('Content-Length') yields " + response.getHeaderString("Content-Length"));
	     System.out.println("response.getHeaderString('Location') yields " + response.getHeaderString("Location"));
	     System.out.println("response.getHeaderString('Connection') yields " + response.getHeaderString("Connection"));
	     System.out.println("response.getHeaderString('Content-Type') yields " + response.getHeaderString("Content-Type"));
	     System.out.println("response.getHeaderString('Server') yields " + response.getHeaderString("Server"));
	     System.out.println("response.getHeaderString('uri') yields " + response.getHeaderString("uri"));
	     System.out.println("response.getHeaderString('URI') yields " + response.getHeaderString("URI"));
	     System.out.println("");
	     //System.out.println("response.readEntity(String.class) yields " + response.readEntity(String.class));
	}
	public boolean isLoginFieldPresent(String fieldName, Document doc){
		Element e = doc.getElementById(fieldName);
        if (null == e){
        	return false;
        }
        if (!e.hasAttr("name")){
        	return false;
        }
        if (!e.attr("name").equals(fieldName)){
        	return false;
        }
        return true;
	}
	public List<edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Dataset> getDatasets() throws BisqueWSException {
		List<edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Dataset> datasetList = null;
		try {
			Client client = ClientBuilder.newClient();
	        
	        WebTarget webTarget = client.target("http://bovary.iplantcollaborative.org/data_service/");
	        WebTarget resourceWebTarget = webTarget.path("dataset");
	        Invocation.Builder invocationBuilder =
	        		resourceWebTarget.request(MediaType.APPLICATION_XML);
	       
	        Response response = invocationBuilder.get();
	        System.out.println(response.getStatus());
	        String xmlString = response.readEntity(String.class);
	        //http://www.javatechtipssharedbygaurav.com/2013/05/how-to-convert-pojo-to-xml-and-xml-to.html
	        JAXBContext context = JAXBContext.newInstance(edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource.class);
	        StringReader reader = new StringReader(xmlString);

		    Unmarshaller unmarshaller = context.createUnmarshaller();
		    edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource resource = (edu.oregonstate.eecs.iis.avatolcv.ws.bisque.dataset.Resource) unmarshaller.unmarshal(reader);
		    datasetList = resource.getDataset();
		}
		catch(JAXBException je){
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
        return datasetList;
	}
	public List<BisqueImage> getImagesForDataset(BisqueDataset dataset){
		return null;
	}
	public String getLargeImagePath(BisqueImage bi){
		return null;
	}
	public String getMediumImagePath(BisqueImage bi){
		return null;
	}
	public List<BisqueAnnotation> getAnnotationsForImage(BisqueImage bi){
		return null;
	}
	public List<String> getAnnotationValues(BisqueAnnotation ba){
		return null;
	}
	public boolean addNewAnnotation(BisqueImage bi, String key, String value){
		return false;
	}
	public boolean reviseAnnotation(BisqueImage bi, String key, String value){
		return false;
	}
}
