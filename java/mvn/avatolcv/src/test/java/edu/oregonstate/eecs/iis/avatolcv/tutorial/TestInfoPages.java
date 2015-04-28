package edu.oregonstate.eecs.iis.avatolcv.tutorial;

import junit.framework.TestCase;

public class TestInfoPages extends TestCase {

}
/*
 * classdef TestInfoPages < matlab.unittest.TestCase
   properties 
        OriginalPath
    end
    
    methods (TestMethodSetup)
        function addSrcToPath(testCase)
            testCase.OriginalPath = path;
            addpath(fullfile(pwd, '../tutorial'));
            addpath(fullfile(pwd, '..'));
        end
    end
    
    methods (TestMethodTeardown)
        function restorePath(testCase)
            path(testCase.OriginalPath);
        end
    end
    
    methods (Test)
        function testfindInfoPageById(testCase)
            xmlFile = QuestionsXMLFile('tests/simpleTutorial.xml');
            info_pages = InfoPages(xmlFile.domNode);
            info_page1 = info_pages.findInfoPageById('BREAD_INFO');
            testCase.verifyEqual(info_page1.id, 'BREAD_INFO');
            info_page2 = info_pages.findInfoPageById('BREAD_FAVORITES');
            testCase.verifyEqual(info_page2.id, 'BREAD_FAVORITES');
            info_page3 = info_pages.findInfoPageById('WHITE_BREAD_INFO');
            testCase.verifyEqual(info_page3.id, 'WHITE_BREAD_INFO');
            info_page4= info_pages.findInfoPageById('RYE_BREAD_INFO');
            testCase.verifyEqual(info_page4.id, 'RYE_BREAD_INFO');
            info_page5= info_pages.findInfoPageById('SHOULD_NOT_FIND_THIS');
            testCase.verifyEqual(info_page5.id, 'NULL');
        end
    end
    
end


 */
