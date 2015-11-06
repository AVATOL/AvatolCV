package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithmDependency;
import edu.oregonstate.eecs.iis.avatolcv.core.TestDataFilter;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.TestHomeWindow;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
   
    public static Test suite(){
        TestSuite suite= new TestSuite(); 
        suite.addTestSuite(TestAlgorithm.class); 
        suite.addTestSuite(TestAlgorithmDependency.class);
        suite.addTestSuite(TestAvatolCVFileSystem.class);
        suite.addTestSuite(TestJsonUtils.class);
        
        //suite.addTestSuite(TestMorphobankWS.class);
        //suite.addTestSuite(TestBisqueWS.class);
        //suite.addTestSuite(TestDataFilter.class); 
        
        suite.addTestSuite(TestHomeWindow.class);
        return suite;
    }
    //return new TestSuite( AppTest.class );
  

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        
    }
}
