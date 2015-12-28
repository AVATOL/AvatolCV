package edu.oregonstate.eecs.iis.avatolcv.datasource;

import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;

public class ChoiceItem implements Comparable {
    private String name;
    private boolean isSelected;
    private Object backingObject;
    private boolean hasNativeType;
    private NormalizedKey nKey = null;
    public ChoiceItem(NormalizedKey nKey, boolean isSelected, boolean hasNativeType, Object o){
    	this.nKey = nKey;
        this.isSelected = isSelected;
        this.backingObject = o;
        this.hasNativeType = hasNativeType;
    }
    public Object getBackingObject(){
        return this.backingObject;
    }
   // public String getName(){
   //     return this.name;
   // }
    public boolean isSelected(){
        return this.isSelected;
    }
    public NormalizedKey getNormalizedKey(){
    	return this.nKey;
    }
    @Override
    public int compareTo(Object arg0) {
        ChoiceItem other = (ChoiceItem)arg0;
        NormalizedKey otherNKey = other.getNormalizedKey();
        return this.nKey.compareTo(otherNKey);
    }
    public boolean hasNativeType(){
    	return this.hasNativeType;
    }
    
}
