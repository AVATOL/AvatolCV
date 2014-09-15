package edu.oregonstate.eecs.iis.avatolcv;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMatrix {

	@Test
	public void testBundle() {
		try {
		    MorphobankBundle m = new MorphobankBundle("/nfs/guille/tgd/users/irvine/matlabui/matrix_downloads/BOGUS");
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}

}
