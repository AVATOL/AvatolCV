package edu.oregonstate.eecs.iis.avatolcv.mb;

public class Taxon {
    private String id = null;
    private String name = null;
    public Taxon(String id, String name){
    	this.name = name;
    	this.id = id;
    }
    public String getId(){
    	return this.id;
    }
    public String getName(){
    	return this.name;
    }
}
