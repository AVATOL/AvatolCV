package edu.oregonstate.eecs.iis.avatolcv.datasource;

public class ChoiceItem implements Comparable {
    private String name;
    private boolean isSelected;
    private Object backingObject;
    public ChoiceItem(String name, boolean isSelected, Object o){
        this.name = name;
        this.isSelected = isSelected;
        this.backingObject = o;
    }
    public Object getBackingObject(){
        return this.backingObject;
    }
    public String getName(){
        return this.name;
    }
    public boolean isSelected(){
        return this.isSelected;
    }
    @Override
    public int compareTo(Object arg0) {
        ChoiceItem other = (ChoiceItem)arg0;
        String otherName = other.getName();
        String thisName = this.getName();
        return thisName.compareTo(otherName);
    }
    
}
