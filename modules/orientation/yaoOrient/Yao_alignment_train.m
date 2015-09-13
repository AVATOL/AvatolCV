function [test_predict_final, Model] = Yao_alignment_train(trainlist,pathAlignmentShipped,rotationOutputDir, annotation, rotatedOrigImageSuffix)

% -------------------------------------------------------------------------
%                       features of training dataset
% -------------------------------------------------------------------------
disp('Generating training features ...')
train_pos = [];
train_neg = [];
cellSize = 8;
for i = 1:1:length(trainlist)
    name = trainlist{i};
    I = imread([pathAlignmentShipped '\train\' name]);
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
    
    % 4 patches
    patch_ori      = [patch_left_rs patch_right_rs];
    patch_flip     = Yao_flip(patch_ori);
    patch_rot      = imrotate(patch_ori,180);
    patch_rot_flip = Yao_flip(patch_rot);
%     figure
%     subplot(2,2,1)
%     imshow(patch_ori)
%     subplot(2,2,2)
%     imshow(patch_flip)
%     subplot(2,2,3)
%     imshow(patch_rot)
%     subplot(2,2,4)
%     imshow(patch_rot_flip)
    % 4 HoG matrix
    hog_ori        = vl_hog(single(rgb2gray(patch_ori))     , cellSize);
    hog_flip       = vl_hog(single(rgb2gray(patch_flip))    , cellSize);
    hog_rot        = vl_hog(single(rgb2gray(patch_rot))     , cellSize);
    hog_rot_flip   = vl_hog(single(rgb2gray(patch_rot_flip)), cellSize);
    % 4 HoG vectors
    vhog_ori        = reshape(hog_ori,      1, 64*128*31); % related to patch size and Cellsize
    vhog_flip       = reshape(hog_flip,     1, 64*128*31);
    vhog_rot        = reshape(hog_rot,      1, 64*128*31);
    vhog_rot_flip   = reshape(hog_rot_flip, 1, 64*128*31);      
    
    % -------------------------------------------------------------------------
    % Here decide which hog feature vector(pos or neg) we would increase
    % -------------------------------------------------------------------------
    idx_loc = ismember(annotation(:,1),name); % idx_loc is the idx of this image
    label = cell2mat(annotation(idx_loc,2));
   
    if label == 1  % leaf base towards right ----> pos instance
        train_pos = vertcat(train_pos,vhog_ori); 
        train_pos = vertcat(train_pos,vhog_flip);
        train_neg = vertcat(train_neg,vhog_rot);
        train_neg = vertcat(train_neg,vhog_rot_flip);
    elseif label == -1
        train_neg = vertcat(train_neg,vhog_ori);
        train_neg = vertcat(train_neg,vhog_flip);
        train_pos = vertcat(train_pos,vhog_rot);
        train_pos = vertcat(train_pos,vhog_rot_flip);
    else
        fprintf('%dth annotation is not 1 or -1, program is terminated!\n',idx_loc);
        exit
    end
    
    fprintf('%dth training image has been processed.\n',i);
end
% -------------------------------------------------------------------------
%                          features of testing dataset
% -------------------------------------------------------------------------
disp('Generating testing features...')

% read the 'test' original images list into variable 'testlist'

% fileID = fopen(testImagesFile);
% testlist = textscan(fileID, '%s');
% fclose(fileID);
% testlist = testlist{1};
addpath(rotationOutputDir);
s = dir([rotationOutputDir '/*' rotatedOrigImageSuffix '.jpg']);
testlist = {s.name}';

test_instances = [];
for i = 1:1:length(testlist)
%     imgfullpath = testlist{i,1};
%     [~,name,ext] = fileparts(imgfullpath); 
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
    
    testpatch_ori      = [testpatch_left_rs testpatch_right_rs];
    testhog_ori        = vl_hog(single(rgb2gray(testpatch_ori)), cellSize);
    vtesthog_ori       = reshape(testhog_ori,1,64*128*31);
    
    test_instances = vertcat(test_instances,vtesthog_ori);
    fprintf('%dth testing image has been processed.\n',i);
end
% -------------------------------------------------------------------------
% combine training and testing intances matrix together to do PCA
% -------------------------------------------------------------------------
disp('Using PCA to reduce feature dimension...')
training_instance_matrix = vertcat(train_pos,train_neg);
pca_instance_mat = vertcat(training_instance_matrix,test_instances);
% [pc,score,latent,tsquare] = princomp(pca_instance_mat,'econ');
tic;coeff = pca(pca_instance_mat);toc; % 713*253952 mat takes 5 mins
pca_data = pca_instance_mat * coeff;

training_data = pca_data(1:size(training_instance_matrix,1),:);
test_data = pca_data(size(training_instance_matrix,1)+1:end,:);

% -------------------------------------------------------------------------
%                           Train SVM
% -------------------------------------------------------------------------
disp('Training SVM...')

pos_label = ones(size(train_pos,1),1);
neg_label = zeros(size(train_neg,1),1) - ones(size(train_neg,1),1);
training_label_vector = vertcat(pos_label,neg_label);

% -------------------------------------------------------------------------
%                        n fold cross-validation
% -------------------------------------------------------------------------
disp('n fold cross validation to choose the best SVM parameters-----------')
% tic
% folds = 10;   % using 10 folds
% 
% 
% % -------------------------------------------------------------------------
% %                        n fold use RBF Kernel
% % -------------------------------------------------------------------------
% % grid of parameters
% [C,gamma] = meshgrid(-5:2:15, -15:2:3);
% % grid search, and cross-validation
% cv_acc = zeros(numel(C),1);
% 
% for i=1:numel(C)
%     cv_acc(i) = svmtrain(training_label_vector, double(training_data), ...
%                     sprintf('-c %f -g %f -v %d', 2^C(i), 2^gamma(i), folds));
% end
% 
% % pair (C,gamma) with best accuracy
% [~,idx] = max(cv_acc);
% 
% % contour plot of paramter selection
% figure
% contour(C, gamma, reshape(cv_acc,size(C))), colorbar
% hold on
% plot(C(idx), gamma(idx), 'rx')
% text(C(idx), gamma(idx), sprintf('Acc = %.2f %%',cv_acc(idx)), ...
%     'HorizontalAlign','left', 'VerticalAlign','top')
% hold off
% xlabel('log_2(C)'), ylabel('log_2(\gamma)'), title('Cross-Validation Accuracy')
% 
% % now you can train you model using best_C and best_gamma
% best_C = 2^C(idx);
% best_gamma = 2^gamma(idx);
% 
% % Finally have the best C and best gamma, re-trained the model
% Model = svmtrain(training_label_vector, double(training_data), ...
%                     sprintf('-c %f -g %f', best_C, best_gamma));
% -------------------------------------------------------------------------



% -------------------------------------------------------------------------
%  n fold use linear Kernel   ---be careful take a long time !                 
% -------------------------------------------------------------------------
% just choose optimal C      
% C = -5:2:15;
% cv_acc = zeros(numel(C),1);
% tic
% for i=1:numel(C)
%     cv_acc(i) = svmtrain(training_label_vector, double(training_data), ...
%                     sprintf('-c %f -v %d -t 0', 2^C(i), folds));
% end
% toc
% % optimal C with best accuracy
% [~,idx] = max(cv_acc);
% best_C = 2^C(idx);

% Finally re-train the model with best_C
best_C = 2^-5;
% Model = svmtrain(training_label_vector, double(training_instance_matrix), ...
%                     sprintf('-c %f -t 0', best_C));
Model = svmtrain(training_label_vector, double(training_data), ...
                    sprintf('-c %f -t 0 -b 1', best_C));

% -------------------------------------------------------------------------
%                        test SVM
% -------------------------------------------------------------------------
disp('Testing SVM...')

random_label = zeros(length(testlist),1);
% [predict_final] = svmpredict(random_label,double(test_instances),Model);
[predict_final,accuracy,prob] = svmpredict(random_label,double(test_data),Model);
test_predict_final = [testlist num2cell(predict_final) num2cell(prob)];

end