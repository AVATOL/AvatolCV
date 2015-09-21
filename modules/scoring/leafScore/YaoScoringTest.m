function YaoScoringTest
    path(path,'.');
    % ----------------------------- Orientation Module ----------------------
    %% set up library path
    pathLibsvm = 'G:\libsvm-3.18\libsvm-3.18'; % make sure you already installed and mexed LIBSVM/matlab
    addpath([pathLibsvm '\matlab']);
    pathVlfeat = 'G:\vlfeat\vlfeat'; 
    run([pathVlfeat '\toolbox\vl_setup']) 
    
    % scoring module
    inputImagesDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\orientation\yaoOrient\orientationOutput';
    testImagesFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\scoring\leafScore\testImagesFullPath.txt';
    testImagesMaskFile = 'C:\Users\collwe\Desktop\avatol_cv\modules\scoring\leafScore\testMaskImagesFullPath.txt';
    scoringOutputDir = 'C:\Users\collwe\Desktop\avatol_cv\modules\scoring\leafScore\scores';
    pathScoringShipped = 'C:\Users\collwe\Desktop\avatol_cv\modules\scoring\leafScore\scoringShipped';
    Yao_scoring_HOGSVM(pathScoringShipped, inputImagesDir, testImagesFile, scoringOutputDir);
end
