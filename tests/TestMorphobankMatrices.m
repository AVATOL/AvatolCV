classdef TestMorphobankMatrices < matlab.unittest.TestCase
   properties 
        OriginalPath
    end
    
    methods (TestMethodSetup)
        function addSrcToPath(testCase)
            testCase.OriginalPath = path;
            addpath(fullfile(pwd, '../app'));
            addpath(fullfile(pwd, '..'));
        end
    end
    
    methods (TestMethodTeardown)
        function restorePath(testCase)
            path(testCase.OriginalPath);
        end
    end
    
    methods (Test)
        function testLoadMatrices(testCase)
            mm = MorphobankMatrices('../matrix_downloads');
            
            %xmlFile = QuestionsXMLFile('tests/simpleTutorial.xml');
            %info_pages = InfoPages(xmlFile.domNode);
            %info_page1 = info_pages.findInfoPageById('BREAD_INFO');
            %testCase.verifyEqual(info_page1.id, 'BREAD_INFO');
            
        end
    end
    
end