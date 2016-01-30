package edu.oregonstate.eecs.iis.avatolcv.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestClassicSplitter extends TestCase {
	public void testRegularSplit(){
		String s = "";
		String[] list1 = s.split("/");
		String s2 = "1234";
		String[] list2 = s2.split("_");
		int foo = 3;
		int bar = foo;
	}
	public void testSplitterNoMatchCases(){
		String[] list = ClassicSplitter.splitt("", ',');
		Assert.assertEquals(list.length, 1);
		Assert.assertEquals(list[0], "");
		
		list = ClassicSplitter.splitt("1", ',');
		Assert.assertEquals(list.length, 1);
		Assert.assertEquals(list[0], "1");
		
		list = ClassicSplitter.splitt("22", ',');
		Assert.assertEquals(list.length, 1);
		Assert.assertEquals(list[0], "22");

		list = ClassicSplitter.splitt("333", ',');
		Assert.assertEquals(list.length, 1);
		Assert.assertEquals(list[0], "333");

		list = ClassicSplitter.splitt("44 4", ',');
		Assert.assertEquals(list.length, 1);
		Assert.assertEquals(list[0], "44 4");
	}
	public void testSplitterMatchCases(){
		// ","
		String[] list = ClassicSplitter.splitt(",", ',');
		Assert.assertEquals(list.length, 2);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		
		// "1,"
		list = ClassicSplitter.splitt("1,", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"1");
		Assert.assertEquals(list[1],"");
		
		// ",1"
		list = ClassicSplitter.splitt(",1", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"1");

		// "22,"
		list = ClassicSplitter.splitt("22,", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"22");
		Assert.assertEquals(list[1],"");

		// ",22"
		list = ClassicSplitter.splitt(",22", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"22");
		
		// "333,"
		list = ClassicSplitter.splitt("333,", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"333");
		Assert.assertEquals(list[1],"");
		
		// ",333"
		list = ClassicSplitter.splitt(",333", ',');
		Assert.assertEquals(2, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"333");
		
		// ",,"
		list = ClassicSplitter.splitt(",,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		
		// ",1,"
		list = ClassicSplitter.splitt(",1,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"1");
		Assert.assertEquals(list[2],"");
		// ",22,"
		list = ClassicSplitter.splitt(",22,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"22");
		Assert.assertEquals(list[2],"");
		// ",333,"
		list = ClassicSplitter.splitt(",333,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"333");
		Assert.assertEquals(list[2],"");

		// "1,,"
		list = ClassicSplitter.splitt("1,,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"1");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		// "22,,"
		list = ClassicSplitter.splitt("22,,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"22");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		// "333,,"
		list = ClassicSplitter.splitt("333,,", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"333");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		
		// ",,1"
		list = ClassicSplitter.splitt(",,1", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"1");
		// ",,22"
		list = ClassicSplitter.splitt(",,22", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"22");
		// ",,333"
		list = ClassicSplitter.splitt(",,333", ',');
		Assert.assertEquals(3, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"333");
		// ",,,"
		list = ClassicSplitter.splitt(",,,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"");
		
		// ",1,,"
		list = ClassicSplitter.splitt(",1,,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"1");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"");
		// ",22,,"
		list = ClassicSplitter.splitt(",22,,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"22");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"");		
		// ",333,,"
		list = ClassicSplitter.splitt(",333,,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"333");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"");
		// ",,1,"
		list = ClassicSplitter.splitt(",,1,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"1");
		Assert.assertEquals(list[3],"");
		// ",,22,"
		list = ClassicSplitter.splitt(",,22,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"22");
		Assert.assertEquals(list[3],"");				
		// ",,333,"
		list = ClassicSplitter.splitt(",,333,", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"333");
		Assert.assertEquals(list[3],"");

		// ",,,1"
		list = ClassicSplitter.splitt(",,,1", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"1");
		// ",,,22"
		list = ClassicSplitter.splitt(",,,22", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"22");				
		// ",,,333"
		list = ClassicSplitter.splitt(",,,333", ',');
		Assert.assertEquals(4, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"333");
		// "1,22,333,4444,55555"
		list = ClassicSplitter.splitt("1,22,333,4444,55555", ',');
		Assert.assertEquals(5, list.length);
		Assert.assertEquals(list[0],"1");
		Assert.assertEquals(list[1],"22");
		Assert.assertEquals(list[2],"333");
		Assert.assertEquals(list[3],"4444");
		Assert.assertEquals(list[4],"55555");
		
		// ",22,,4444,"
		list = ClassicSplitter.splitt(",22,,4444,", ',');
		Assert.assertEquals(5, list.length);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"22");
		Assert.assertEquals(list[2],"");
		Assert.assertEquals(list[3],"4444");
		Assert.assertEquals(list[4],"");
		
	}
	public void testSplitterDot(){
		String[] list = ClassicSplitter.splitt(".", '.');
		Assert.assertEquals(list.length, 2);
		Assert.assertEquals(list[0],"");
		Assert.assertEquals(list[1],"");
		// "1.22.333.4444.55555"
		list = ClassicSplitter.splitt("1.22.333.4444.55555", '.');
		Assert.assertEquals(5, list.length);
		Assert.assertEquals(list[0],"1");
		Assert.assertEquals(list[1],"22");
		Assert.assertEquals(list[2],"333");
		Assert.assertEquals(list[3],"4444");
		Assert.assertEquals(list[4],"55555");
	}
	
}
