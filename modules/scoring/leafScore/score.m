function score(  testImagesFile, testImagesMaskFile, scoringOutputDir, pathLibsvm, pathVlfeat, trainingDataDir)
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here
display('running step scoring.');
flst=dir([trainingDataDir, '/training_*.txt']);
flst={flst.name};
flst =cell2mat(flst);
outputFileName = ['scored' flst(9:length(flst)-4), '.txt'];

display(flst);

apex  = findstr(flst, 'apex');
display( apex);
if length(apex) == 0
    apex = 0;
else
    apex = 1;
end

display([trainingDataDir, filesep, flst])
fileID = fopen([trainingDataDir, filesep, flst]);
%trainingData = textscan(fileID,'%s %s','Delimiter',',');
trainingData = textscan(fileID,'%s %s %s %s %s','Delimiter',',');
fclose(fileID);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 path(path,'.');
    % ----------------------------- Orientation Module ----------------------
    %% set up library path
    addpath(pathLibsvm);

    run(pathVlfeat) 
  
   
    Scoring_HOGSVM(trainingData, testImagesFile, scoringOutputDir, apex, outputFileName);
    display('running completed for scoring');
    quit()
end

