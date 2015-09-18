package edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class WebServiceTest {
	public WebServiceTest() {
        super();
    }
 
    public static void main(String[] args) {
    	//String latLongString = "lat=36.7201600&lng=-4.4203400&date=today";
    	
        Client client = ClientBuilder.newClient();
         
        WebTarget webTarget = client.target("http://api.sunrise-sunset.org/");
        WebTarget resourceWebTarget = webTarget.path("json");
        //WebTarget helloworldWebTarget = resourceWebTarget.path("helloworld");
        WebTarget webTargetWithQueryParam = resourceWebTarget.queryParam("lat", "36.7201600").queryParam("lng", "-4.4203400").queryParam("date", "today");
       //lat=36.7201600&lng=-4.4203400&date=today  
        Invocation.Builder invocationBuilder =
        		webTargetWithQueryParam.request(MediaType.APPLICATION_JSON);
        //invocationBuilder.header("some-header", "true");
        //Invocation invocation = invocationBuilder.buildGet();
        //invocation.invoke()
        Response response = invocationBuilder.get();
        System.out.println(response.getStatus());
        String jsonString = response.readEntity(String.class);
        System.out.println(jsonString);
        //{"results":{"sunrise":"5:52:59 AM","sunset":"6:45:57 PM","solar_noon":"12:19:28 PM","day_length":"12:52:58","civil_twilight_begin":"5:26:43 AM","civil_twilight_end":"7:12:13 PM","nautical_twilight_begin":"4:55:31 AM","nautical_twilight_end":"7:43:26 PM","astronomical_twilight_begin":"4:23:19 AM","astronomical_twilight_end":"8:15:37 PM"},"status":"OK"}
        

        jsonString = JsonUtils.stripOffJsonContainerLayer(jsonString);
        //jsonString = jsonString.replaceFirst(",\\\"status\\\":\\\"OK\\\"}", "");
        System.out.println(jsonString);
        //{"sunrise":"5:52:59 AM","sunset":"6:45:57 PM","solar_noon":"12:19:28 PM","day_length":"12:52:58","civil_twilight_begin":"5:26:43 AM","civil_twilight_end":"7:12:13 PM","nautical_twilight_begin":"4:55:31 AM","nautical_twilight_end":"7:43:26 PM","astronomical_twilight_begin":"4:23:19 AM","astronomical_twilight_end":"8:15:37 PM"}
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        try {
        	SunsetSunrise ss = mapper.readValue(jsonString, SunsetSunrise.class);
        	System.out.println(ss.getAstronomical_twilight_begin());
        }
        catch(JsonParseException jpe){
        	System.out.println(jpe.getMessage());
        	jpe.printStackTrace();
        }
        catch(JsonMappingException jme){
        	System.out.println(jme.getMessage());
        	jme.printStackTrace();
        }

        catch(IOException ioe){
        	System.out.println(ioe.getMessage());
        	ioe.printStackTrace();
        }
        //SunsetSunrise ss = response.readEntity(SunsetSunrise.class);
        //System.out.println(ss.getSunset());
    }
  
}
