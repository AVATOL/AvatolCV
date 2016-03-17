package edu.oregonstate.eecs.iis.avatolcv.ws;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import edu.oregonstate.eecs.iis.avatolcv.util.ClassicSplitter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfo.MBAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.AnnotationInfoForSinglePoint;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.Authentication;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CellMediaInfo.MBMediaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharStateInfo.MBCharStateValue;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.CharacterInfo.MBCharacter;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ErrorCheck;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MBRectangleAnnotation;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBProject;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MediaUrlInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.TaxaInfo.MBTaxon;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.ViewInfo.MBView;

public class MorphobankWSClientImpl implements MorphobankWSClient {
    private String username = "undefinedUser";
    private String password = "undefinedPassword";
    private static final String FILESEP = System.getProperty("file.separator");
    private boolean isAuthenticated = false;
    public boolean authenticate(String name, String pw) throws MorphobankWSException{
        /*
         * http://morphobank.org/service.php/AVATOLCv/authenticateUser/username/irvine@eecs.oregonstate.edu/password/squonkmb

{"ok":true,"authenticated":1,"userId":"987","user":{"user_id":"987","user_name":"irvine@eecs.oregonstate.edu","fname":"Jed","lname":"Irvine","email":"irvine@eecs.oregonstate.edu"}}

         */
        
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
                this.isAuthenticated = true;
                this.password = pw;
                this.username = name;
            }
            else {
                this.isAuthenticated = false;
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
        return this.isAuthenticated;
    }
    public List<MBMatrix> getMorphobankMatricesForUser() throws MorphobankWSException {
        String thisMethodName = "getMorphobankMatricesForUser";
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
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);
        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error listing matrices for user : " + ea.getErrorMessage());
        }
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            MatrixInfo mi = mapper.readValue(jsonString, MatrixInfo.class);
            List<MBProject> projects = mi.getProjects();
            for (MBProject proj : projects){
                String projectID = proj.getProjectID();
                List<MBMatrix> curMatrices = proj.getMatrices();
                for (MBMatrix m : curMatrices){
                    m.setProjectID(projectID);
                    matrices.add(m);
                }
            }
            
        }
        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return matrices;
    }
    public List<MBCharacter> getCharactersForMatrix(String matrixID)  throws MorphobankWSException{
        String thisMethodName = "getCharactersForMatrix";
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
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);
        
        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting characters for matrix : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            CharacterInfo ci = mapper.readValue(jsonString, CharacterInfo.class);
            characters = ci.getCharacters();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return characters;
    }
    public List<MBTaxon> getTaxaForMatrix(String matrixID)  throws MorphobankWSException {
        String thisMethodName = "getTaxaForMatrix";
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
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);
        
        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting taxa for matrix : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            TaxaInfo ti = mapper.readValue(jsonString, TaxaInfo.class);
            taxa = ti.getTaxa();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return taxa;
    }
    public List<MBCharStateValue> getCharStatesForCell(String matrixID, String charID, String taxonID)  throws MorphobankWSException {
        String thisMethodName = "getCharStatesForCell";
        /*
         * 
http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/383114/taxonID/72002

{"ok":true,"charStates":[{"charStateID":"NPA"}]}

http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002

{"ok":true,"charStates":[{"charStateID":"1157845"},{"charStateID":"1157844"}]}


http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/383114/taxonID/71967

{"ok":true,"charStates":[{"charStateID":"inapplicable"}]}

         */
    
    
        List<MBCharStateValue> charStateValues = null;
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getCharStatesForCell/username/" + username + "/password/" + password + "/matrixID/" + matrixID + "/characterID/" + charID + "/taxonID/" + taxonID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);
        
        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting char states for cell : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            CharStateInfo csi = mapper.readValue(jsonString, CharStateInfo.class);
            charStateValues = csi.getCharStates();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return charStateValues;
    }
    public List<MBMediaInfo> getMediaForCell(String matrixID, String charID, String taxonID)  throws MorphobankWSException {
        String thisMethodName = "getMediaForCell";
        /*
         * http://morphobank.org/service.php/AVATOLCv/getMediaForCell/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002

{"ok":true,"media":[{"mediaID":"284045","viewID":"6282"}]}
         */
        List<MBMediaInfo> mediaInfo = null;
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getMediaForCell/username/" + username + "/password/" + password + "/matrixID/" + matrixID + "/characterID/" + charID + "/taxonID/" + taxonID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);

        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting media for cell : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            CellMediaInfo cmi = mapper.readValue(jsonString, CellMediaInfo.class);
            mediaInfo = cmi.getMedia();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return mediaInfo;
    }
    public List<MBAnnotation> getAnnotationsForCellMedia(String matrixID, String charID, String taxonID, String mediaID)  throws MorphobankWSException {
        String thisMethodName = "getAnnotationsForCellMedia";
        /*
         * http://morphobank.org/service.php/AVATOLCv/getAnnotationsForCellMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/72002/mediaID/284045

NOTE - when there are multiplepoints, they are in an array:  
...so need to pull it into temporary obejct that can be mapped to.

{"ok":true,"annotations":[{"type":"polygon","points":[{"x":"31.891597158772733","y":"19.44466304661473"},{"x":"32.143102753789776","y":"26.495894651242097"},{"x":"39.436765009284095","y":"29.182078119671573"},{"x":"47.48494404982955","y":"32.875580388762096"},{"x":"51.76053916511931","y":"27.167440518349466"},{"x":"54.52710071030682","y":"18.437344245953675"},{"x":"50.50301119003409","y":"10.37879384066525"},{"x":"44.21537131460796","y":"6.013745704467354"}]},{"type":"polygon","points":[{"x":"46.73042726477841","y":"58.73009627239579"},{"x":"48.742472024914775","y":"55.37236693685895"},{"x":"51.00602238006818","y":"54.36504813619789"},{"x":"54.275595115289775","y":"53.69350226909053"},{"x":"56.79065106546023","y":"53.69350226909053"},{"x":"59.557212610647724","y":"54.70082106975158"},{"x":"61.82076296580114","y":"56.715458671073684"},{"x":"63.329796535903405","y":"58.73009627239579"},{"x":"64.33581891597159","y":"62.759371475040005"},{"x":"64.33581891597159","y":"66.78864667768421"},{"x":"63.58130213092045","y":"72.49678654809685"},{"x":"61.56925737078409","y":"78.54069935206317"},{"x":"58.048179040545456","y":"84.58461215602948"},{"x":"52.51505595017045","y":"85.59193095669055"},{"x":"45.47289928969318","y":"73.83987828231159"}]}]}

...When points is a single point, it is not in an array:
{"ok":true,"annotations":[{"type":"point","points":{"x":"18.8283475783476","y":"47.4198717948718"}}]} 
         *
         */
        List<MBAnnotation> annotations = new ArrayList<MBAnnotation>();
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getAnnotationsForCellMedia/username/" + username + "/password/" + password + "/matrixID/" + matrixID + "/characterID/" + charID + "/taxonID/" + taxonID + "/mediaID/" + mediaID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);

        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting annotations for cell media : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        
        /*
         * PROBLEM - annotations of type point, polygon and rectangle all have different body forms that don't map so a consistent object structure to support jackson mapping.  
         * So, I need to pre-process the json to tease out the each annotation string, then handle each separately
         */
        String justTypes = MorphobankAnnotationHelper.getJustTypes(jsonString);
        List<String> jsonAnnotationSections = MorphobankAnnotationHelper.splitTypes(justTypes);
        for (String jsonAnnotation : jsonAnnotationSections){
            if (AnnotationInfoForSinglePoint.isTypePoint(jsonAnnotation)){
                System.out.println("...point annotation...");
                MBAnnotation a = MorphobankAnnotationHelper.getMBAnnotationForSinglePointAnnotation(jsonAnnotation);
                annotations.add(a);
            }
            else if (MBRectangleAnnotation.isTypeRectangle(jsonAnnotation)) {
                System.out.println("...rectangle annotation...");
                MBAnnotation a = MorphobankAnnotationHelper.getMBAnnotationForRectangleAnnotation(jsonAnnotation);
                annotations.add(a);
            }
            else {
                System.out.println("...polygon annotation...");
                MBAnnotation a = MorphobankAnnotationHelper.getMBAnnotationForPolygonAnnotation(jsonAnnotation);
                annotations.add(a);
            }
        }
 
            
       
        return annotations;
    }
    
    public List<MBView> getViewsForProject(String projectID)  throws MorphobankWSException {
        String thisMethodName = "getViewsForProject";
        /*
         * 
http://morphobank.org/service.php/AVATOLCv/getViewsForProject/username/irvine@eecs.oregonstate.edu/password/squonkmb/projectID/139

{"ok":true,"views":[{"viewID":"2236","name":"Cats"},{"viewID":"2369","name":"Cats2"},{"viewID":"4497","name":"test"},{"viewID":"4498","name":"test2"},{"viewID":"6280","name":"smith"},{"viewID":"6281","name":"wesson"},{"viewID":"6282","name":"clink"},{"viewID":"6856","name":"testing all the time, blah"}]}
         */
        List<MBView> views = null;
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/getViewsForProject/username/" + username + "/password/" + password + "/projectID/" + projectID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
        }
        String jsonString = response.readEntity(String.class);

        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error getting views for project : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ViewInfo ai = mapper.readValue(jsonString, ViewInfo.class);
            views = ai.getViews();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return views;
    }
    /*
     * HACK - we pass in mediaFileName so that we can be compliant with ImageInfo, which expects a name, 
     * after I wrote it for the Bisque data source case.  As of this writing, for Morphobank images, 
     * mediaName will always be the empty string ("") since filename is not returned by the web service that gets the imageInfo.
     * Thus the resulting name will have two consecutive underscores.
     */
    public boolean downloadImageForMediaId(String dirToSaveTo, String mediaID, String mediaFileName, String type, String imageWidth)  throws MorphobankWSException {
        String thisMethodName = "downloadImageForMediaId";
        /*
         * 
http://morphobank.org/service.php/AVATOLCv/getMedia/username/irvine@eecs.oregonstate.edu/password/squonkmb/mediaID/284045/version/thumbnail

{"ok":true,"media":"http:\/\/www.morphobank.org\/media\/morphobank3\/images\/2\/8\/4\/0\/53727_media_files_media_284045_thumbnail.jpg"}

http://www.morphobank.org/media/morphobank3/images/2/8/4/0/53727_media_files_media_284045_thumbnail.jpg
         */
        boolean result = false;
        try {
            // first, use the sevice to get the url of the image
            Client client = ClientBuilder.newClient();
            String url = "http://morphobank.org/service.php/AVATOLCv/getMedia/username/" + username + "/password/" + password + "/mediaID/" + mediaID + "/version/" + type;
            WebTarget webTarget = client.target(url);
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
            
            Response response = invocationBuilder.get();
            int status = response.getStatus();
            if (status!= 200){
                String reason = response.getStatusInfo().getReasonPhrase();
                throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
            }
            String jsonString = response.readEntity(String.class);

            ErrorCheck ea = new ErrorCheck(jsonString);
            if (ea.isError()){
                throw new MorphobankWSException("Error getting image for mediaID : " + ea.getErrorMessage());
            }
            
            System.out.println(jsonString);
            ObjectMapper mapper = new ObjectMapper();
        
            MediaUrlInfo mui = mapper.readValue(jsonString, MediaUrlInfo.class);
            String mediaUrl = mui.getMedia();
            //http://www.morphobank.org/media/morphobank3/images/2/8/4/0/53727_media_files_media_284045_thumbnail.jpg
            String[] parts = ClassicSplitter.splitt(mediaUrl,'/');
            String filenameAtMB = parts[parts.length - 1];
            String[] filenameParts = ClassicSplitter.splitt(filenameAtMB,'.');
            String imageFileExtension = filenameParts[1].toLowerCase();
            
            //Now use the retrieved url to download the image
            /*
             *  Used this excellent reference for downloading a file: http://www.benchresources.net/jersey-2-x-web-service-for-uploadingdownloading-image-file-java-client/
             */
            
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.register(MultiPartFeature.class);
            client =  ClientBuilder.newClient(clientConfig);
            client.property("accept", "image/" + imageFileExtension);
            webTarget = client.target(mediaUrl);
            invocationBuilder = webTarget.request();
            response = invocationBuilder.get();
            status = response.getStatus();
            if (status!= 200){
                String reason = response.getStatusInfo().getReasonPhrase();
                throw new MorphobankWSException("error code " + status + " returned by " + thisMethodName + "... " + reason);
            }
            
 

            InputStream inputStream = response.readEntity(InputStream.class);
            String pathToSaveTo = dirToSaveTo + FILESEP + mediaID + "_" + mediaFileName + "_" + imageWidth + "." + imageFileExtension;// see comment above method re mediaFileName
            System.out.println("saving file to " + pathToSaveTo);
            OutputStream outputStream = new FileOutputStream(pathToSaveTo);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
        }

        catch(JsonParseException jpe){
            throw new MorphobankWSException("problem parsing response to " + thisMethodName + ".", jpe);
        }
        catch(JsonMappingException jme){
            throw new MorphobankWSException("problem mapping json response to " + thisMethodName + ".", jme);
        }
        catch(IOException ioe){
            throw new MorphobankWSException("io problem in objectMapper.readValue for " + thisMethodName + ".", ioe);
        }
        return result;
    }
    @Override
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }
    @Override
    public boolean addNewScore(String matrixID, String charID, String taxonID, String charStateID)
            throws MorphobankWSException {

        // http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/***/matrixID/23331/characterID/1820895/taxonID/770536/stateID/4876140
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/recordScore/username/" + username + "/password/" + password + "/matrixID/" + matrixID + "/characterID/" + charID + "/taxonID/" + taxonID  + "/stateID/" + charStateID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by recordScore... " + reason);
        }
        String jsonString = response.readEntity(String.class);

        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error during recordScore : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        return true;
       
    }
    @Override
    public boolean reviseScore(String matrixID, String cellID, String charStateID) throws MorphobankWSException {
        // http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/23331/cellID/58835485/stateID/4876140
        
        Client client = ClientBuilder.newClient();
        String url = "http://morphobank.org/service.php/AVATOLCv/recordScore/username/" + username + "/password/" + password + "/matrixID/" + matrixID + "/cellID/" + cellID + "/stateID/" + charStateID;
        WebTarget webTarget = client.target(url);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        
        Response response = invocationBuilder.get();
        int status = response.getStatus();
        if (status!= 200){
            String reason = response.getStatusInfo().getReasonPhrase();
            throw new MorphobankWSException("error code " + status + " returned by recordScore... " + reason);
        }
        String jsonString = response.readEntity(String.class);

        ErrorCheck ea = new ErrorCheck(jsonString);
        if (ea.isError()){
            throw new MorphobankWSException("Error during recordScore : " + ea.getErrorMessage());
        }
        
        System.out.println(jsonString);
        return true;
    }
}
/*


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



http://morphobank.org/service.php/AVATOLCv/recordScore/username/irvine@eecs.oregonstate.edu/password/squonkmb/matrixID/1423/characterID/519541/taxonID/255564/stateID/1157845/npa/
char : test task
taxon: testicus testing
 
* it was NPA, I set this to state2 and it showed up as NPA, state2.  I set it to state1 and it shows up as NPA, state2, state1
also, what about provenance?


*/