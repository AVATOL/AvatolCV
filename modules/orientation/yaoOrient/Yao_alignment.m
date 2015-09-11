function [test_predict, model] = Yao_alignment(pathAlignmentShipped, rotationOutputDir, rotatedOrigImageSuffix)
%       pathAlignmentShipped:       preshipped folder of alignment


close all;

%--------------------------------------------------------------------------
%         training alignment SVM using preshipped training leaves
%--------------------------------------------------------------------------

%% Read the csv annotation file into a cell array
fileID = fopen([pathAlignmentShipped '\annotation.csv']);
C = textscan(fileID, '%s %f', 'Delimiter',',');
fclose(fileID);
annotation = horzcat(C{1,1},num2cell(C{1,2}));

%% Training the alignment SVM based on preshipped training sets
s = dir([pathAlignmentShipped '\train\*leaf.jpg']);
list_img = {s.name}';  % store the image names 
train_counts = round(length(list_img)*4/5); % using 80% of preshipped to train

% Just one-fold 
% n_fold = 1;
% eval_results = cell(n_fold,3); % C_cm, accuracy_all, accuracy_base

% for n = 1:1:n_fold
    fprintf('This is the leaf alignment of %dth-fold.--------------------\n ',n);
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



end 