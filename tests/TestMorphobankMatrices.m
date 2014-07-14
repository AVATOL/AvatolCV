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
            testCase.verifyEqual(length(mm.matrixDirNames),2);
            testCase.verifyEqual(char(mm.matrixDirNames(1)),'AVAToL Bat Skull Project_morphobank'); 
            testCase.verifyEqual(char(mm.matrixDirNames(2)),'BOGUS'); 
        end
    end
    
end