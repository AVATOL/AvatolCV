package edu.oregonstate.eecs.iis.avatolcv.obsolete.jsontesting;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@JsonRootName(value = "results")
public class SunsetSunrise implements Serializable {
    public String sunrise;
    public String sunset;
    public String solar_noon;
    public String day_length;
    public String civil_twilight_begin;
    public String civil_twilight_end;
    public String nautical_twilight_begin;
    public String nautical_twilight_end;
    public String astronomical_twilight_begin;
    public String astronomical_twilight_end;
    
    
    public SunsetSunrise(){
    }
    public void setSunrise(String sunrise){
    	this.sunrise = sunrise;
    }
    public void setSunset(String sunset){
    	this.sunset = sunset;
    }
    public void setSolar_noon(String s){
    	this.solar_noon = s;
    }
    public void setDay_length(String s){
    	this.day_length = s;
    }
    public void setCivil_twilight_begin(String s){
    	this.civil_twilight_begin = s;
    }
    public void setCivil_twilight_end(String s){
    	this.civil_twilight_end = s;
    }
    public void setNautical_twilight_begin(String s){
    	this.nautical_twilight_begin = s;
    }
    public void setNautical_twilight_end(String s){
    	this.nautical_twilight_end = s;
    }
    public void setAstronomical_twilight_begin(String s){
    	this.astronomical_twilight_begin = s;
    }
    public void setAstronomical_twilight_end(String s){
    	this.astronomical_twilight_end = s;
    }
    
    // getters
    public String getSunrise(){
    	return this.sunrise;
    }
    public String getSunset(){
    	return this.sunset;
    }
    public String getSolar_noon( ){
    	return this.solar_noon;
    }
    public String getDay_length(){
    	return this.day_length;
    }
    public String getCivil_twilight_begin(){
    	return this.civil_twilight_begin;
    }
    public String getCivil_twilight_end(){
    	return this.civil_twilight_end;
    }
    public String getNautical_twilight_begin(){
    	return this.nautical_twilight_begin;
    }
    public String getNautical_twilight_end(){
    	return this.nautical_twilight_end;
    }
    public String getAstronomical_twilight_begin(){
    	return this.astronomical_twilight_begin;
    }
    public String getAstronomical_twilight_end(){
    	return this.astronomical_twilight_end;
    }
    /*
    public static class MyBeanMessageBodyReader implements MessageBodyReader<SunsetSunrise> {

    	@Override
    	public boolean isReadable(Class<?> type, Type genericType,
    			Annotation[] annotations, MediaType mediaType) {
    		return type == SunsetSunrise.class;
    	}

    	@Override
    	public SunsetSunrise readFrom(Class<SunsetSunrise> type,
    			Type genericType,
    			Annotation[] annotations, MediaType mediaType,
    			MultivaluedMap<String, String> httpHeaders,
    			InputStream entityStream)
    					throws IOException, WebApplicationException {

    		try {
    			JAXBContext jaxbContext = JAXBContext.newInstance(SunsetSunrise.class);
    			SunsetSunrise myBean = (SunsetSunrise) jaxbContext.createUnmarshaller()
    					.unmarshal(entityStream);
    			return myBean;
    		} 
    		catch (JAXBException jaxbException) {
    			throw new ProcessingException("Error deserializing a SunsetSunrise.",jaxbException);
    		}
    	}
	}*/
}
