package edu.oregonstate.eecs.iis.avatolcv.ws.morphobank;


/*{"ok":true,
 * "authenticated":1,
 * "userId":"987",
 * "user":{"user_id":"987","user_name":"irvine@eecs.oregonstate.edu","fname":"Jed","lname":"Irvine","email":"irvine@eecs.oregonstate.edu"}}
*/
public class Authentication  {
    /**
	 * 
	 */
	//public static final long serialVersionUID = 1L;
	public String ok;
	public String authenticated;
	public String userId;
	public MBUser user;
	//try JsonNode? : http://stackoverflow.com/questions/8388656/java-json-jackson-nested-elements
	
    public void setOk(String s){
    	this.ok = s;
    }
    public void setAuthenticated(String s){
    	this.authenticated = s;
    }
    public void setUserId(String s){
    	this.userId = s;
    }
    public void setUser(MBUser s){
    	this.user = s;
    }
    
    public String getOk(){
    	return this.ok;
    }
    public String getAuthenticated(){
    	return this.authenticated;
    }
    public String getUserId(){
    	return this.userId;
    }
    public MBUser getUser(){
    	return this.user;
    }
    
    
    public static class MBUser {
        /**
    	 * 
    	 */
    	//public static final long serialVersionUID = 1L;
    	//{"user_id":"987","user_name":"irvine@eecs.oregonstate.edu","fname":"Jed","lname":"Irvine","email":"irvine@eecs.oregonstate.edu"}
    	public String user_id;
    	public String user_name;
    	public String fname;
    	public String lname;
    	public String email;
    	
    	public void setUser_id(String s){
    		this.user_id = s;
    	}
    	public void setUser_name(String s){
    		this.user_name = s;
    	}
    	public void setFname(String s){
    		this.fname = s;
    	}
    	public void setLname(String s){
    		this.lname = s;
    	}
    	public void setEmail(String s){
    		this.email = s;
    	}
    	
    	public String getUser_id(){
    		return this.user_id;
    	}
    	public String setUser_name(){
    		return this.user_name;
    	}
    	public String getFname(){
    		return this.fname;
    	}
    	public String getLname(){
    		return this.lname;
    	}
    	public String getEmail(){
    		return this.email;
    	}
    	
    }
   
    
}
//{"ok":true,"authenticated":1,"userId":"987","user":{"user_id":"987","user_name":"irvine@eecs.oregonstate.edu","fname":"Jed","lname":"Irvine","email":"irvine@eecs.oregonstate.edu"}}
