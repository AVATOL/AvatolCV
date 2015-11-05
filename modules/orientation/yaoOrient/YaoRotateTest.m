function YaoRotateTest
    path(path,'.');
    % ----------------------------- Orientation Module ----------------------
    inputImagesDir = '/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/segOutput';
%    inputImagesDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\segmentation\yaoSeg\segOutput';
    testImagesFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testImagesFullPath.txt';
%    testImagesFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testImagesFullPath.txt';
    testImagesMaskFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testMaskImagesFullPath.txt';
%    testImagesMaskFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testMaskImagesFullPath.txt';
    rotationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
%    rotationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\rotationOutput';
    
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
    
    %pathAlignmentShipped = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\alignmentShipped';
    pathAlignmentShipped = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/alignmentShipped';
   
    %orientationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\orientationOutput';
    orientationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    
    [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, '_rotatedOrig','_rotatedMask','_alignOrig';,'_alignMask',orientationOutputDir  );

    
    
end
