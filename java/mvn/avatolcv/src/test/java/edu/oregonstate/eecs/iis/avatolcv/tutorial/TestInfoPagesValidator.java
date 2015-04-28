package edu.oregonstate.eecs.iis.avatolcv.tutorial;

import junit.framework.TestCase;

public class TestInfoPagesValidator extends TestCase {

}
/*
*
*classdef TestInfoPagesValidator < matlab.unittest.TestCase
     properties 
        OriginalPath;
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
        
        function testValidateInfoPageCount(testCase)
            ipv = InfoPagesValidator();
            info_pages = {};
            testCase.verifyFalse(ipv.validateInfoPagesCount(info_pages));
            
            q = InfoPage( 'AAA', 'text1', 'BBB');
            info_pages = [ info_pages, q ];
            testCase.verifyTrue(ipv.validateInfoPagesCount(info_pages));
            
            q = InfoPage('BBB', 'text2', 'NO_MORE_PAGES');
            info_pages = [ info_pages, q ];
            testCase.verifyTrue(ipv.validateInfoPagesCount(info_pages));
        end
        
        function testValidateIdsUnique(testCase)
            ipv = InfoPagesValidator();
            info_pages = {};
            ip1 = InfoPage('AAA', 'foo', 'BBB');
            info_pages = [ info_pages, ip1 ];
            testCase.verifyTrue(isempty(ipv.getDuplicateIds(info_pages)));
            
            ip2 = InfoPage('BBB', 'bar', 'CCC');
            info_pages = [ info_pages, ip2 ];
            testCase.verifyTrue(isempty(ipv.getDuplicateIds(info_pages)));
            
            ip3 = InfoPage('CCC', 'sdfgfdfg', 'NO_MORE_PAGES');
            info_pages = [ info_pages, ip3 ];
            testCase.verifyTrue(isempty(ipv.getDuplicateIds(info_pages)));
            
            ip4 = InfoPage('BBB', 'jlklkjds', 'xyz');
            info_pages = [ info_pages, ip4 ];
            testCase.verifyFalse(isempty(ipv.getDuplicateIds(info_pages)));
        end
        
        function testRemoveMatchFromList(testCase)
            ipv = InfoPagesValidator();
            foo = { 'xx', 'y', 'z' };
            foo = ipv.removeMatchStringFromList('y', foo);
            testCase.verifyEqual('xx', char(foo(1)));
            testCase.verifyEqual('z', char(foo(2)));
            
            testCase.verifyTrue(length(foo) == 2);
            foo = ipv.removeMatchStringFromList('a', foo);
            testCase.verifyEqual('xx', char(foo(1)));
            testCase.verifyEqual('z', char(foo(2)));
            
            testCase.verifyTrue(length(foo) == 2);
            foo = ipv.removeMatchStringFromList('xx', foo);
            testCase.verifyEqual('z', char(foo(1)));
            testCase.verifyTrue(length(foo) == 1);
            
            foo = ipv.removeMatchStringFromList('z', foo);
            testCase.verifyTrue(length(foo) == 0);
        end
        
        function testValidateAllInfoPagesUsed(testCase)
            ipv = InfoPagesValidator();
            info_pages = {};
            ip1 = InfoPage('AAA', 'na', 'BBB');
            info_pages = [ info_pages, ip1 ];
            ip2 = InfoPage('BBB', 'na', 'CCC');
            info_pages = [ info_pages, ip2 ];
            ip3 = InfoPage('CCC', 'na', 'NO_MORE_PAGES');
            info_pages = [ info_pages, ip3 ];
            unused = ipv.getUnusedInfoPages(info_pages);
            testCase.verifyTrue(isempty(unused));
            
            ipv = QuestionsValidator();
            info_pages = {};
            ip1 = QQuestion('AAA', 'na', 'CCC');
            info_pages = [ info_pages, ip1 ];
            ip2 = QQuestion('UNUSED', 'na', 'BOGUS_LINK');
            info_pages = [ info_pages, ip2 ];
            ip3 = QQuestion('CCC', 'na', 'NO_MORE_PAGES');
            info_pages = [ info_pages, ip3 ];
            unused = ipv.getUnusedQuestions(info_pages);
            testCase.verifyFalse(isempty(unused));
        end
        
       
        function testGetImageMalformations(testCase)
            ipv = InfoPagesValidator();
            i1 = QImage('','someCaption');
            malformations = ipv.getImageMalformations(i1);
            problem = char(malformations(1));
            expectedProblem = 'image filename empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i2 = QImage('data/questionnaire/images/nonExistent.jpg','someCaption');
            malformations = ipv.getImageMalformations(i2);
            problem = char(malformations(1));
            expectedProblem = 'image filename does not exist: data/questionnaire/images/nonExistent.jpg';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i3 = QImage('data/questionnaire/images/bogusJPG.jpg','');
            malformations = ipv.getImageMalformations(i3);
            problem = char(malformations(1));
            expectedProblem = 'image caption empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            i4 = QImage('data/questionnaire/images/bogusJPG.jpg','someCaption');
            malformations = ipv.getImageMalformations(i4);
            testCase.verifyTrue(length(malformations) == 0);
        end
        
       
        function testGetQuestionMalformations(testCase)
            ipv = InfoPagesValidator();
            
            info_page = InfoPage('', 'someText','foo');
            malformations = ipv.getInfoPageMalformations(info_page);
            problem = char(malformations(1));
            expectedProblem = 'info_page id empty';
            testCase.verifyTrue(strcmp(problem,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            info_page = InfoPage('ID4', '', 'foo');
            malformations = ipv.getInfoPageMalformations(info_page);
            problem1 = char(malformations(1));
            expectedProblem = 'info_page text empty for ID4';
            testCase.verifyTrue(strcmp(problem1,expectedProblem));
            testCase.verifyTrue(length(malformations) == 1);
            
            %need more tests here to cover answers and images to make sure the errors percolate up
        end
    end
end


*/