function [] = Yao_scoring_HOGSVM(pathScoringShipped, inputImagesDir, testImagesFile, scoringOutputDir)

close all;
train_path = pathScoringShipped;
addpath(genpath(train_path));

annotation = [];
if ispc
    load([train_path '\annotation_shape_234567']);
    s = dir([train_path '\train\*leaf.jpg']);
else
    load([train_path '/annotation_shape_234567']);
    s = dir([train_path '/train/*leaf.jpg']);
end

list_img = {s.name}';

fraction = 2/5;
numTrain = round(length(list_img)*fraction);
% numTest = length(list_img) - numTrain;

list_idx = 1:1:length(list_img);  % store the image index
train_idx = vl_colsubset(list_idx,numTrain);

= list_img(train_idx',1);

addpath(inputImagesDir);
fileID = fopen(testImagesFile);
testlist = textscan(fileID, '%s');
fclose(fileID);
testlist = testlist{1};

% -------------------------------------------------------------------------
%                       features of training dataset
% -------------------------------------------------------------------------
disp('Generating training features -------------------------------------------------------')

train_instance_apex = [];
train_instance_base = [];
cellSize = 8;

for i = 1:1:length(trainlist)
    name = trainlist{i};
    I = imread(name);
    [height,width,~] = size(I);
    ratio = width/height;
%     figure;imshow(I);
     
    if ratio >= 2 
        patch_left       = I(:,round(1:height),:);
        patch_right      = I(:,round(width-height):end,:);
    else 
        patch_left       = I(round(height/2-width/4):round(height/2+width/4), 1:round(width/2),:);
        patch_right      = I(round(height/2-width/4):round(height/2+width/4), round(width/2):end,:);
    end  
    
    % resize all the patches to [512 512]
    patch_left_rs  = imresize(patch_left  ,[512 512]);
    patch_right_rs = imresize(patch_right ,[512 512]);
    
    patch_apex = patch_left_rs;
    patch_apex_flip = flip(patch_left_rs);
    patch_base = patch_right_rs;
    patch_base_flip = flip(patch_right_rs);
%     figure;subplot(1,2,1);imshow(patch_apex);subplot(1,2,2);imshow(patch_apex_flip)
    hog_apex = vl_hog(single(rgb2gray(patch_apex)), cellSize);
    hog_apex_flip = vl_hog(single(rgb2gray(patch_apex_flip)), cellSize);
    hog_base = vl_hog(single(rgb2gray(patch_base)), cellSize);
    hog_base_flip = vl_hog(single(rgb2gray(patch_base_flip)), cellSize);
    
    vhog_apex = reshape(hog_apex, 1, 64*64*31);
    vhog_apex_flip = reshape(hog_apex_flip, 1, 64*64*31);  
    vhog_base = reshape(hog_base, 1, 64*64*31);
    vhog_base_flip = reshape(hog_base_flip, 1, 64*64*31);
    
    train_instance_apex = vertcat(train_instance_apex, vhog_apex, vhog_apex_flip);
    train_instance_base = vertcat(train_instance_base, vhog_base, vhog_base_flip);
    fprintf('%dth training image has been processed.\n',i);
end

% -------------------------------------------------------------------------
%                          features of testing dataset
% -------------------------------------------------------------------------
disp('Generating testing features----------------------------------------------------------')

test_instance_apex = [];
test_instance_base = [];
for i = 1:1:length(testlist)
    name = testlist{i};
    I = imread(name);
    [height,width,~] = size(I);
    ratio = width/height;
    
    if ratio >= 2 
        testpatch_left = I(:,round(1:height),:);
        testpatch_right = I(:,round(width-height):end,:);
    else 
        testpatch_left = I(round(height/2-width/4):round(height/2+width/4), 1:round(width/2),:);
        testpatch_right = I(round(height/2-width/4):round(height/2+width/4), round(width/2):end,:);
    end  
    
    testpatch_left_rs  = imresize(testpatch_left,[512 512]);
    testpatch_right_rs = imresize(testpatch_right,[512 512]);
    
    testpatch_apex      = testpatch_left_rs;
    testhog_apex        = vl_hog(single(rgb2gray(testpatch_apex)), cellSize);
    vtesthog_apex       = reshape(testhog_apex,1,64*64*31);
    test_instance_apex  = vertcat(test_instance_apex,vtesthog_apex);
    
    testpatch_base      = testpatch_right_rs;
    testhog_base        = vl_hog(single(rgb2gray(testpatch_base)), cellSize);
    vtesthog_base       = reshape(testhog_base,1,64*64*31);
    test_instance_base  = vertcat(test_instance_base,vtesthog_base);
    
    fprintf('%dth testing image has been processed.\n',i);
end

% combine training and testing intances matrix together to do PCA
pca_apex_mat = vertcat(train_instance_apex, test_instance_apex);
[pc_apex,~,~,~] = princomp(pca_apex_mat,'econ');
pca_apex_data = pca_apex_mat * pc_apex;
train_apex_data = pca_apex_data(1:size(train_instance_apex,1),:);
test_apex_data = pca_apex_data(size(train_instance_apex,1)+1:end,:);

pca_base_mat = vertcat(train_instance_base, test_instance_base);
[pc_base,~,~,~] = princomp(pca_base_mat,'econ');
pca_base_data = pca_base_mat * pc_base;
train_base_data = pca_base_data(1:size(train_instance_base,1),:);
test_base_data = pca_base_data(size(train_instance_base,1)+1:end,:);

% -------------------------------------------------------------------------
%                          SVM Training and testing
% -------------------------------------------------------------------------
disp('Training SVM----------------------------------------------------------')
disp('apex characters-----------------------------------------------------')

% eval_results = cell(2,size(annotation,2));
apexAngleResults = cell(length(testlist), 3 + 1);
apexCurvatureResults = cell(length(testlist), 3 + 1);
baseAngleResults = cell(length(testlist), 4 + 1); % base angle has 4 classes
baseCurvatureResults = cell(length(testlist), 3 + 1);

for ic = 4:5
    fprintf('%dth leaf shape character is under classification.\n',ic);
    label = cell2mat(annotation(:,ic));
%     train_label = label(train_idx',1);
    train_label = Yao_get_label(annotation,trainlist,ic);
    n = 2; % n is the rate of how many times training data are generate more. Right now just flip, so the data are doubled
    r=repmat(train_label,1,n)';
    train_label = r(:);
%     test_label = label(test_idx',1);
%     test_label = Yao_get_label(annotation,testlist,ic);
    test_label = zeros(length(testlist),1);
    [prob,pred] = Yao_shape_SVM(label,train_label,test_label,train_apex_data,test_apex_data);
    if ic == 4
        apexAngleResults = horzcat(num2cell(pred),num2cell(prob)); 
    else % ic == 5
        apexCurvatureResults = horzcat(num2cell(pred),num2cell(prob));
    end
%     eval_results{1,ic} = pred;
%     eval_results{2,ic} = prob;
end

disp('base characters------------------------------------------------------')
for ic = 6:7
    fprintf('%dth leaf shape character is under classification.\n',ic);
    label = cell2mat(annotation(:,ic));
%     train_label = label(train_idx',1);
    train_label = Yao_get_label(annotation,trainlist,ic);
    n = 2; % n is the rate of how many times training data are generate more. Right now just flip
    r=repmat(train_label,1,n)';
    train_label = r(:);
%     test_label = label(test_idx',1);
%     test_label = Yao_get_label(annotation,testlist,ic);
    test_label = zeros(length(testlist),1);
    if ic == 6
        label = [1;2;3;4]; % hardcode the # of base angles here.
    end
    [prob,pred] = Yao_shape_SVM(label,train_label,test_label,train_base_data,test_base_data);
    if ic == 6
        baseAngleResults = horzcat(num2cell(pred),num2cell(prob)); 
    else % ic == 5
        baseCurvatureResults = horzcat(num2cell(pred),num2cell(prob));
    end    
    
%     eval_results{1,ic} = pred;
%     eval_results{2,ic} = prob;
end

% -------------------------------------------------------------------------
%                    Store the scoring results into csv file
% -------------------------------------------------------------------------
mkdir(scoringOutputDir);
Yao_cell2csv('apexAngleResults.csv',apexAngleResults,',');
Yao_cell2csv('apexCurvatureResults.csv',apexCurvatureResults,',');
Yao_cell2csv('baseAngleResults.csv',baseAngleResults,',');
Yao_cell2csv('baseCurvatureResults.csv',baseCurvatureResults,',');

movefile('apexAngleResults.csv',scoringOutputDir);
movefile('apexCurvatureResults.csv',scoringOutputDir);
movefile('baseAngleResults.csv',scoringOutputDir);
movefile('baseCurvatureResults.csv',scoringOutputDir);



rmpath(inputImagesDir);
rmpath(genpath(train_path));
end