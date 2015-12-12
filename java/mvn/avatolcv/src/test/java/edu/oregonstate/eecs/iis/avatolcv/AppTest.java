package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithmDependency;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithmInput;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithmOutput;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestAlgorithmSequence;
import edu.oregonstate.eecs.iis.avatol.algorithm.TestRunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.core.TestDataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.TestEvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.TestNormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.TestScoringSetsKeySorter;
import edu.oregonstate.eecs.iis.avatolcv.core.TestTrueScoringSet;
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
        suite.addTestSuite(TestAlgorithmInput.class);
        suite.addTestSuite(TestAlgorithmOutput.class);
        suite.addTestSuite(TestAlgorithmSequence.class);
        suite.addTestSuite(TestRunConfigFile.class);
        suite.addTestSuite(TestAvatolCVFileSystem.class);
        suite.addTestSuite(TestJsonUtils.class);
        suite.addTestSuite(TestNormalizedImageInfos.class);
        suite.addTestSuite(TestEvaluationSet.class);
        suite.addTestSuite(TestTrueScoringSet.class);
        suite.addTestSuite(TestScoringSetsKeySorter.class);
        
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
