function  Yao_postprocessing(segmentationOutputDir, testingImagesFile, croppedOrigImageSuffix, croppedMaskImageSuffix) 

%   1. Generate the fullsize mask images:  
%   2. Find the largest connected component of mask and then fit a
%   rectangle on it
%   3. crop the rectangle area on both raw image and mask image.
%
%   Example: 
%           Yao_postprocessing('lala','C:\Users\collwe\Desktop\avatol_cv\segmentationTest\testlistFullPath.txt','croppedOri','croppedMask')
%

close all
%%fprintf('segmentationOutputDir is %s\n',segmentationOutputDir);
%% add the 'output' folder into path to get access to segmentation results
currentPath = pwd;
%%fprintf('currentPath as pwd %s\n',currentPath);
%parentPath = fileparts(currentPath);
addpath([currentPath '/output']);

%%fprintf('currentPath as pwd/output %s\n',currentPath);
%% read the 'test' images list into variable 'testlist'
fileID = fopen(testingImagesFile);


fprintf('made it past fopen\n');
testlist = textscan(fileID, '%s', 'delimiter', '\n');
fclose(fileID);
testlist = testlist{1};
%fprintf('testlist %s\n',testlist);
%% creating the morphlogical structure element for 'erode' and 'dialate'
se = strel('disk',5);  % se = strel('disk', R, N) 

%% read the DARWIN output files and turn them into mask images
for i = 1:1:length(testlist)
    fullpath = testlist{i,1};
    fprintf('working image %s\n',fullpath);
    [pathstr,name,ext] = fileparts(fullpath); 
    mask_name = [name '.pairwise.txt']; % CRF pairwise
    fprintf('now trying to load mask_name %s\n',mask_name);
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
    if CC.NumObjects == 0
        display('Skipping because the mask is all balck');
        continue;
    end
    display('connected components found');
    numPixels = cellfun(@numel,CC.PixelIdxList);
    [biggest,CCidx] = max(numPixels);
    MaskCC = im2bw(zeros(size(Mask)));
    MaskCC(CC.PixelIdxList{CCidx}) = 1;
    % find minimum BB that bound this mask and crop it on mask and raw image
    stats = regionprops(MaskCC,'BoundingBox');
    if size(stats, 1) == 0
        continue;
    end
    display('connected components found2');
    BB = num2cell(stats.BoundingBox);
    [x, y, l, h] = BB{:};
    x = floor(x); y = floor(y);
    if x == 0
        l = l -1;
        x = 1;
    end
    if y == 0
        h = h -1;
        y = 1;
    end
     display('connected components found3');
    Mask_crop = Mask(y:y+h,x:x+l);
    Image = imread(fullpath);
    Image_crop = Image(y:y+h,x:x+l,:);
     display('connected components found4');
    % write the Mask_crop and Image_crop to folder 'segmentationOutputDir'
%     mkdir(segmentationOutputDir, 'test'); % talk with Jed ?!!!!
    imwrite(Mask_crop,[segmentationOutputDir '/' name croppedMaskImageSuffix ext]);
    imwrite(Image_crop,[segmentationOutputDir '/' name croppedOrigImageSuffix ext]);
end

%% removethe 'output' folder into path to get access to segmentation results
rmpath([currentPath '/output'])
quit 

end
