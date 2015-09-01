function YaoRotateTest
    path(path,'.');
%    inputImagesDir = '/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/segOutput';
    inputImagesDir = '/Users/jedirvine/av/avatol_cv/modules/segmentation/yaoSeg/segOutput';
%    testImagesFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testImagesFullPath.txt';
    testImagesFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testImagesFullPath.txt';
%    testImagesMaskFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testMaskImagesFullPath.txt';
    testImagesMaskFile = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/testMaskImagesFullPath.txt';
    rotatedOrigImageSuffix = '_rotatedOrig';
    rotatedMaskImageSuffix = '_rotatedMask';
%    rotationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    rotationOutputDir = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/orientationOutput';
    
    Yao_Rotation(inputImagesDir, testImagesFile, testImagesMaskFile,rotatedOrigImageSuffix, rotatedMaskImageSuffix, rotationOutputDir)
end
