function score(  testImagesFile, testImagesMaskFile, scoringOutputDir, pathLibsvm, pathVlfeat, trainingDataDir)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
flst=dir([trainingDataDir, 'training_*.txt']);
flst={flst.name};
flst =cell2mat(flst);
outputFileName = ['scoring' flst(9:length(flst)-4), '.txt'];

apex  = findstr(flst, 'apex');
display( apex);
if length(apex) == 0
    apex = 0;
else
    apex = 1;
end

display([trainingDataDir, flst])
fileID = fopen([trainingDataDir, flst]);
trainingData = textscan(fileID,'%s %s','Delimiter',',');
fclose(fileID);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 path(path,'.');
    % ----------------------------- Orientation Module ----------------------
    %% set up library path
    addpath(pathLibsvm);

    run(pathVlfeat) 
  
   
    Scoring_HOGSVM(trainingData, testImagesFile, scoringOutputDir, apex, outputFileName);
end

