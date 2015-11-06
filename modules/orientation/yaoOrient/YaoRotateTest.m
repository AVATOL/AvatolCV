function YaoRotateTest
    path(path,'.');
    % ----------------------------- Orientation Module ----------------------
    %inputImagesDir = '/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/segOutput';
    %inputImagesDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\segmentation\yaoSeg\segOutput';
    inputImagesDir = 'C:\avatol\git\avatol_cv\modules\segmentation\yaoSeg\segOutput';
    
    %testImagesFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testImagesFullPath.txt';
    %testImagesFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testImagesFullPath.txt';
    testImagesFile = 'C:\avatol\git\avatol_cv\modules\orientation\yaoOrient\testImagesFullPath.txt';
    
    %testImagesMaskFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testMaskImagesFullPath.txt';
    %testImagesMaskFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testMaskImagesFullPath.txt';
    testImagesMaskFile = 'C:\avatol\git\avatol_cv\modules\orientation\yaoOrient\testMaskImagesFullPath.txt';
    
    %rotationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    %rotationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\rotationOutput';
     
    %orientationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    %orientationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\orientationOutput';
    orientationOutputDir = 'C:\avatol\git\avatol_cv\modules\orientation\yaoOrient\orientationOutput';

	currentDir = pwd();
    cd(fileparts(mfilename('fullpath')));
	thisScriptDir = pwd();
	pathAlignmentShipped = sprintf('%s%s%s', thisScriptDir, filesep,'alignmentShipped');
	fprintf('pathAlignmentShipped derived as %s',pathAlignmentShipped);
    mkdir(orientationOutputDir);
    
    cd(orientationOutputDir);
    cd('..');
    parentDir = pwd();
    cd(currentDir);
    rotationOutputDir = sprintf('%s%s%s', parentDir,filesep,'rotationOutput');
    
    fprintf('rotationOutputDir created as %s',rotationOutputDir);
    
    Yao_Rotation(inputImagesDir, testImagesFile, testImagesMaskFile,'_rotatedOrig', '_rotatedMask', rotationOutputDir);
    
    % ----------------------------- Alignment Module ----------------------
    %pathLibsvm = 'G:\libsvm-3.18\libsvm-3.18'; % make sure you already installed and mexed LIBSVM/matlab
    pathLibsvm = '/Users/jedirvine/av/avatol_cv/modules/3rdParty/libsvm/libsvm-318'; 
    %addpath([pathLibsvm '\matlab']);
    addpath([pathLibsvm '/matlab']);
    %pathVlfeat = 'G:\vlfeat\vlfeat'; 
    pathVlfeat = '/Users/jedirvine/av/avatol_cv/modules/3rdParty/vlfeat/vlfeat-0.9.20'; 
    %run([pathVlfeat '\toolbox\vl_setup']) 
    run([pathVlfeat '/toolbox/vl_setup'])
    
    [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, '_rotatedOrig','_rotatedMask','_orientedOrig','_orientedMask',orientationOutputDir  );
end
