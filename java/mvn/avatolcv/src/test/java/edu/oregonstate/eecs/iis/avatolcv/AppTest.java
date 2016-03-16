package edu.oregonstate.eecs.iis.avatolcv;

import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithm;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithmDependency;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithmInput;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithmOutput;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestAlgorithmSequence;
import edu.oregonstate.eecs.iis.avatolcv.algorithm.TestRunConfigFile;
import edu.oregonstate.eecs.iis.avatolcv.core.TestDataFilter;
import edu.oregonstate.eecs.iis.avatolcv.core.TestEvaluationSet;
import edu.oregonstate.eecs.iis.avatolcv.core.TestNormalizedImageInfos;
import edu.oregonstate.eecs.iis.avatolcv.core.TestNormalizedTypeIDName;
import edu.oregonstate.eecs.iis.avatolcv.core.TestTrueScoringSet;
import edu.oregonstate.eecs.iis.avatolcv.datasource.UploadSessionTest;
import edu.oregonstate.eecs.iis.avatolcv.javafxui.TestHomeWindow;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBAnnotationParseTester;
import edu.oregonstate.eecs.iis.avatolcv.morphobank.MBTestMBDataSource;
import edu.oregonstate.eecs.iis.avatolcv.results.TestOutputImageSorter;
import edu.oregonstate.eecs.iis.avatolcv.results.TestResultsTableSortable;
import edu.oregonstate.eecs.iis.avatolcv.scoring.TestScoringSetsKeySorter;
import edu.oregonstate.eecs.iis.avatolcv.util.TestClassicSplitter;
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

        // avatolcv package
        suite.addTestSuite(TestAvatolCVFileSystem.class);
        suite.addTestSuite(TestJsonUtils.class);
        
        // algorithms package
        suite.addTestSuite(TestAlgorithm.class); 
        suite.addTestSuite(TestAlgorithmDependency.class);
        suite.addTestSuite(TestAlgorithmInput.class);
        suite.addTestSuite(TestAlgorithmOutput.class);
        suite.addTestSuite(TestAlgorithmSequence.class);
        suite.addTestSuite(TestRunConfigFile.class);
        
        // core
        suite.addTestSuite(TestDataFilter.class); 
        suite.addTestSuite(TestEvaluationSet.class);
        suite.addTestSuite(TestNormalizedImageInfos.class);
        suite.addTestSuite(TestNormalizedTypeIDName.class);
        suite.addTestSuite(TestTrueScoringSet.class);
        
        // datasource 
        suite.addTestSuite(UploadSessionTest.class);

        // morphobank
        suite.addTestSuite(MBAnnotationParseTester.class);
        suite.addTestSuite(MBTestMBDataSource.class);
        
        // results
        suite.addTestSuite(TestOutputImageSorter.class);
        suite.addTestSuite(TestResultsTableSortable.class);
        
        //scoring
        suite.addTestSuite(TestScoringSetsKeySorter.class);
       
        // util
        suite.addTestSuite(TestClassicSplitter.class);
        
        //suite.addTestSuite(TestMorphobankWS.class);
        //suite.addTestSuite(TestBisqueWS.class);
        
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
