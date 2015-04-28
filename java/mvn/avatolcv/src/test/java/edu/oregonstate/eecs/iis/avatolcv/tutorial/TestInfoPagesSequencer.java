package edu.oregonstate.eecs.iis.avatolcv.tutorial;

import junit.framework.TestCase;

public class TestInfoPagesSequencer extends TestCase {

}
/*
*
*classdef TestInfoPagesSequencer < matlab.unittest.TestCase
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
        function testInfoPagesSequencerCASEForwardSimple(testCase)
            xmlFile = QuestionsXMLFile('tests/simpleTutorial.xml');
            info_pages = InfoPages(xmlFile.domNode);
            ips = InfoPageSequencer(info_pages);
            index = ips.nextInfoPageIndex;
            testCase.verifyEqual(index,1);
            ip1 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip1.id,'BREAD_INFO');
            
            % test valid answers moving forward
            ips.moveToNextPage();
            testCase.verifyEqual(ips.currentInfoPage.id,'BREAD_FAVORITES');
            testCase.verifyEqual(ips.nextInfoPageIndex,2);
            testCase.verifyFalse(ips.isAllInfoPagesShown());
            ip2 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip2.id,'BREAD_FAVORITES');
            
            ips.moveToNextPage();
            testCase.verifyEqual(ips.currentInfoPage.id,'WHITE_BREAD_INFO');
            testCase.verifyEqual(ips.nextInfoPageIndex,3);
            testCase.verifyFalse(ips.isAllInfoPagesShown());
            ip3 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip3.id,'WHITE_BREAD_INFO');
            
            ips.moveToNextPage();
            testCase.verifyEqual(ips.currentInfoPage.id,'RYE_BREAD_INFO');
            testCase.verifyEqual(ips.nextInfoPageIndex,4);
            testCase.verifyTrue(ips.isAllInfoPagesShown());
            ip4 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip4.id,'RYE_BREAD_INFO');
            
            % BACK UP  
            testCase.verifyTrue(ips.canBackUp());
            ips.backUp();
            testCase.verifyEqual(ips.currentInfoPage.id,'WHITE_BREAD_INFO');
            testCase.verifyEqual(ips.nextInfoPageIndex,3);
            testCase.verifyFalse(ips.isAllInfoPagesShown());
            ip3 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip3.id,'WHITE_BREAD_INFO');
            
            testCase.verifyTrue(ips.canBackUp());
            ips.backUp();
            testCase.verifyEqual(ips.currentInfoPage.id,'BREAD_FAVORITES');
            testCase.verifyEqual(ips.nextInfoPageIndex,2);
            testCase.verifyFalse(ips.isAllInfoPagesShown());
            ip2 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip2.id,'BREAD_FAVORITES');
            
            testCase.verifyTrue(ips.canBackUp());
            ips.backUp();
            testCase.verifyEqual(ips.currentInfoPage.id,'BREAD_INFO');
            testCase.verifyEqual(ips.nextInfoPageIndex,1);
            testCase.verifyFalse(ips.isAllInfoPagesShown());
            ip1 = ips.getCurrentInfoPage();
            testCase.verifyEqual(ip1.id,'BREAD_INFO');
            
            testCase.verifyFalse(ips.canBackUp());
        end
    end
end


*/