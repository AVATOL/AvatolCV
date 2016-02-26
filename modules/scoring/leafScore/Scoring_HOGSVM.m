function [] = Scoring_HOGSVM(trainingData, testImagesFile, scoringOutputDir, apex, outputFileName)

trainingFiles = trainingData{1,1};
trainingLabels = trainingData{1,2};
labelCells = repmat(trainingLabels,1,2)';
labelCells = labelCells(:);

close all;
%% In this part I have hard coded the leaf classes but we can change it
%% later so it reads from the file and assign it automatically
%%
labelMap = containers.Map();
labelMap2 = containers.Map('KeyType','uint32','ValueType','char');
lIndex = 1;
for i=1:1:length(labelCells)
    if ~isKey(labelMap, cell2mat(labelCells(i)))
        labelMap(cell2mat(labelCells(i))) = lIndex;
        labelMap2(uint32(lIndex)) = cell2mat(labelCells(i));
        lIndex = lIndex + 1;
    end
end

fileID = fopen(outputFileName, 'w');
labelValues=values(labelMap2);
fprintf(fileID, 'classNames=')
for i=1:1:length(labelValues)
    if i < length(labelValues)
        fprintf(fileID, [cell2mat(labelValues(1,i)), ','])
    else
        fprintf(fileID, [cell2mat(labelValues(1,i)), '\n'])
    end
end

fclose(fileID);



train_label = zeros(length(labelCells),1);
for i=1:1:length(labelCells)
    train_label(i) = labelMap(cell2mat(labelCells(i)));
end
% -------------------------------------------------------------------------
%                       features of training dataset
% -------------------------------------------------------------------------
disp('Generating training features -------------------------------------------------------')

train_instance_apex = [];
train_instance_base = [];
cellSize = 8;

for i = 1:1:length(trainingFiles)
    name = trainingFiles{i};
    I = imread(name);
    [height,width,~] = size(I);
    ratio = width/height;
    %figure;imshow(I);
     
    if ratio >= 1.95 
        patch_left       = I(:,round(1:height),:);
        patch_right      = I(:,round(width-height):end,:);
    else 
        patch_left       = I(round(height/2-width/4):round(height/2+width/4), 1:round(width/2),:);
        patch_right      = I(round(height/2-width/4):round(height/2+width/4), round(width/2):end,:);
    end  
  
    % resize all the patches to [512 512]
    patch_left_rs  = imresize(patch_left  ,[512 512]);
    patch_right_rs = imresize(patch_right ,[512 512]);
    patch_left_rs = rgb2gray(patch_left_rs);
    patch_right_rs = rgb2gray(patch_right_rs);
    patch_apex = patch_left_rs;
    patch_apex_flip = flipud(patch_left_rs);
    patch_base = patch_right_rs;
    patch_base_flip = flipud(patch_right_rs);
%     figure;subplot(1,2,1);imshow(patch_apex);subplot(1,2,2);imshow(patch_apex_flip)
    hog_apex = vl_hog(single((patch_apex)), cellSize);
    hog_apex_flip = vl_hog(single((patch_apex_flip)), cellSize);
    hog_base = vl_hog(single((patch_base)), cellSize);
    hog_base_flip = vl_hog(single((patch_base_flip)), cellSize);
    
    vhog_apex = reshape(hog_apex, 1, 64*64*31);
    vhog_apex_flip = reshape(hog_apex_flip, 1, 64*64*31);  
    vhog_base = reshape(hog_base, 1, 64*64*31);
    vhog_base_flip = reshape(hog_base_flip, 1, 64*64*31);
    
    train_instance_apex = vertcat(train_instance_apex, vhog_apex, vhog_apex_flip);
    train_instance_base = vertcat(train_instance_base, vhog_base, vhog_base_flip);
    fprintf('%dth training image has been processed.\n',i);
end

% -------------------------------------------------------------------------
%                          openning test ßdataset
% -------------------------------------------------------------------------

fileID = fopen(testImagesFile);
testlist = textscan(fileID, '%s');
fclose(fileID);
testlist = testlist{1};

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
    if ratio >= 1.95
        testpatch_left = I(:,round(1:height),:);
        testpatch_right = I(:,round(width-height):end,:);
    else 
        testpatch_left = I(round(height/2-width/4):round(height/2+width/4), 1:round(width/2),:);
        testpatch_right = I(round(height/2-width/4):round(height/2+width/4), round(width/2):end,:);
    end  
    
    testpatch_left_rs  = imresize(testpatch_left,[512 512]);
    testpatch_right_rs = imresize(testpatch_right,[512 512]);
    
    if apex == 1
        testpatch_apex      = testpatch_left_rs;
        testhog_apex        = vl_hog(single(rgb2gray(testpatch_apex)), cellSize);
        vtesthog_apex       = reshape(testhog_apex,1,64*64*31);
        test_instance_apex  = vertcat(test_instance_apex,vtesthog_apex);
    else
        testpatch_base      = testpatch_right_rs;
        testhog_base        = vl_hog(single(rgb2gray(testpatch_base)), cellSize);
        vtesthog_base       = reshape(testhog_base,1,64*64*31);
        test_instance_base  = vertcat(test_instance_base,vtesthog_base);
    end
    fprintf('%dth testing image has been processed.\n',i);
end

if apex == 1
    % combine training and testing intances matrix together to do PCA
    pca_apex_mat = vertcat(train_instance_apex, test_instance_apex);
    pc_apex = pca(pca_apex_mat);
    %[pc_apex,~,~,~] = princomp(pca_apex_mat,'econ');
    pca_apex_data = pca_apex_mat * pc_apex;
    train_data = pca_apex_data(1:size(train_instance_apex,1),:);
    test_data = pca_apex_data(size(train_instance_apex,1)+1:end,:);
else
    %base
    % combine training and testing intances matrix together to do PCA
    pca_base_mat = vertcat(train_instance_base, test_instance_base);
    pc_base = pca(pca_base_mat);
    %[pc_base,~,~,~] = princomp(pca_base_mat,'econ');
    pca_base_data = pca_base_mat * pc_base;
    train_data = pca_base_data(1:size(train_instance_base,1),:);
    test_data = pca_base_data(size(train_instance_base,1)+1:end,:);
end
% -------------------------------------------------------------------------
%                          SVM Training and testing
% -------------------------------------------------------------------------
disp('Training the SVM for leaf scoring-------------------------------------')

% WE only care if this is a character regarding the apex or base.
% Because the apex/base is dependent on the orientation output.
results = cell(length(testlist), 1);

test_label = zeros(length(testlist),1);
[prob,pred] = Yao_shape_SVM(length(labelMap),train_label,test_label,train_data,test_data);
display('SVM done.');
display('Generating Results ----');
results = horzcat(results, num2cell(pred), cell(length(testlist), 1),num2cell(prob));
for i=1:1:length(testlist)
    results{i,1} = cell2mat(testlist(i));
    results{i,2} =  labelMap2(cell2mat(results(i,2)));
end
mkdir(scoringOutputDir);


Yao_cell2csv(outputFileName,results,',');
movefile(outputFileName,scoringOutputDir);
display('Results Generated.');


end
