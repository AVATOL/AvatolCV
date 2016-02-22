package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import junit.framework.TestCase;

public class UploadTest extends TestCase {

	public void testUpload(){
		BisqueWSClientImpl bisque = new BisqueWSClientImpl();
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
			 
	        //System.out.println("initial request to http://bovary.iplantcollaborative.org/auth_service/login");
	        //WebTarget webTargetInitLogin = client.target("http://bovary.iplantcollaborative.org/auth_service/");
	        System.out.println("initial request to http://bisque.iplantcollaborative.org/auth_service/login");
	        WebTarget webTargetInitLogin = client.target("http://bisque.iplantcollaborative.org/auth_service/");
	        
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
	        persistAuthenticationCookies(authenticationCookies);
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
}
