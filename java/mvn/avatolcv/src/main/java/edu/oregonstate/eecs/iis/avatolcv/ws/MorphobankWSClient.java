package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.Authentication;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharState;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBProject;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;

public class MorphobankWSClient {
	private String username = null;
	private String password = null;
	public boolean authenticate(String name, String pw) throws MorphobankWSException{
		/*
		 * http://morphobank.org/service.php/AVATOLCv/authenticateUser/username/irvine@eecs.oregonstate.edu/password/squonkmb

{"ok":true,"authenticated":1,"userId":"987","user":{"user_id":"987","user_name":"irvine@eecs.oregonstate.edu","fname":"Jed","lname":"Irvine","email":"irvine@eecs.oregonstate.edu"}}

		 */
		boolean result = false;
		Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/authenticateUser/username/" + name + "/password/" + pw;
	    WebTarget webTarget = client.target(url);
	    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
	        
	    Response response = invocationBuilder.get();
	    System.out.println(response.getStatus());
	    String jsonString = response.readEntity(String.class);
	     
	    System.out.println(jsonString);
	    ObjectMapper mapper = new ObjectMapper();
        try {
        	Authentication auth = mapper.readValue(jsonString, Authentication.class);
        	String authenticated = auth.authenticated;
        	if (authenticated.equals("1")){
        		result = true;
        		this.password = pw;
        		this.username = name;
        	}
        	else {
        		result = false;
        	}
        	System.out.println("userId " + auth.userId);
        	System.out.println("authed " + auth.authenticated);
        	System.out.println("ok     " + auth.ok);
        	System.out.println("user " + auth.user);
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
		return result;
	}
	public List<MBMatrix> getMorphobankMatricesForUser(){
		/*
		 * 
http://morphobank.org/service.php/AVATOLCv/getProjectsForUser/username/irvine@eecs.oregonstate.edu/password/squonkmb

{"ok":true,"projects":[{"projectID":"139","name":"AVATOL Test Project","matrices":[{"matrixID":"1423","name":"testing"}]},{"projectID":"700","name":"Crowdsourcing test project (mammals)","matrices":[{"matrixID":"1617","name":"Crowdsourcing Pilot Project"}]}]}

http://morphobank.org/service.php/AVATOLCv/getProjectsForUser/userID/987

		 */
		List<MBMatrix> matrices = new ArrayList<MBMatrix>();
		Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getProjectsForUser/username/" + username + "/password/" + password;
	    WebTarget webTarget = client.target(url);
	    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
	        
	    Response response = invocationBuilder.get();
	    System.out.println(response.getStatus());
	    String jsonString = response.readEntity(String.class);
	     
	    System.out.println(jsonString);
	    ObjectMapper mapper = new ObjectMapper();
        try {
        	MatrixInfo mi = mapper.readValue(jsonString, MatrixInfo.class);
        	List<MBProject> projects = mi.getProjects();
        	for (MBProject proj : projects){
        		List<MBMatrix> curMatrices = proj.getMatrices();
        		for (MBMatrix m : curMatrices){
        			System.out.println("adding matrix " + m.getName());
        			matrices.add(m);
        		}
        	}
        	
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
		return matrices;
	}
	public List<MBCharacter> getCharactersForMatrix(String matrixID){
		/*
		 * 
		 * http://morphobank.org/service.php/AVATOLCv/getCharactersForMatrix/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423

{"ok":true,"characters":[{"charID":"383114","charName":"Tube material!!!","charStates":[{"charStateID":"821248","charStateName":"mucus???","charStateNum":"0"},{"charStateID":"821249","charStateName":"chitinous","charStateNum":"1"},{"charStateID":"821250","charStateName":"calcareous","charStateNum":"2"}]},{"charID":"555957","charName":"meow","charStates":[{"charStateID":"1245629","charStateName":"New state","charStateNum":"0"},{"charStateID":"1245630","charStateName":"New state","charStateNum":"1"},{"charStateID":"1245631","charStateName":"New state","charStateNum":"2"}]},{"charID":"519541","charName":"test task.","charStates":[{"charStateID":"1157844","charStateName":"state 1","charStateNum":"0"},{"charStateID":"1157845","charStateName":"state 2","charStateNum":"1"}]}]}

		 */
		List<MBCharacter> characters = null;
		Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getCharactersForMatrix/username/" + username + "/password/" + password + "/matrixID/" + matrixID;
	    WebTarget webTarget = client.target(url);
	    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
	        
	    Response response = invocationBuilder.get();
	    System.out.println(response.getStatus());
	    String jsonString = response.readEntity(String.class);
	     
	    System.out.println(jsonString);
	    ObjectMapper mapper = new ObjectMapper();
        try {
        	CharacterInfo ci = mapper.readValue(jsonString, CharacterInfo.class);
        	characters = ci.getCharacters();
        	for (MBCharacter character : characters){
        		System.out.println("charId   : " + character.getCharID());
        		System.out.println("charName : " + character.getCharName());
        		List<MBCharState> charStates = character.getCharStates();
        		for (MBCharState state : charStates){
        			System.out.println("stateId " + state.getCharStateID() + " stateName " + state.getCharStateName() + " stateNum " + state.getCharStateNum());
        		}
        	}
        	
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
		return characters;
	}
	public List<MBTaxon> getTaxaForMatrix(String matrixID){
		/*
		 * 
http://morphobank.org/service.php/AVATOLCv/getTaxaForMatrix/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423

{"ok":true,"taxa":[{"taxonID":"72002","taxonName":"Aetobatus"},{"taxonID":"255564","taxonName":"Testicus testing"},{"taxonID":"71967","taxonName":""},{"taxonID":"138348","taxonName":"Dodecaceria sp."},{"taxonID":"138349","taxonName":"Thelepus cincinnatus"},{"taxonID":"138350","taxonName":"Amphitritides harpa"},{"taxonID":"138351","taxonName":"Marenzelleria viridis"},{"taxonID":"138352","taxonName":"Polydora giardi"},{"taxonID":"138353","taxonName":"Augeneriella alata"},{"taxonID":"138354","taxonName":"Fabricinuda sp."},{"taxonID":"138355","taxonName":"Manayunkia athalassia"},{"taxonID":"138356","taxonName":"Novofabria labrus"},{"taxonID":"138357","taxonName":"Amphicorina mobilis"},{"taxonID":"138358","taxonName":"Amphiglen terebro"},{"taxonID":"138359","taxonName":"Bispira crassicornis"},{"taxonID":"138360","taxonName":"Bispira manicata"},{"taxonID":"138361","taxonName":"Bispira porifera"},{"taxonID":"138362","taxonName":"Bispira serrata"},{"taxonID":"138363","taxonName":"Branchiomma nt"},{"taxonID":"138364","taxonName":"Branchiomma nsw"},{"taxonID":"138365","taxonName":"Branchiomma bairdi"},{"taxonID":"138366","taxonName":"Branchiomma nigromaculata"},{"taxonID":"138367","taxonName":"Calcisabella piloseta"},{"taxonID":"138368","taxonName":"Chone sp."},{"taxonID":"138369","taxonName":"Dialychone perkinsi"},{"taxonID":"138370","taxonName":"Euchone limnicola"},{"taxonID":"138371","taxonName":"Euchone sp."},{"taxonID":"138372","taxonName":"Fabrisabella vasculosa"},{"taxonID":"138373","taxonName":"Megalomma sp. a"},{"taxonID":"138374","taxonName":"Myxicola sp."},{"taxonID":"138375","taxonName":"Notaulax nsw"},{"taxonID":"138376","taxonName":"Notaulax qld"},{"taxonID":"138377","taxonName":"Pseudopotamilla nt"},{"taxonID":"138378","taxonName":"Pseudopotamilla qld"},{"taxonID":"138379","taxonName":"Pseudopotamilla monoculata"},{"taxonID":"138380","taxonName":"Pseudopotamilla nsw"},{"taxonID":"138381","taxonName":"Eudistylia vancouveri"},{"taxonID":"138382","taxonName":"Schizobranchia insignis"},{"taxonID":"138383","taxonName":"Sabella spallanzanii"},{"taxonID":"138384","taxonName":"Sabella pavonina"},{"taxonID":"138385","taxonName":"Sabellastarte australiensis"},{"taxonID":"138386","taxonName":"Sabellastarte nt"},{"taxonID":"138387","taxonName":"Sabellastate vic"},{"taxonID":"138388","taxonName":"Sabellastarte indonesia"},{"taxonID":"138389","taxonName":"Stylomma palmatum"},{"taxonID":"138390","taxonName":"Myriochele sp."},{"taxonID":"138391","taxonName":"Owenia qld"},{"taxonID":"138392","taxonName":"Owenia fusiformis"},{"taxonID":"138393","taxonName":"Crucigera zygophora"},{"taxonID":"138394","taxonName":"Ditrupa arietina"},{"taxonID":"138395","taxonName":"Hydroides sp."},{"taxonID":"138396","taxonName":"Pomatoceros triqueter"},{"taxonID":"138397","taxonName":"Spirobranchus lima"},{"taxonID":"138398","taxonName":"Protolaeospira tricostalis"},{"taxonID":"138688","taxonName":"A. gomesii"},{"taxonID":"128957","taxonName":"\u2020Abelisauridae"},{"taxonID":"129044","taxonName":"Afrotis"},{"taxonID":"138400","taxonName":"\u2020Protula tubularia"},{"taxonID":"138406","taxonName":"Osedax frankpressi"},{"taxonID":"138405","taxonName":"Lamellibrachia columna"},{"taxonID":"138411","taxonName":"Scoloplos armiger"},{"taxonID":"138407","taxonName":"Ridgeia piscesae"},{"taxonID":"138408","taxonName":"Riftia pachyptila"},{"taxonID":"129102","taxonName":"Aegotheles"},{"taxonID":"138399","taxonName":"Protula paliata"},{"taxonID":"138404","taxonName":"Sabellaria alveolata"}]}

		 */
		List<MBTaxon> taxa = null;
		Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getTaxaForMatrix/username/" + username + "/password/" + password + "/matrixID/" + matrixID;
	    WebTarget webTarget = client.target(url);
	    Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
	        
	    Response response = invocationBuilder.get();
	    System.out.println(response.getStatus());
	    String jsonString = response.readEntity(String.class);
	     
	    System.out.println(jsonString);
	    ObjectMapper mapper = new ObjectMapper();
        try {
        	TaxaInfo ti = mapper.readValue(jsonString, TaxaInfo.class);
        	taxa = ti.getTaxa();
        	for (MBTaxon taxon : taxa){
        		System.out.println("taxonId   : " + taxon.getTaxonID());
        		System.out.println("taxonName : " + taxon.getTaxonName());
        	}
        	
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
		return taxa;
	}
}
/*




http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/383114/taxonID/72002

{"ok":true,"charStates":[{"charStateID":"NPA"}]}

http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002

{"ok":true,"charStates":[{"charStateID":"1157845"},{"charStateID":"1157844"}]}


http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/383114/taxonID/71967

{"ok":true,"charStates":[{"charStateID":"inapplicable"}]}





http://morphobank.org/service.php/AVATOLCv/getMediaForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002

{"ok":true,"media":[{"mediaID":"284045","viewID":"6282"}]}



http://morphobank.org/service.php/AVATOLCv/getAnnotationsForCellMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002/mediaID/284045

{"ok":true,"annotations":[{"type":"polygon","points":[{"x":"31.891597158772733","y":"19.44466304661473"},{"x":"32.143102753789776","y":"26.495894651242097"},{"x":"39.436765009284095","y":"29.182078119671573"},{"x":"47.48494404982955","y":"32.875580388762096"},{"x":"51.76053916511931","y":"27.167440518349466"},{"x":"54.52710071030682","y":"18.437344245953675"},{"x":"50.50301119003409","y":"10.37879384066525"},{"x":"44.21537131460796","y":"6.013745704467354"}]},{"type":"polygon","points":[{"x":"46.73042726477841","y":"58.73009627239579"},{"x":"48.742472024914775","y":"55.37236693685895"},{"x":"51.00602238006818","y":"54.36504813619789"},{"x":"54.275595115289775","y":"53.69350226909053"},{"x":"56.79065106546023","y":"53.69350226909053"},{"x":"59.557212610647724","y":"54.70082106975158"},{"x":"61.82076296580114","y":"56.715458671073684"},{"x":"63.329796535903405","y":"58.73009627239579"},{"x":"64.33581891597159","y":"62.759371475040005"},{"x":"64.33581891597159","y":"66.78864667768421"},{"x":"63.58130213092045","y":"72.49678654809685"},{"x":"61.56925737078409","y":"78.54069935206317"},{"x":"58.048179040545456","y":"84.58461215602948"},{"x":"52.51505595017045","y":"85.59193095669055"},{"x":"45.47289928969318","y":"73.83987828231159"}]}]}

___what are width and height on her example



http://morphobank.org/service.php/AVATOLCv/getViewsForProject/username/irvine@eecs.oregonstate.edu/password/squonkmb/projectID/139

{"ok":true,"views":[{"viewID":"2236","name":"Cats"},{"viewID":"2369","name":"Cats2"},{"viewID":"4497","name":"test"},{"viewID":"4498","name":"test2"},{"viewID":"6280","name":"smith"},{"viewID":"6281","name":"wesson"},{"viewID":"6282","name":"clink"},{"viewID":"6856","name":"testing all the time, blah"}]}


http://morphobank.org/service.php/AVATOLCv/getMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045/versionID/thumbnail

{"ok":true,"error":"invalid version"}


http://morphobank.org/service.php/AVATOLCv/getMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045/version/thumbnail

{"ok":true,"media":"http:\/\/www.morphobank.org\/media\/morphobank3\/images\/2\/8\/4\/0\/53727_media_files_media_284045_thumbnail.jpg"}

http://www.morphobank.org/media/morphobank3/images/2/8/4/0/53727_media_files_media_284045_thumbnail.jpg

success!

http://morphobank.org/service.php/AVATOLCv/getMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045/version/large

{"ok":true,"media":"http:\/\/www.morphobank.org\/media\/morphobank3\/images\/2\/8\/4\/0\/32331_media_files_media_284045_large.jpg"}

http://www.morphobank.org/media/morphobank3/images/2/8/4/0/32331_media_files_media_284045_large.jpg

success!


http://morphobank.org/service.php/AVATOLCv/getMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045/version/small

{"ok":true,"media":"http:\/\/www.morphobank.org\/media\/morphobank3\/images\/2\/8\/4\/0\/11866_media_files_media_284045_small.jpg"}

http://www.morphobank.org/media/morphobank3/images/2/8/4/0/11866_media_files_media_284045_small.jpg

http://morphobank.org/service.php/AVATOLCv/getLargeSizeImage/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045

{"ok":false,"errors":["Invalid HTTP request method for this service"]}



http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/255564/stateID/1157845/npa/
char : test task
taxon: testicus testing

* it was NPA, I set this to state2 and it showed up as NPA, state2.  I set it to state1 and it shows up as NPA, state2, state1
also, what about provenance?


*/