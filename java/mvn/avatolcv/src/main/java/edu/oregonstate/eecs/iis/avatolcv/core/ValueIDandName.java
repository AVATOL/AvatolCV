package edu.oregonstate.eecs.iis.avatolcv.core;

public class ValueIDandName{
    private String id = null;
    private String name = null;
    public ValueIDandName(String id, String name){
        this.id = id;
        this.name = name;
    }
    public String getID(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String toString(){
        return id + "|" + name;
    }
}