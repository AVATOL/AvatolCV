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
         function testAsoFirstLoadMatrices(testCase)
            mm = MorphobankMatrices('../matrix_downloads');
            sddXMLFile = XMLFile('../matrix_downloads/BOGUS/BOGUS_sdd.xml');
            domNode = sddXMLFile.domNode;
            matrixCharacters = MatrixCharacters(domNode,'BOGUS','../matrix_downloads/BOGUS');
            
            testCase.verifyEqual('../matrix_downloads/BOGUS/BOGUS_sdd.xml',mm.getSDDFilePath('BOGUS'));
        end
        function testLoadMatrices(testCase)
            mm = MorphobankMatrices('../matrix_downloads');
            testCase.verifyEqual(length(mm.matrixDirNames),2);
            testCase.verifyEqual(char(mm.matrixDirNames(1)),'AVAToL Bat Skull Project_morphobank'); 
            testCase.verifyEqual(char(mm.matrixDirNames(2)),'BOGUS'); 
            sddXMLFile = XMLFile('../matrix_downloads/AVAToL Bat Skull Project_morphobank/AVAToL Bat Skull Project_sdd.xml');
            domNode = sddXMLFile.domNode;
            matrixCharacters = MatrixCharacters(domNode,'AVAToL Bat Skull Project_morphobank','../matrix_downloads/AVAToL Bat Skull Project_morphobank');
            testCase.verifyEqual(matrixCharacters.characters(1).name,'GEN skull, dorsal margin, shape at juncture of braincase and rostrum in lateral view');
            testCase.verifyEqual(matrixCharacters.characters(2).name,'GEN skull, posterior extension of alveolar line and occiput, intersection');
            
            testCase.verifyEqual('../matrix_downloads/AVAToL Bat Skull Project_morphobank/AVAToL Bat Skull Project_sdd.xml',mm.getSDDFilePath('AVAToL Bat Skull Project_morphobank'));
        end
    end
    
end