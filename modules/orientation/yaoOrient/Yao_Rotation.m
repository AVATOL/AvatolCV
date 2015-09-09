function [] = Yao_Rotation(inputImagesDir, testImagesFile, testImagesMaskFile,rotatedOrigImageSuffix, rotatedMaskImageSuffix, rotationOutputDir)
% This is the preprocessing step 'Rotation' for AVATOL_CV
% Usage:  input:   
%               inputImagesDir          : images directory to find segmentation output
%               testImagesFile          : fullpath list of images (original)
%               testImagesMaskFile      : fullpath list of images (mask)
%               rotatedOrigImageSuffix  : L1.png ->  L1rotated.png
%               rotatedMaskImageSuffix  : L1.png ->  L1rotatedMask.png 
%               rotationOutputDir       : images directory to store rotated images (original and mask)
%           output:
%               rotated images saved in 'rotationOutputDir'   

%% read the 'test' images list into variable 'testlist'
fileID = fopen(testImagesFile);
testlist = textscan(fileID, '%s');
fclose(fileID);
testlist = testlist{1};


%% read the 'test' images MASK list into variable 'testlistMask'
fileID = fopen(testImagesMaskFile);
testlistMask = textscan(fileID, '%s');
fclose(fileID);
testlistMask = testlistMask{1};

%% add the folder path which has the input images.(here assume imgs and masks are in same folder)
% firstpath = testlist{1,1};
% [folderpath,~,~] = fileparts(firstpath);
addpath(inputImagesDir);
mkdir(rotationOutputDir);
addpath(rotationOutputDir);
%% heuristically choose the Llenth-to-width threshold to choose rotation algorithms
cutRatio = 1.6;

%% start to rotate images iteratively
rotRecord = cell(length(testlist),2);
for i = 1:1:length(testlist)
    imgfullpath = testlist{i,1}; imgMaskfullpath = testlistMask{i,1};
    [~,name,ext] = fileparts(imgfullpath); [~,nameMask,extMask] = fileparts(imgMaskfullpath);
    img_name = [name ext];
    rotRecord{i,1} = img_name;
    mask_name = [nameMask extMask];
    I = imread(img_name);
    I_mask = im2bw(imread(mask_name)); % raw mask image, may have multiple CC
    
    % finding the largest connected component
    CC = bwconncomp(I_mask);
    numPixels = cellfun(@numel,CC.PixelIdxList);
    [~,idx] = max(numPixels);
    linear_idx = CC.PixelIdxList{idx};
    [x,y] = ind2sub(size(I_mask),linear_idx);
    Mask = zeros(size(I_mask));
    Mask(linear_idx) = 1;
    Mask = imfill(Mask, 'holes'); % fill the holes in Masks
    
    % find the minimum BB around Mask
    bb = Yao_minBoundingBox([y x]'); % bb including the coordinates of bb's 4 corners
    length_1 = norm([bb(1,1)-bb(1,2) bb(2,1)-bb(2,2)]);
    length_2 = norm([bb(1,2)-bb(1,3) bb(2,2)-bb(2,3)]);

    if length_1/length_2 >= cutRatio || length_2/length_1 >= cutRatio % shape is long rectangle
        % finding the main axis of ellipse and rotate
        STAT = regionprops(Mask,'Orientation');
        angle = STAT.Orientation;  % this angle is the angle between main axis of ellipse and x-axis
        Mask_rotate = imrotate(Mask, -angle); % imrotate is in counterclockwise
        I_rotate = imrotate(I,-angle);
        % record the rotation angles
        rotRecord{i,2} = -angle;
    else
        angle = Yao_hough_findlines(mask_name,img_name);
        I_rotate = imrotate(I,angle-90);
        Mask_rotate = imrotate(Mask, angle-90); % imrotate is in counterclockwise
        % record the rotation angles
        rotRecord{i,2} = angle-90;
    end
    mask_3_channel = repmat(Mask_rotate, [1 1 3]);
    I_rotate(~mask_3_channel) = 255;
    % find the bounding box position after rotate the image
    STAT_rotate = regionprops(Mask_rotate, 'BoundingBox');
    bb_rotate = round(STAT_rotate.BoundingBox); % bb_rotate = [x y width length]
    corner_x = bb_rotate(1);
    corner_y = bb_rotate(2);
    corner_width = bb_rotate(3);
    corner_height = bb_rotate(4);
    % I_rotate_leave = rgb2gray(I_rotate);
    [width, height, ~] = size(I_rotate);
    I_rotate_leave = I_rotate(corner_y:min(width,corner_y+corner_height),corner_x:min(height,corner_x+corner_width),:);
    Mask_rotate_leave = Mask_rotate(corner_y:min(width,corner_y+corner_height),corner_x:min(height,corner_x+corner_width));
    % imwrite(I_rotate, [train_path '/resizedIMAGE/' testlist{i,1} '_rotate.jpg']);

    imwrite(I_rotate_leave, [rotationOutputDir '\' name rotatedOrigImageSuffix ext]);
    imwrite(Mask_rotate_leave, [rotationOutputDir '\' nameMask rotatedMaskImageSuffix extMask]);     
    
end
rmpath(rotationOutputDir);
rmpath(folderpath);
end

