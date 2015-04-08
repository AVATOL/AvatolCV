package edu.oregonstate.eecs.iis.avatolcv;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSClient;
import edu.oregonstate.eecs.iis.avatolcv.ws.MorphobankWSException;
import edu.oregonstate.eecs.iis.avatolcv.ws.morphobank.MatrixInfo.MBMatrix;
import junit.framework.TestCase;

public class TestMorphobankWS extends TestCase {

	public void testAuthenticate() {
		String username = "irvine@eecs.oregonstate.edu";
		String pw = "squonkmb";
		MorphobankWSClient wsClient = new MorphobankWSClient();
		try {
			wsClient.authenticate(username, pw);
			List<MBMatrix>matrices = wsClient.getMorphobankMatricesForUser(username,pw);
		}
		catch(MorphobankWSException me){
			System.out.println(me.getMessage());
			me.printStackTrace();
		}
	}

}
