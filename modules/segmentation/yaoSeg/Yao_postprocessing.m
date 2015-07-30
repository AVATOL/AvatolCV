function [] = Yao_postprocessing(segmentationOutputDir, testingImagesFile, croppedOrigImageSuffix, croppedMaskImageSuffix) 
%   1. Generate the fullsize mask images:  
%   2. Find the largest connected component of mask and then fit a
%   rectangle on it
%   3. crop the rectangle area on both raw image and mask image.
%
%   Example: 
%           Yao_postprocessing('lala','C:\Users\collwe\Desktop\avatol_cv\segmentationTest\testlistFullPath.txt','croppedOri','croppedMask')
%

close all

%% add the 'output' folder into path to get access to segmentation results
currentPath = pwd;
%parentPath = fileparts(currentPath);
addpath([currentPath '/output']);

%% read the 'test' images list into variable 'testlist'
fileID = fopen(testingImagesFile);
testlist = textscan(fileID, '%s');
fclose(fileID);
testlist = testlist{1};

%% creating the morphlogical structure element for 'erode' and 'dialate'
se = strel('disk',5);  % se = strel('disk', R, N) 

%% read the DARWIN output files and turn them into mask images
for i = 1:1:length(testlist)
    fullpath = testlist{i,1};
    [pathstr,name,ext] = fileparts(fullpath); 
    mask_name = [name '.pairwise.txt']; % CRF pairwise
    labelImage = textread(mask_name);
    Mask_0 = labelImage == 0;
    Mask = imdilate(Mask_0, se);
    Mask = imerode(Mask, se);
    
    % fill the holes in the mask
    Mask = imfill(Mask, 'holes');
    
    % write the Mask output, mask has not time 255 before imwrite.
    imwrite(Mask,[currentPath '/output/' name '_MASK' ext]);
    
    % find maximum CC of Mask, then generate the MaskCC(only the mask of 1 leaf)
    CC = bwconncomp(Mask);
    numPixels = cellfun(@numel,CC.PixelIdxList);
    [biggest,CCidx] = max(numPixels);
    MaskCC = im2bw(zeros(size(Mask)));
    MaskCC(CC.PixelIdxList{CCidx}) = 1;
    
    % find minimum BB that bound this mask and crop it on mask and raw image
    stats = regionprops(MaskCC,'BoundingBox');
    BB = num2cell(stats.BoundingBox);
    [x, y, l, h] = BB{:};
    x = floor(x); y = floor(y);
    Mask_crop = Mask(y:y+h,x:x+l);
    Image = imread(fullpath);
    Image_crop = Image(y:y+h,x:x+l);
    
    % write the Mask_crop and Image_crop to folder 'segmentationOutputDir'
%     mkdir(segmentationOutputDir, 'test'); % talk with Jed ?!!!!
    imwrite(Mask_crop,[segmentationOutputDir '/' name croppedMaskImageSuffix ext]);
    imwrite(Image_crop,[segmentationOutputDir '/' name croppedOrigImageSuffix ext]);
end

%% removethe 'output' folder into path to get access to segmentation results
rmpath([currentPath '/output'])

end
