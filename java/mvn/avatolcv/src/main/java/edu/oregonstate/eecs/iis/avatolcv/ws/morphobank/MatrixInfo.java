package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.bisque.BisqueDataset;

public class MatrixInfo {
//{"ok":true,"projects":[{"projectID":"139","name":"AVATOL Test Project","matrices":[{"matrixID":"1423","name":"testing"}]},{"projectID":"700","name":"Crowdsourcing test project (mammals)","matrices":[{"matrixID":"1617","name":"Crowdsourcing Pilot Project"}]}]}
	private String ok;
	private List<MBProject> projects;
	
	public void setOk(String s){
		this.ok = s;
	}
	public String getOk(){
		return this.ok;
	}

	public void setProjects(List<MBProject> s){
		this.projects = s;
	}
	public List<MBProject> getProjects(){
		return this.projects;
	}
	
	public static class MBProject{
		//{"projectID":"139","name":"AVATOL Test Project","matrices":[{"matrixID":"1423","name":"testing"}]}
		private String projectID;
		private String name;
		private List<MBMatrix> matrices;
		
		public void setProjectID(String s){
			this.projectID = s;
		}
		public String getProjectID(){
			return this.projectID;
		}

		public void setName(String s){
			this.name = s;
		}
		public String getName(){
			return this.name;
		}

		public void setMatrices(List<MBMatrix> s){
			this.matrices = s;
		}
		public List<MBMatrix> getMatrices(){
			return this.matrices;
		}
	}
	public static class MBMatrix implements Comparable {
		//{"matrixID":"1423","name":"testing"}
		private String matrixID;
		private String name;
		private String projectID;
		public void setMatrixID(String s){
			this.matrixID = s;
		}
		public String getMatrixID(){
			return this.matrixID;
		}

		public void setName(String s){
			this.name = s;
		}
		public String getName(){
			return this.name;
		}
		@Override
	    public int compareTo(Object arg0) {
		    MBMatrix other = (MBMatrix)arg0;
	        String otherName = other.getName();
	        String thisName = this.getName();
	        return thisName.compareTo(otherName);
	    }
		public void setProjectID(String projectID){
		    this.projectID = projectID;
		}
		public String getProjectID(){
		    return this.projectID;
		}
	}
}
