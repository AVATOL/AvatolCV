function [] = Yao_annotation(train_path)
% Yao manully label test data set


%train_path = '/scratch/AVATOL/AVATOL_BOW/All_imgs';
addpath(train_path);

s = dir([train_path '/*leaf.jpg']);
testlist = {s.name}';

figure;
test_annotation = cell(length(testlist),2); % 1 is base area to right, -1 is apex area to right
test_annotation(:,1) = testlist;
for i = 1:1%:length(testlist)
    I = imread(testlist{i});
    [height,width,~] = size(I);
    ratio = width/height;
    if ratio >= 2 
        patch_left = I(:,round(1:height),:);
        patch_right = I(:,round(width-height):end,:);
    else 
        patch_left = I(round(height/2-width/4):round(height/2+width/4), 1:round(width/2),:);
        patch_right = I(round(height/2-width/4):round(height/2+width/4), round(width/2):end,:);
    end  
    clf;
    subplot(2,1,1);imshow(I);
    str = sprintf('%dth image: %s',i,testlist{i});
    title(str,'Interpreter','none');
    subplot(2,2,3);imshow(patch_left);
    subplot(2,2,4);imshow(patch_right);
    pause on
    prompt = 'What is the orientation of this leaf? \n Type "1" if Base towards right; type "-1" if Apex towards right.';
    result = input(prompt);
    test_annotation{i,2} = result;  
    pause off
end

Yao_cell2csv('Annotation_.csv',test_annotation,',');
end


