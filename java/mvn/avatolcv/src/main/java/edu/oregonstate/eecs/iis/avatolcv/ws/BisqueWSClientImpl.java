package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;














import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationComboBox;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationComboBoxProperty;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationComboBoxTemplate;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationComboBoxTemplateResource;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.AnnotationsResource;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueImage;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.DatasetResource;
import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.ImagesResource;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
//import org.glassfish.jersey.SslConfigurator;
//import org.glassfish.jersey.client.ClientConfig;
//import org.glassfish.jersey.client.ClientProperties;
//import org.glassfish.jersey.client.HttpUrlConnectorProvider;
//import org.glassfish.jersey.jetty.connector.JettyConnectorProvider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;


public class BisqueWSClientImpl implements BisqueWSClient {
	private static final String FILESEP = System.getProperty("file.separator");
	Map<String, NewCookie> authenticationCookies = null;
   
	public boolean isAuthenticated(){
		if (null == this.authenticationCookies){
			return false;
		}
		return true;
	}
	public boolean authenticate(String name, String password) throws BisqueWSException{
		/*
		 * Here's what needs to happen:
		 *
	     * INITIAL REQUEST
	     * 
		 * 1. initial request goes to /bisque.iplantc.org/auth_service/login
         * 2. redirect request is returned with Location field set to  
         *      http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F
         *      
         * SEND FIRST REDIRECT
         * 
         * 3. following that redirect will yield another redirect request to
         *      https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F
         * 
         * SEND SECOND REDIRECT
         * 
         * 4. following that redirect will Which will yield a form with 'username' and 'password' and some hidden fields 
         * 
         * SCRAPE FORM AND SEND FILLED OUT FORM TO GET "ticket="
         * 
         * 5. I need to parse the html form to find any hidden form fields 
         * 6. Then submit the form with the username and password filled out and include the hidden fields also to 
         *     https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F
 (same as where the form came from)
         * 
	     * REDIRECT WITH ticket= as param TO GENERATE auth_tkt
         * 
         * 7. This yields a redirect request that includes a ticket=
         *       http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org
         * 8. following that redirect (with all the cookies accumulated) yields another redirect that includes a cookie that includes an auth_tkt  (Bisque will check 
         *    the ticket against the CAS service and basically do a local login creating a cookie and returning it)
         *      auth_tkt=1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!
         *      
         *      gotchas:
         *      
         *      1.  Wireshark can't render the https encrypted bits of the conversation as text, so it looked like they weren't 
         *      happening on the wire, though I knew they were happening from my IDE evidence.  This should have been obvious, 
         *      but didn't occur to me at first.
         *
         *      2.  Jersey library is apparently rigged by default to follow one redirect automatically (a reasonable web default?),
         *       but then not a second.  This one drove me crazy for a while as I looked back and forth between the evidence of 
         *       what seemed to be going on in my IDE and on the wire until I knuckled down and commented out all my code except 
         *       the first GET, and then uncommented bit by bit.
		 */
		try {
			ClientConfig config = new ClientConfig();
			/*
			 * Turn off redirects - by default seems to be configured to follow the first redirect only.  I need to 
			 * see all the redirects as the auth_tkt comes in on one.
			 */
			config.property(ClientProperties.FOLLOW_REDIRECTS, false);
			Client client = ClientBuilder.newClient(config);
			/*
			 * INITIAL REQUEST
			 */
	        System.out.println("initial request to http://bovary.iplantcollaborative.org/auth_service/login");
	        WebTarget webTargetInitLogin = client.target("http://bovary.iplantcollaborative.org/auth_service/");
	        
	        WebTarget resourceWebTarget = webTargetInitLogin.path("login");
	        Invocation.Builder invocationBuilder =
	        		resourceWebTarget.request(MediaType.TEXT_HTML);

            NewCookie gaCookie = new NewCookie("_ga","GA1.2.1556824412.1424301652");
            NewCookie gatCookie = new NewCookie("_gat","1");
            NewCookie debugCookie1 = new NewCookie("debug1","initialRequest");
            invocationBuilder.cookie(gaCookie);
            invocationBuilder.cookie(debugCookie1);

	        System.out.println("\n\n\n============  response from initial request ============  ");
	        Response responseFromInitLogin = invocationBuilder.get();
	
	        
	        dumpResponse("responseFromInitLogin",responseFromInitLogin);
	        Map<String, NewCookie> cookieBatch0 = responseFromInitLogin.getCookies();
	        String redirectLocation1 = "" + responseFromInitLogin.getLocation();
	        /*
			 * SEND FIRST REDIRECT
			 */
	        System.out.println("redirectLocation1 to " + redirectLocation1);
	        WebTarget webTargetInitLoginRedirect1 = client.target(redirectLocation1);
	        Invocation.Builder invocationBuilderRedirect1 =
	        		webTargetInitLoginRedirect1.request(MediaType.TEXT_HTML);
	        
            NewCookie debugCookie2 = new NewCookie("debug2","firstRedirect");
            invocationBuilderRedirect1.cookie(debugCookie2);
	        Response responseFromRedirect1 = invocationBuilderRedirect1.get();
	        dumpResponse("responseFromRedirect1",responseFromRedirect1);

	        /*
			 * SEND SECOND REDIRECT
			 */
	        String redirectLocation2 = "" + responseFromRedirect1.getLocation();
	        System.out.println("redirectLocation2 to " + redirectLocation2);
	        WebTarget webTargetInitLoginRedirect2 = client.target(redirectLocation2);
	        Invocation.Builder invocationBuilderRedirect2 =
	        		webTargetInitLoginRedirect2.request(MediaType.TEXT_HTML);
	       
            NewCookie debugCookie3 = new NewCookie("debug3","secondRedirect");
            invocationBuilderRedirect2.cookie(debugCookie3);
	        Response responseFromRedirect2 = invocationBuilderRedirect2.get();
	        dumpResponse("responseFromRedirect2",responseFromRedirect2);
	        /*
			 * SCRAPE FORM AND SEND FILLED OUT FORM TO GET "ticket="
			 */
	        //String redirectLocation3 = "" + responseFromRedirect2.getLocation(); // this returns null, so use prior redirect location
	        System.out.println("\n\n\n============  loading cookies from second redirect result ============  ");
            Map<String, NewCookie> cookieBatch1 = responseFromRedirect2.getCookies();
            System.out.println("hasEntity: " + responseFromRedirect2.hasEntity());
	        String htmlForm = responseFromRedirect2.readEntity(String.class);
	        //System.out.println("response2.readEntity(String.class) yields " + htmlForm);
	        Set<String> keySet0 = cookieBatch1.keySet();
	        for (String key : keySet0){
	        	NewCookie ncook = cookieBatch1.get(key);
	        	System.out.println("cookie : " + ncook);
	        }
	        System.out.println("\n============  scraping form for hidden fields ============  ");
	        Hashtable<String,String> authKeyVal = loadAuthKeyValsFromForm(htmlForm, name, password);
	       
	        System.out.println("sending filled out form back to (should be same) url " + redirectLocation2);
	        //WebTarget webTargetSubmittingForm = client.target(redirectLocation3);
	        WebTarget webTargetSubmittingForm = client.target(redirectLocation2);// for some reason, we need to use redirectionLocation2
		       
	        Enumeration<String> keysEnum = authKeyVal.keys();
	        Form form = new Form();

	        while (keysEnum.hasMoreElements()){
	        	String key = keysEnum.nextElement();
	        	String value = authKeyVal.get(key);
	        	form.param(key, value);
	        	System.out.println("form field : " + key + "=" + value);
	        }
	        Invocation.Builder invocationBuilder3 = webTargetSubmittingForm.request(MediaType.TEXT_HTML);
	        addCookiesToInvocationBuilder(invocationBuilder3,cookieBatch1);

            NewCookie debugCookie4 = new NewCookie("debug4","sendingFormData");
            invocationBuilder3.cookie(debugCookie3);

	        System.out.println("\n\n\n============  sending filled out form ============  ");
	        Response formPostResponse =
	        		invocationBuilder3
	                        .post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                            //.post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));

	        System.out.println("\n\n\n============  dumping form response ============");
	        dumpResponse("formPostResponse",formPostResponse);
	        int status = formPostResponse.getStatus();
	        if (status == 200){
	        	// no redirect given - authentication has failed.
	        	return false;
	        }
	        /*
			 * REDIRECT WITH ticket= as param TO GENERATE auth_tkt
			 */
	        System.out.println("\n\n\n============  trying redirect with ticket in hand ============  ");
	        String formPostRedirectLocation = "" + formPostResponse.getLocation();
	        System.out.println("formPostResponseRedirectLocation : " + formPostRedirectLocation);
	        //WebTarget webTarget4 = client4.target(formPostRedirectLocation);
	        WebTarget webTarget4 = client.target(formPostRedirectLocation);
	        Invocation.Builder invocationBuilder4 =
	        		webTarget4.request(MediaType.TEXT_HTML);
	        Map<String, NewCookie> cookieBatch2 = formPostResponse.getCookies();
	        addCookiesToInvocationBuilder(invocationBuilder4,cookieBatch2);
	        addCookiesToInvocationBuilder(invocationBuilder4,cookieBatch1);
            invocationBuilder4.cookie(gaCookie);
            invocationBuilder4.cookie(gatCookie);

            NewCookie debugCookie5 = new NewCookie("debug5","followingRedirectFromFormResponse");
            invocationBuilder4.cookie(debugCookie5);
	        Response response4 = invocationBuilder4.get();
	        dumpResponse("response4",response4);
	        /*
			 * CAPTURE auth_tkt
			 */
	        //System.out.println("\n\n readEntity on response 4:\n\n" + response4.readEntity(String.class));
	        authenticationCookies = response4.getCookies();
	        Set<String> keySet4 = authenticationCookies.keySet();
	        for (String key : keySet4){
	        	System.out.println("final cookie " + key + " " + authenticationCookies.get(key));
	        }

	        /*
			 * FOLLOW REDIRECT with auth_tkt in hand
			 */
	        String redirectLocationFinal = "" + response4.getLocation();
	        System.out.println("redirectLocationFinal to " + redirectLocationFinal);
	        WebTarget webTargetFinal = client.target(redirectLocation2);
	        Invocation.Builder invocationBuilderRedirectFinal =
	        		webTargetFinal.request(MediaType.TEXT_HTML);
	       
            NewCookie debugCookie6 = new NewCookie("debug6","finalRedirect");
            invocationBuilderRedirectFinal.cookie(debugCookie6);

	        Map<String, NewCookie> authenticationCookies = response4.getCookies();
	        addCookiesToInvocationBuilder(invocationBuilderRedirectFinal,authenticationCookies);
	        // no need to follow the final redirect - that's just saying "here's where you can use the authentication"
	       // Response responseFromFinal = invocationBuilderRedirectFinal.get();
	       // dumpResponse("responseFromFinal",responseFromFinal);
		}
		catch(Exception je){
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
		return true;
	}
	public void logout(){
		ClientConfig config = new ClientConfig();
		/*
		 * Turn off redirects - by default seems to be configured to follow the first redirect only.  I need to 
		 * see all the redirects as the auth_tkt comes in on one.
		 */
		config.property(ClientProperties.FOLLOW_REDIRECTS, false);
		Client client = ClientBuilder.newClient(config);
		/*
		 * INITIAL REQUEST
		 */
        System.out.println("initial request to http://bovary.iplantcollaborative.org/auth_service/logout_handler");
        WebTarget webTargetInitLogin = client.target("http://bovary.iplantcollaborative.org/auth_service/");
        
        WebTarget resourceWebTarget = webTargetInitLogin.path("logout_handler");
        Invocation.Builder invocationBuilder =
        		resourceWebTarget.request(MediaType.TEXT_HTML);
        if (authenticationCookies != null){
        	addCookiesToInvocationBuilder(invocationBuilder,authenticationCookies);
        }
       // NewCookie gaCookie = new NewCookie("_ga","GA1.2.1556824412.1424301652");
        //NewCookie gatCookie = new NewCookie("_gat","1");
        //NewCookie debugCookie1 = new NewCookie("debug1","initialRequest");
       // invocationBuilder.cookie(gaCookie);
        //invocationBuilder.cookie(debugCookie1);

        System.out.println("\n\n\n============  response from initial request ============  ");
        Response responseFromInitLogout = invocationBuilder.get();

        
        dumpResponse("responseFromInitLogout",responseFromInitLogout);
        Map<String, NewCookie> cookieBatch0 = responseFromInitLogout.getCookies();
        String redirectLocation1 = "" + responseFromInitLogout.getLocation();
        
        /*
		 * SEND FIRST REDIRECT
		 */
        
        System.out.println("redirectLocation1 to " + redirectLocation1);
        WebTarget webTargetInitLogoutRedirect1 = client.target(redirectLocation1);
        Invocation.Builder invocationBuilderRedirect1 =
        		webTargetInitLogoutRedirect1.request(MediaType.TEXT_HTML);
        addCookiesToInvocationBuilder(invocationBuilderRedirect1,cookieBatch0);
        NewCookie debugCookie2 = new NewCookie("debug2","firstRedirect");
        invocationBuilderRedirect1.cookie(debugCookie2);
        Response responseFromRedirect1 = invocationBuilderRedirect1.get();
        Map<String, NewCookie> cookieBatch1 = responseFromRedirect1.getCookies();
        dumpResponse("responseFromRedirect1",responseFromRedirect1);

        /*
		 * SEND SECOND REDIRECT
		 */
        String redirectLocation2 = "" + responseFromRedirect1.getLocation();
        System.out.println("redirectLocation2 to " + redirectLocation2);
        WebTarget webTargetInitLogoutRedirect2 = client.target(redirectLocation2);
        Invocation.Builder invocationBuilderRedirect2 =
        		webTargetInitLogoutRedirect2.request(MediaType.TEXT_HTML);

        addCookiesToInvocationBuilder(invocationBuilderRedirect2,cookieBatch1);
        NewCookie debugCookie3 = new NewCookie("debug3","secondRedirect");
        invocationBuilderRedirect2.cookie(debugCookie3);
        Response responseFromRedirect2 = invocationBuilderRedirect2.get();
        Map<String, NewCookie> cookieBatch2 = responseFromRedirect2.getCookies();
        dumpResponse("responseFromRedirect2",responseFromRedirect2);
        /*
		 * SEND third REDIRECT
		 */
        String redirectLocation3 = "" + responseFromRedirect2.getLocation();
        System.out.println("redirectLocation3 to " + redirectLocation3);
        WebTarget webTargetInitLogoutRedirect3 = client.target(redirectLocation3);
        Invocation.Builder invocationBuilderRedirect3 =
        		webTargetInitLogoutRedirect3.request(MediaType.TEXT_HTML);

        addCookiesToInvocationBuilder(invocationBuilderRedirect3,cookieBatch2);
        NewCookie debugCookie4 = new NewCookie("debug4","thirdRedirect");
        invocationBuilderRedirect3.cookie(debugCookie4);
        Response responseFromRedirect3 = invocationBuilderRedirect3.get();
        dumpResponse("responseFromRedirect3",responseFromRedirect3);
	}
	public void addCookiesToInvocationBuilder(Invocation.Builder ib, Map<String, NewCookie> cookies){
		 Set<String> keySet3 = cookies.keySet();
		 for (String key : keySet3){
	         System.out.println("adding cookie " + cookies.get(key));
	         ib.cookie(cookies.get(key));
	     }   
	}
	public Hashtable<String,String> loadAuthKeyValsFromForm(String htmlForm, String name, String password) throws BisqueWSException {
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
        authKeyVal.put("username", name);
        authKeyVal.put("password", password);
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
        authKeyVal.put("submit", "LOGIN");
        return authKeyVal;
	}
	public void addAuthCookie(Invocation.Builder builder){
		System.out.println("adding auth cookies ");
		Set<String> keySet = authenticationCookies.keySet();
	    for (String key : keySet){
	    	NewCookie c = authenticationCookies.get(key);
	    	System.out.println(c.getName() + "=" + c.getValue());
	        builder.cookie(c);
	    }
		System.out.println("");
	}
	public void dumpResponse(String context, Response response){
		System.out.println("======================================================================");
		 System.out.println(context);
		System.out.println("======================================================================");
		 System.out.println("response.getStatus() yields " + response.getStatus());
	     System.out.println("response.toString() yields " + response.toString());
	     System.out.println("response.getLocation() yields " + response.getLocation());
	     //System.out.println("response.getMediaType() yields " + response.getMediaType());
	     System.out.println("response.getStringHeaders() yields " + response.getStringHeaders());
	     System.out.println("response.getCookies() yields " + response.getCookies());
	     //System.out.println("response.getCookiesHeaders() yields " + response.getHeaders());
	     //System.out.println("response.getLinks() yields " + response.getLinks());
	     //System.out.println("response.getMetadata() yields " + response.getMetadata());
	     System.out.println("");
	     System.out.println("response.getHeaders() yields " + response.getHeaders());
	     System.out.println("response.getHeaderString('Set-Cookie') yields " + response.getHeaderString("Set-Cookie"));
	     //System.out.println("response.getHeaderString('Date') yields " + response.getHeaderString("Date"));
	     //System.out.println("response.getHeaderString('Content-Length') yields " + response.getHeaderString("Content-Length"));
	     //System.out.println("response.getHeaderString('Location') yields " + response.getHeaderString("Location"));
	     //System.out.println("response.getHeaderString('Connection') yields " + response.getHeaderString("Connection"));
	     //System.out.println("response.getHeaderString('Content-Type') yields " + response.getHeaderString("Content-Type"));
	     //System.out.println("response.getHeaderString('Server') yields " + response.getHeaderString("Server"));
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
	public List<BisqueDataset> getDatasets() throws BisqueWSException {
		List<BisqueDataset> datasetList = null;
		try {
			Client client = ClientBuilder.newClient();
	        String url = "http://bovary.iplantcollaborative.org/data_service/dataset";
	        String xmlString = getXmlResponseFromUrl(client,url);
	        //http://www.javatechtipssharedbygaurav.com/2013/05/how-to-convert-pojo-to-xml-and-xml-to.html
	        @SuppressWarnings("restriction")
			JAXBContext context = JAXBContext.newInstance(DatasetResource.class);
	        StringReader reader = new StringReader(xmlString);

		    @SuppressWarnings("restriction")
			Unmarshaller unmarshaller = context.createUnmarshaller();
		    DatasetResource resource = (DatasetResource) unmarshaller.unmarshal(reader);
		    datasetList = resource.getDataset();
		}
		catch(JAXBException je){
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
        return datasetList;
	}
	public List<BisqueImage> getImagesForDataset(String datasetResource_uniq) throws BisqueWSException {
		List<BisqueImage> imageList = null;
		try {
			Client client = ClientBuilder.newClient();
	        //http://bovary.iplantcollaborative.org/data_service/dataset/00-kutq3fez25ntEkp5pdXhMM/value
			String url = "http://bovary.iplantcollaborative.org/data_service/dataset/" + datasetResource_uniq + "/value";
			String xmlString = getXmlResponseFromUrl(client,url);
	        //http://www.javatechtipssharedbygaurav.com/2013/05/how-to-convert-pojo-to-xml-and-xml-to.html
	        @SuppressWarnings("restriction")
			JAXBContext context = JAXBContext.newInstance(ImagesResource.class);
	        StringReader reader = new StringReader(xmlString);

		    @SuppressWarnings("restriction")
			Unmarshaller unmarshaller = context.createUnmarshaller();
		    ImagesResource resource = (ImagesResource) unmarshaller.unmarshal(reader);
		    imageList = resource.getImage();
		}
		catch(JAXBException je){
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
        return imageList;
	}
	public boolean downloadImageOfWidth(String imageResource_uniq, int width, String dirToSaveTo, String type, String imageName) throws BisqueWSException {
		boolean result = false;
		try {
			ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(MultiPartFeature.class);
            Client client =  ClientBuilder.newClient(clientConfig);
            client.property("accept", "image/jpg");
            client.property(ClientProperties.CONNECT_TIMEOUT, 5000);
            client.property(ClientProperties.READ_TIMEOUT,    5000);
            String mediaUrl = "http://bovary.iplantcollaborative.org/image_service/" + imageResource_uniq + "?resize=" + width + ",0&format=jpeg";
            WebTarget webTarget = client.target(mediaUrl);
            Invocation.Builder invocationBuilder = webTarget.request();
            addAuthCookie(invocationBuilder);
            Response response = invocationBuilder.get();
            int status = response.getStatus();
    	    if (status!= 200){
    	    	String reason = response.getStatusInfo().getReasonPhrase();
    	    	throw new BisqueWSException("error code " + status + " returned by downloadImageOfWidth... " + reason);
    	    }
            
            InputStream inputStream = response.readEntity(InputStream.class);
            //saving file to C:\avatol\git\avatol_cv\sessionData\jedHome\images\00-b7itcHVfYEEaBiMXFsibVS_neph.JPG_400.jpg
            String pathToSaveTo = dirToSaveTo + FILESEP + imageResource_uniq + "_" + imageName + "_" + type + ".jpg";
            System.out.println("saving file to " + pathToSaveTo);
            OutputStream outputStream = new FileOutputStream(pathToSaveTo);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
			result = true;
		}
		catch(IOException ioe){
			ioe.printStackTrace();
			result = false;
		}
		catch(ProcessingException pe){
			if (pe.getCause() instanceof SocketTimeoutException){
				throw new BisqueWSException("timeout");
			}
			else {
				throw new BisqueWSException("ProcessingException durin image download", pe);
			}
		}
		
        return result;
	}
	public List<BisqueAnnotation> getAnnotationsForImage(String imageResource_uniq) throws BisqueWSException {
		List<BisqueAnnotation> annotations = null;
		try {
			Client client = ClientBuilder.newClient();
	        //http://bovary.iplantcollaborative.org/data_service/00-sYCwqbfmiErqLsHzpds6G4/?view=deep
			String url = "http://bovary.iplantcollaborative.org/data_service/" + imageResource_uniq + "/?view=deep";
			String xmlString = getXmlResponseFromUrl(client,url);
	        //http://www.javatechtipssharedbygaurav.com/2013/05/how-to-convert-pojo-to-xml-and-xml-to.html
	        @SuppressWarnings("restriction")
			JAXBContext context = JAXBContext.newInstance(AnnotationsResource.class);
	        StringReader reader = new StringReader(xmlString);

		    @SuppressWarnings("restriction")
			Unmarshaller unmarshaller = context.createUnmarshaller();
		    AnnotationsResource resource = (AnnotationsResource) unmarshaller.unmarshal(reader);
		    annotations = resource.getTag();
		}
		catch(JAXBException je){
			System.out.println(je.getMessage());
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
        return annotations;
	}
	public String getXmlResponseFromUrl(Client client,String url){
		System.out.println("trying url : " + url);
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder =
        	 	webTarget.request(MediaType.APPLICATION_XML);
        addAuthCookie(invocationBuilder);
        Response response = invocationBuilder.get();
        System.out.println(response.getStatus());
        String xmlString = response.readEntity(String.class);
        return xmlString;
	}
	public List<String> getAnnotationValueOptions(String annotationTypeValue) throws BisqueWSException {
		List<String> annotationValues = new ArrayList<String>();
		try {
			/*
			 *  use the type to do a lookup
			 *  
			 *  type field will be of this form:  type="/data_service/template/4921264/tag/5224002" 
			 */
			
			Client client = ClientBuilder.newClient();
	        //http://bovary.iplantcollaborative.org/data_service/00-sYCwqbfmiErqLsHzpds6G4/?view=deep
			String url = "http://bovary.iplantcollaborative.org/" + annotationTypeValue;
			String xmlString = getXmlResponseFromUrl(client,url);
	        //http://www.javatechtipssharedbygaurav.com/2013/05/how-to-convert-pojo-to-xml-and-xml-to.html
	        @SuppressWarnings("restriction")
			JAXBContext context = JAXBContext.newInstance(AnnotationComboBox.class);
	        StringReader reader = new StringReader(xmlString);

		    @SuppressWarnings("restriction")
			Unmarshaller unmarshaller = context.createUnmarshaller();
		    AnnotationComboBox comboBox = (AnnotationComboBox) unmarshaller.unmarshal(reader);
		   
			/*
			 *  take the uri field from the result and look that up, adding /?view=deep
			 */
		    String uriFromComboBox = comboBox.getUri();
		    url = uriFromComboBox + "/?view=deep";
		    xmlString = getXmlResponseFromUrl(client,url);
		    System.out.println("xml string after url " + url + "\n" + xmlString);
		    JAXBContext context2 = JAXBContext.newInstance(AnnotationComboBoxTemplateResource.class);
	        reader = new StringReader(xmlString);

		    unmarshaller = context2.createUnmarshaller();
		    AnnotationComboBoxTemplateResource comboBoxTemplateResource = (AnnotationComboBoxTemplateResource) unmarshaller.unmarshal(reader);
		    
			/*
			 *  from that result, dig out the "string" tag from the template and it's value field has the range of values
			 */
		    AnnotationComboBoxTemplate template = comboBoxTemplateResource.getTemplate();
			List<AnnotationComboBoxProperty> properties = template.getTag();
			String selectChoices = null;
			for (AnnotationComboBoxProperty property : properties){
				String name = property.getName();
				if (name.equals("select")){
					selectChoices = property.getValue();
					String[] parts = selectChoices.split(",");
					for (String part: parts){
						annotationValues.add(part);
					}
				}
			}
			if (selectChoices == null){
				throw new BisqueWSException("couldn't find values for character " + annotationTypeValue);
			}
		}
		catch(@SuppressWarnings("restriction") JAXBException je){
			System.out.println(je.getMessage());
			throw new BisqueWSException("Problem unmarshalling xml response",je);
		}
        return annotationValues;
	}
	public boolean addNewAnnotation(String imageResource_uniq, String key, String value){
		return false;
	}
	public boolean reviseAnnotation(String imageResource_uniq, String key, String value){
		return false;
	}
	//ssl :   https://jersey.java.net/nonav/documentation/2.0/client.html  - Securing a client
			//  response.getStringHeaders()
			//  client.target(someurl).header("some-header", "true")
			//  response.getCookies()
			//  
			// http://tutorials.jenkov.com/oauth2/authorization-code-request-response.html
			// https://ssl.trustwave.com/support/support-how-ssl-works.php
}

/*
 * 
 * initial request to http://bovary.iplantcollaborative.org/auth_service/login



============  response from initial request ============  
======================================================================
responseFromInitLogin
======================================================================
response.getStatus() yields 302
response.toString() yields InboundJaxrsResponse{ClientResponse{method=GET, uri=http://bovary.iplantcollaborative.org/auth_service/login, status=302, reason=Found}}
response.getLocation() yields http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F
response.getStringHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:03 GMT], Content-Length=[375], Location=[http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getCookies() yields {}

response.getHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:03 GMT], Content-Length=[375], Location=[http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getHeaderString('Set-Cookie') yields null

redirectLocation1 to http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F
======================================================================
responseFromRedirect1
======================================================================
response.getStatus() yields 302
response.toString() yields InboundJaxrsResponse{ClientResponse{method=GET, uri=http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F, status=302, reason=Found}}
response.getLocation() yields https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F
response.getStringHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:09 GMT], Content-Length=[0], Location=[https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getCookies() yields {}

response.getHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:09 GMT], Content-Length=[0], Location=[https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getHeaderString('Set-Cookie') yields null

redirectLocation2 to https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F
======================================================================
responseFromRedirect2
======================================================================
response.getStatus() yields 200
response.toString() yields InboundJaxrsResponse{ClientResponse{method=GET, uri=https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F, status=200, reason=OK}}
response.getLocation() yields null
response.getStringHeaders() yields {Transfer-Encoding=[chunked], Date=[Wed, 01 Apr 2015 21:16:10 GMT], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Set-Cookie=[JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA; Path=/cas; Secure], Connection=[close], Content-Type=[text/html;charset=UTF-8], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getCookies() yields {JSESSIONID=JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA;Version=1;Path=/cas;Secure}

response.getHeaders() yields {Transfer-Encoding=[chunked], Date=[Wed, 01 Apr 2015 21:16:10 GMT], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Set-Cookie=[JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA; Path=/cas; Secure], Connection=[close], Content-Type=[text/html;charset=UTF-8], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getHeaderString('Set-Cookie') yields JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA; Path=/cas; Secure




============  loading cookies from second redirect result ============  
hasEntity: true
cookie : JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA;Version=1;Path=/cas;Secure

============  scraping form for hidden fields ============  
Looking for hidden fields
<input type="hidden" name="lt" value="LT-2897-jpqeufTXA3efcViJRzs4EEDRoIctNe">
<input type="hidden" name="execution" value="e1s1">
<input type="hidden" name="_eventId" value="submit">
sending filled out form back to (should be same) url https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F
form field : lt=LT-2897-jpqeufTXA3efcViJRzs4EEDRoIctNe
form field : _eventId=submit
form field : password=Monocots123
form field : submit=LOGIN
form field : execution=e1s1
form field : username=avatol-nybg
adding cookie JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA;Version=1;Path=/cas;Secure



============  sending filled out form ============  



============  dumping form response ============
======================================================================
formPostResponse
======================================================================
response.getStatus() yields 302
response.toString() yields InboundJaxrsResponse{ClientResponse{method=POST, uri=https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F, status=302, reason=Moved Temporarily}}
response.getLocation() yields http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org
response.getStringHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:10 GMT], Content-Length=[0], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Location=[http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org], Set-Cookie=[CASTGC=TGT-147-6DiDbDN3yBN0wwQX64IuUAhMyCucMfadpL2e4iMHTKhdcLsncE-auth.iplantc.org; Path=/cas/; Secure, CASPRIVACY=""; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/cas/], Content-Type=[text/plain; charset=UTF-8], Connection=[close], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getCookies() yields {CASTGC=CASTGC=TGT-147-6DiDbDN3yBN0wwQX64IuUAhMyCucMfadpL2e4iMHTKhdcLsncE-auth.iplantc.org;Version=1;Path=/cas/;Secure, CASPRIVACY=CASPRIVACY=;Version=1;Path=/cas/;Expires=Thu, 01 Jan 1970 00:00:10 GMT}

response.getHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:10 GMT], Content-Length=[0], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Location=[http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org], Set-Cookie=[CASTGC=TGT-147-6DiDbDN3yBN0wwQX64IuUAhMyCucMfadpL2e4iMHTKhdcLsncE-auth.iplantc.org; Path=/cas/; Secure, CASPRIVACY=""; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/cas/], Content-Type=[text/plain; charset=UTF-8], Connection=[close], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getHeaderString('Set-Cookie') yields CASTGC=TGT-147-6DiDbDN3yBN0wwQX64IuUAhMyCucMfadpL2e4iMHTKhdcLsncE-auth.iplantc.org; Path=/cas/; Secure,CASPRIVACY=""; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/cas/




============  trying redirect with ticket in hand ============  
formPostResponseRedirectLocation : http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org
adding cookie CASTGC=TGT-147-6DiDbDN3yBN0wwQX64IuUAhMyCucMfadpL2e4iMHTKhdcLsncE-auth.iplantc.org;Version=1;Path=/cas/;Secure
adding cookie CASPRIVACY=;Version=1;Path=/cas/;Expires=Thu, 01 Jan 1970 00:00:10 GMT
adding cookie JSESSIONID=1FEFA6AA74BAA5BFB101D04863A43BBA;Version=1;Path=/cas;Secure
======================================================================
response4
======================================================================
response.getStatus() yields 302
response.toString() yields InboundJaxrsResponse{ClientResponse{method=GET, uri=http://bovary.iplantcollaborative.org/auth_service/cas_login_handler?username=&came_from=%2F&ticket=ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org, status=302, reason=Found}}
response.getLocation() yields http://bovary.iplantcollaborative.org/
response.getStringHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:10 GMT], Content-Length=[0], Location=[http://bovary.iplantcollaborative.org/], Set-Cookie=[auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=.bovary.iplantcollaborative.org, auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=bovary.iplantcollaborative.org, auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getCookies() yields {auth_tkt=auth_tkt=1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!;Version=1;Path=/}

response.getHeaders() yields {Date=[Wed, 01 Apr 2015 21:16:10 GMT], Content-Length=[0], Location=[http://bovary.iplantcollaborative.org/], Set-Cookie=[auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=.bovary.iplantcollaborative.org, auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=bovary.iplantcollaborative.org, auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/], Connection=[keep-alive], Content-Type=[text/html; charset=UTF-8], Server=[nginx/1.2.6]}
response.getHeaderString('Set-Cookie') yields auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=.bovary.iplantcollaborative.org,auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/; Domain=bovary.iplantcollaborative.org,auth_tkt="1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!"; Path=/

final cookie auth_tkt auth_tkt=1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!;Version=1;Path=/
redirectLocationFinal to http://bovary.iplantcollaborative.org/
adding cookie auth_tkt=1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!;Version=1;Path=/
======================================================================
responseFromFinal
======================================================================
response.getStatus() yields 200
response.toString() yields InboundJaxrsResponse{ClientResponse{method=GET, uri=https://auth.iplantcollaborative.org/cas/login?service=http%3A%2F%2Fbovary.iplantcollaborative.org%2Fauth_service%2Fcas_login_handler%3Fusername%3D%26came_from%3D%252F, status=200, reason=OK}}
response.getLocation() yields null
response.getStringHeaders() yields {Transfer-Encoding=[chunked], Date=[Wed, 01 Apr 2015 21:16:10 GMT], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Set-Cookie=[JSESSIONID=0F95FA449BBCE4F08098FF561421334F; Path=/cas; Secure], Connection=[close], Content-Type=[text/html;charset=UTF-8], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getCookies() yields {JSESSIONID=JSESSIONID=0F95FA449BBCE4F08098FF561421334F;Version=1;Path=/cas;Secure}

response.getHeaders() yields {Transfer-Encoding=[chunked], Date=[Wed, 01 Apr 2015 21:16:10 GMT], Expires=[Thu, 01 Jan 1970 00:00:00 GMT], Set-Cookie=[JSESSIONID=0F95FA449BBCE4F08098FF561421334F; Path=/cas; Secure], Connection=[close], Content-Type=[text/html;charset=UTF-8], Cache-Control=[no-store, no-cache], Pragma=[no-cache]}
response.getHeaderString('Set-Cookie') yields JSESSIONID=0F95FA449BBCE4F08098FF561421334F; Path=/cas; Secure

adding auth cookies 
auth_tkt=1a7323950a50005ed35da577399b5f7d551c601aavatol-nybg!cas:ST-166-0cjaTreIMylzT9LB6KMi-auth.iplantc.org!

200

name          test 2013-06-28 14:42:46
resource_uniq 00-kutq3fez25ntEkp5pdXhMM
created       2013-06-28T14:43:16.397281
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2013-06-28T14:43:16.397281
uri           http://bovary.iplantcollaborative.org/data_service/00-kutq3fez25ntEkp5pdXhMM

name          veins-Alismatales
resource_uniq 00-d5gdWy8yBmtwsiN7qxPbmD
created       2013-07-10T12:38:56.753717
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T12:38:56.753717
uri           http://bovary.iplantcollaborative.org/data_service/00-d5gdWy8yBmtwsiN7qxPbmD

name          veins-Dioscorealesa-MThadeo
resource_uniq 00-oYyZSxXf9kuudMxxLm9UA7
created       2013-07-10T12:59:36.497514
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T12:59:36.497514
uri           http://bovary.iplantcollaborative.org/data_service/00-oYyZSxXf9kuudMxxLm9UA7

name          veins-Liliales-MThadeo
resource_uniq 00-NKF6xpbNWRJM4DbusnZzJH
created       2013-07-10T13:09:16.190131
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T13:09:16.190131
uri           http://bovary.iplantcollaborative.org/data_service/00-NKF6xpbNWRJM4DbusnZzJH

name          veins-Asperigales-MThadeo
resource_uniq 00-CTiHpnkhvZfsBqWD6Q9DFW
created       2013-07-10T13:30:11.440995
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T13:57:35.679393
uri           http://bovary.iplantcollaborative.org/data_service/00-CTiHpnkhvZfsBqWD6Q9DFW

name          veins-Margaret's Leaves-MThadeo
resource_uniq 00-jy7gqNawEZ4fPraJrpkdZ3
created       2013-07-10T14:07:40.616371
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T14:07:40.616371
uri           http://bovary.iplantcollaborative.org/data_service/00-jy7gqNawEZ4fPraJrpkdZ3

name          veins-non-classified-MThadeo
resource_uniq 00-8QyGFefSsqVGZFGbLiAsXW
created       2013-07-10T14:56:56.064751
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T14:56:56.064751
uri           http://bovary.iplantcollaborative.org/data_service/00-8QyGFefSsqVGZFGbLiAsXW

name          vein-Pandanales-MThadeo
resource_uniq 00-3qM56AiJ5y7s8mBqunug9b
created       2013-07-10T15:06:08.009408
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-18T15:36:05.908361
uri           http://bovary.iplantcollaborative.org/data_service/00-3qM56AiJ5y7s8mBqunug9b

name          veins-Poales-MThadeo
resource_uniq 00-pdFhMqeAnmkE3B69ecdSvF
created       2013-07-10T15:07:29.100079
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T15:07:29.100079
uri           http://bovary.iplantcollaborative.org/data_service/00-pdFhMqeAnmkE3B69ecdSvF

name          veins-Zingiberales-MThadeo
resource_uniq 00-tBEebejyqsWMMYEcyJGSKT
created       2013-07-10T15:08:49.186139
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T15:08:49.186139
uri           http://bovary.iplantcollaborative.org/data_service/00-tBEebejyqsWMMYEcyJGSKT

name          veins-Whole Leaves-MThadeo
resource_uniq 00-qA8ag5QLfKzWVGABJeTvVb
created       2013-07-10T15:12:35.595583
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T15:12:35.595583
uri           http://bovary.iplantcollaborative.org/data_service/00-qA8ag5QLfKzWVGABJeTvVb

name          viens-whole leaves-MThadeo2
resource_uniq 00-DL8Ak37C8fMUJWvRbysYME
created       2013-07-10T15:20:59.609727
owner         http://bovary.iplantcollaborative.org/data_service/00-rmL6PeBJoZ9phJbiRkWNVi
permission    private
ts            2013-07-10T15:20:59.609727
uri           http://bovary.iplantcollaborative.org/data_service/00-DL8Ak37C8fMUJWvRbysYME

name          Peabody whole leaves
resource_uniq 00-k9jgQE2YduuzcyYmgKCJEE
created       2013-07-18T17:00:27.317812
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2013-07-18T17:00:27.317812
uri           http://bovary.iplantcollaborative.org/data_service/00-k9jgQE2YduuzcyYmgKCJEE

name          Peabody partial leaves 20081209
resource_uniq 00-dacFioCMLCAfFx4SaqvHqQ
created       2013-07-18T17:07:18.379152
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2013-07-18T17:07:18.379152
uri           http://bovary.iplantcollaborative.org/data_service/00-dacFioCMLCAfFx4SaqvHqQ

name          test LAN
resource_uniq 00-WLsbBhEceQ7PYBNQHm8kjg
created       2013-07-18T17:09:19.285350
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2013-07-18T17:09:19.285350
uri           http://bovary.iplantcollaborative.org/data_service/00-WLsbBhEceQ7PYBNQHm8kjg

name          Peabody partial leaves 20090107
resource_uniq 00-MbAGvKJ4vsNtVjkm6UkFMU
created       2013-07-18T17:13:59.730496
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2013-07-18T17:16:36.540196
uri           http://bovary.iplantcollaborative.org/data_service/00-MbAGvKJ4vsNtVjkm6UkFMU

name          M. Conover cleared monocots macro
resource_uniq 00-Ao7hfwpSRmdEEd4MNgH4w6
created       2013-08-07T13:43:31.175121
owner         http://bovary.iplantcollaborative.org/data_service/00-zaiVzRh3hRGiP8SGYZAiVj
permission    private
ts            2013-08-08T07:10:50.073488
uri           http://bovary.iplantcollaborative.org/data_service/00-Ao7hfwpSRmdEEd4MNgH4w6

name          M. Conover cleared monocot leaves micro
resource_uniq 00-HNykmTfa6R5pWCdejVUgje
created       2013-08-08T08:25:37.719856
owner         http://bovary.iplantcollaborative.org/data_service/00-zaiVzRh3hRGiP8SGYZAiVj
permission    private
ts            2014-01-30T10:28:58.596941
uri           http://bovary.iplantcollaborative.org/data_service/00-HNykmTfa6R5pWCdejVUgje

name          testAVATOLimages
resource_uniq 00-A5Ds7UuP8D5WUezscZpxiB
created       2014-01-22T13:48:47.070133
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-01-22T13:49:21.737867
uri           http://bovary.iplantcollaborative.org/data_service/00-A5Ds7UuP8D5WUezscZpxiB

name          pictures of whiteboard
resource_uniq 00-EcM682FtpUSG2YAHCQrswa
created       2014-01-23T09:36:02.568692
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-01-23T09:36:02.568692
uri           http://bovary.iplantcollaborative.org/data_service/00-EcM682FtpUSG2YAHCQrswa

name          NCLC Smithsonian monocots
resource_uniq 00-5zr3TKitDd8qbZKaMA9F59
created       2014-04-29T08:23:39.250278
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-04-29T08:23:39.250278
uri           http://bovary.iplantcollaborative.org/data_service/00-5zr3TKitDd8qbZKaMA9F59

name          Canna indica leaf phytoliths
resource_uniq 00-YqTGgeS2dDc5rG8UKzhTdm
created       2014-04-30T07:28:25.093569
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:28:25.093569
uri           http://bovary.iplantcollaborative.org/data_service/00-YqTGgeS2dDc5rG8UKzhTdm

name          Cheilocostus speciosus leaf phytoliths
resource_uniq 00-3SSztPofFZGDTqqJgwgMcQ
created       2014-04-30T07:32:14.690779
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-06-11T11:43:44.017593
uri           http://bovary.iplantcollaborative.org/data_service/00-3SSztPofFZGDTqqJgwgMcQ

name          Phenakospermum guiannense leaf phytoliths
resource_uniq 00-Yvz72x8hLTMbYK8U9YKMYY
created       2014-04-30T07:35:36.505165
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-06-20T22:25:16.186342
uri           http://bovary.iplantcollaborative.org/data_service/00-Yvz72x8hLTMbYK8U9YKMYY

name          Tappeinochilus salomonensis leaf phytoliths
resource_uniq 00-BGG8zXXVvMhMUYqykLeMkk
created       2014-04-30T07:38:26.166096
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:38:26.166096
uri           http://bovary.iplantcollaborative.org/data_service/00-BGG8zXXVvMhMUYqykLeMkk

name          Costus laevis leaf phytoliths
resource_uniq 00-QAeYAmCmzA2cdak2CiQ9fm
created       2014-04-30T07:48:34.295672
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:48:34.295672
uri           http://bovary.iplantcollaborative.org/data_service/00-QAeYAmCmzA2cdak2CiQ9fm

name          Costus glabosus leaf phytoliths
resource_uniq 00-SakvhMzTCkDfdaXnR4fYwC
created       2014-04-30T07:54:15.952676
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:54:15.952676
uri           http://bovary.iplantcollaborative.org/data_service/00-SakvhMzTCkDfdaXnR4fYwC

name          Costus lacerus leaf phytoliths
resource_uniq 00-po4bav8e9SjqrkGoCCEs5N
created       2014-04-30T07:56:34.161809
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:56:34.161809
uri           http://bovary.iplantcollaborative.org/data_service/00-po4bav8e9SjqrkGoCCEs5N

name          Costus microcephalus leaf phytoliths
resource_uniq 00-GiutvSJDfBe7gUxBBqWJwa
created       2014-04-30T07:59:29.862241
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T07:59:29.862241
uri           http://bovary.iplantcollaborative.org/data_service/00-GiutvSJDfBe7gUxBBqWJwa

name          Costus glabosus leaf phytoliths
resource_uniq 00-hLrwvLVEFaPeu75QYAEXZR
created       2014-04-30T08:01:42.680802
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T08:01:42.680802
uri           http://bovary.iplantcollaborative.org/data_service/00-hLrwvLVEFaPeu75QYAEXZR

name          Costus arabicus leaf phytoliths
resource_uniq 00-UzYgegAyBozX7yqGYLQffc
created       2014-04-30T08:03:08.841678
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T08:03:08.841678
uri           http://bovary.iplantcollaborative.org/data_service/00-UzYgegAyBozX7yqGYLQffc

name          Strelitzia reginae leaf phytoliths
resource_uniq 00-Q2AMD7DVszmdruYotP9Kr8
created       2014-04-30T08:07:34.626823
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-06-16T13:51:51.571991
uri           http://bovary.iplantcollaborative.org/data_service/00-Q2AMD7DVszmdruYotP9Kr8

name          Ravenala madagascarensis leaf phytoliths
resource_uniq 00-P7hTCPatjUDwj2k4MtHBxV
created       2014-04-30T08:09:30.618222
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-04-30T08:09:30.618222
uri           http://bovary.iplantcollaborative.org/data_service/00-P7hTCPatjUDwj2k4MtHBxV

name          NCLC yale macro
resource_uniq 00-H2bHf5MbTNmY6EPnipqv8X
created       2014-04-30T12:30:52.060134
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-04-30T12:30:52.060134
uri           http://bovary.iplantcollaborative.org/data_service/00-H2bHf5MbTNmY6EPnipqv8X

name          Selena MCL leaves macro
resource_uniq 00-TFM5oDpWncdhiGysztc5W7
created       2014-04-30T13:10:09.245944
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-04-30T13:10:09.245944
uri           http://bovary.iplantcollaborative.org/data_service/00-TFM5oDpWncdhiGysztc5W7

name          Costaceae leaf phytoliths
resource_uniq 00-arPDjsdtDWDSmMdJXWnhSD
created       2014-05-12T07:22:18.103445
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-06-09T09:29:00.532433
uri           http://bovary.iplantcollaborative.org/data_service/00-arPDjsdtDWDSmMdJXWnhSD

name          Strelitzia reginae leaf phytoliths
resource_uniq 00-cwLRjtsba83dFbokfSm8GH
created       2014-05-12T07:39:27.989520
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-05-12T07:43:55.740617
uri           http://bovary.iplantcollaborative.org/data_service/00-cwLRjtsba83dFbokfSm8GH

name          Streliztiaceae leaf phytoliths
resource_uniq 00-ti5ucg6FhBoN92abEuvYHY
created       2014-05-12T07:45:41.753975
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-06-06T16:10:00.777914
uri           http://bovary.iplantcollaborative.org/data_service/00-ti5ucg6FhBoN92abEuvYHY

name          Costus phaeotrichus leaf phytoliths
resource_uniq 00-AhBntE8pJGzXWUYofYBzra
created       2014-05-12T07:48:37.730040
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-05-12T07:50:21.511604
uri           http://bovary.iplantcollaborative.org/data_service/00-AhBntE8pJGzXWUYofYBzra

name          Dimerocostus strobilaceos
resource_uniq 00-jgZgaaadJC3v9xcdJNJNoE
created       2014-05-12T07:51:10.514133
owner         http://bovary.iplantcollaborative.org/data_service/00-DTzYkGagvRt9awAXSmSb54
permission    private
ts            2014-05-12T07:52:47.341595
uri           http://bovary.iplantcollaborative.org/data_service/00-jgZgaaadJC3v9xcdJNJNoE

name          test123
resource_uniq 00-wfvAAXQXMZjQGqGUsn6AAn
created       2014-11-18T14:55:34.499539
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2014-11-18T14:58:27.233831
uri           http://bovary.iplantcollaborative.org/data_service/00-wfvAAXQXMZjQGqGUsn6AAn

name          test3
resource_uniq 00-zHacurdzS6gX7oPBfETVzf
created       2015-03-10T10:41:24.442165
owner         http://bovary.iplantcollaborative.org/data_service/00-HDJxvAzzNKS8uSaofcroNj
permission    private
ts            2015-03-10T10:41:24.442165
uri           http://bovary.iplantcollaborative.org/data_service/00-zHacurdzS6gX7oPBfETVzf

 */
