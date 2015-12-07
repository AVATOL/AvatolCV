function  orient( testImagesFile, testImagesMaskFile, orientationOutputDir, pathLibsvm, pathVlfeat)
    path(path,'.');
    % ----------------------------- Orientation Module ----------------------

	currentDir = pwd();
    cd(fileparts(mfilename('fullpath')));
	thisScriptDir = pwd();
	pathAlignmentShipped = sprintf('%s%s%s', thisScriptDir, filesep,'alignmentShipped');
	%in case we want to use a comiled version we need to give the absolute
	%path because the local directory will be ~/.mcrCache8.0/
    %pathAlignmentShipped = '/Users/jedirvine/av/avatol_cv/modules/orientation/yaoOrient/alignmentShipped'
    fprintf('pathAlignmentShipped derived as %s',pathAlignmentShipped);
    
    mkdir(orientationOutputDir);
    
    cd(orientationOutputDir);
    cd('..');
    parentDir = pwd();
    cd(currentDir);
    rotationOutputDir = sprintf('%s%s%s', parentDir,filesep,'rotationOutput');
    
    fprintf('rotationOutputDir created as %s',rotationOutputDir);
    inputImagesDir = '';
    Yao_Rotation(inputImagesDir, testImagesFile, testImagesMaskFile,'_rotatedOrig', '_rotatedMask', rotationOutputDir);
    
    % ----------------------------- Alignment Module ----------------------
  
    %addpath([pathLibsvm '\matlab']);
    addpath(pathLibsvm);
    run(pathVlfeat);
    [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, '_rotatedOrig','_rotatedMask','_orientedOrig','_orientedMask',orientationOutputDir  );
    display('running completed for oreinetation');
    quit force
end

