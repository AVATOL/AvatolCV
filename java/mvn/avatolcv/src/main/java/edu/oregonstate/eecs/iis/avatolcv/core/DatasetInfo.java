package edu.oregonstate.eecs.iis.avatolcv.core;


public class DatasetInfo implements Comparable {
    public static final String NO_CONTAINING_PROJECT_ID = "noProjectID";
    private String ID = null;
    private String name = null;
    private String projectID = null;
    
    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    @Override
    public int compareTo(Object arg0) {
        DatasetInfo other = (DatasetInfo)arg0;
        String otherName = other.getName();
        String thisName = this.getName();
        return thisName.compareTo(otherName);
    }
    
}
