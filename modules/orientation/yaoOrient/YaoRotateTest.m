function YaoRotateTest
    path(path,'.');
    % ----------------------------- Orientation Module ----------------------
%    inputImagesDir = '/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/segOutput';
    inputImagesDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\segmentation\yaoSeg\segOutput';
%    testImagesFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testImagesFullPath.txt';
    testImagesFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testImagesFullPath.txt';
%    testImagesMaskFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testMaskImagesFullPath.txt';
    testImagesMaskFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\testMaskImagesFullPath.txt';
    rotatedOrigImageSuffix = '_rotatedOrig';
    rotatedMaskImageSuffix = '_rotatedMask';
%    rotationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    rotationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\rotationOutput';
    
    Yao_Rotation(inputImagesDir, testImagesFile, testImagesMaskFile,rotatedOrigImageSuffix, rotatedMaskImageSuffix, rotationOutputDir);
    
    % ----------------------------- Alignment Module ----------------------
    pathLibsvm = 'G:\libsvm-3.18\libsvm-3.18'; % make sure you already installed and mexed LIBSVM/matlab
    addpath([pathLibsvm '\matlab']);
    pathVlfeat = 'G:\vlfeat\vlfeat'; 
    run([pathVlfeat '\toolbox\vl_setup'])
    
    pathAlignmentShipped = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\alignmentShipped';
    alignOrigImageSuffix = '_alignOrig';
    aligndMaskImageSuffix = '_alignMask';
    orientationOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\orientationOutput';
    
    [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, rotatedOrigImageSuffix,rotatedMaskImageSuffix,alignOrigImageSuffix,aligndMaskImageSuffix,orientationOutputDir  );

    
    
end
