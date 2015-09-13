function [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, rotatedOrigImageSuffix,rotatedMaskImageSuffix, alignOrigImageSuffix,aligndMaskImageSuffix,orientationOutputDir )
%       pathAlignmentShipped:       preshipped folder of alignment


close all;

%--------------------------------------------------------------------------
%         training alignment SVM using preshipped training leaves
%--------------------------------------------------------------------------

%% Read the csv annotation file into a cell array
if ispc
    annotationCSV = '\annotation.csv';
    preshippedTrainingSubPath = '\train\*leaf.jpg';
    slashStar = '\*';
    slash = '\';
else
    annotationCSV = '/annotation.csv';
    preshippedTrainingSubPath = '/train/*leaf.jpg';
    slashStar = '/*';
    slash = '/';
end
fileID = fopen([pathAlignmentShipped annotationCSV]);
C = textscan(fileID, '%s %f', 'Delimiter',',');
fclose(fileID);
annotation = horzcat(C{1,1},num2cell(C{1,2}));

%% Training the alignment SVM based on preshipped training sets
s = dir([pathAlignmentShipped preshippedTrainingSubPath]);
list_img = {s.name}';  % store the image names 
train_counts = round(length(list_img)*4/5); % using 80% of preshipped to train

% Just one-fold 
% n_fold = 1;
% eval_results = cell(n_fold,3); % C_cm, accuracy_all, accuracy_base

% for n = 1:1:n_fold
%     fprintf('This is the leaf alignment of %dth-fold.--------------------\n ',n);
    disp('Training the binary SVM for leaf orientation alignment...')
    list_idx = 1:1:length(list_img);  % store the image index
    train_idx = vl_colsubset(list_idx,train_counts);
    trainlist = list_img(train_idx',1);
    [test_predict, model] = Yao_alignment_train(trainlist,pathAlignmentShipped,rotationOutputDir, annotation, rotatedOrigImageSuffix);
    
    
    %% do results evaluation (if Ground Truth is known)
%     [C_cm, accuracy_all, accuracy_base] = Yao_evaluate_results(test_predict_final,trainlist);
%     eval_results{n,1} = C_cm;
%     eval_results{n,2} = accuracy_all;
%     eval_results{n,3} = accuracy_base;
% end

%% afterh the alignment prediction, rotate the leaf 180 degree if necessary
addpath(rotationOutputDir);
s = dir([rotationOutputDir slashStar rotatedMaskImageSuffix '.jpg']);
testlistMask = {s.name}'; % here I assume Original and Mask images are listed in the same order.

mkdir(orientationOutputDir);
for i = 1:1:size(test_predict,1)
    [name,orientation]= test_predict{i,1:2};
    fullName = [rotationOutputDir slash name];
    I = imread(fullName);
    nameMask = testlistMask{i};
    fullNameMask = [rotationOutputDir slash nameMask];
    Mask = imread(fullNameMask);
    if orientation == 1
        % do nothing, it is already cannonical orientation.
        I_rot = I;
        Mask_rot = Mask;
    elseif orientation == -1
        % leaf is in the opposite orientation, rotate is 180 degrees
        fullName = [rotationOutputDir slash name];
        I = imread(fullName);
        I_rot = imrotate(I,180);
        fullNameMask = [rotationOutputDir slash testlistMask{i}];
        Mask = imread(fullNameMask);
        Mask_rot = imrotate(Mask,180);
    else 
        fprintf('%dth test image has wrong alignment label, please check!\n',i); 
    end   
    imwrite(I_rot, [orientationOutputDir slash name(1:end-4) alignOrigImageSuffix fullName(end-3:end)]);
    imwrite(Mask_rot, [orientationOutputDir slash nameMask(1:end-4) aligndMaskImageSuffix fullNameMask(end-3:end)] );
end



end 