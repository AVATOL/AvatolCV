function angle = Yao_hough_findlines(mask_name,img_name)
% hough transform test
% Read an image into the MATLAB workspace.

    I_mask = im2bw(imread(mask_name));
    I = imread(img_name);

    % finding the largest connected component
    CC = bwconncomp(I_mask);
    numPixels = cellfun(@numel,CC.PixelIdxList);
    [~,idx] = max(numPixels);
    % idx the is the index of the largest cc
    linear_idx = CC.PixelIdxList{idx};
    Mask = zeros(size(I_mask));
    Mask(linear_idx) = 1;
    I_gray = rgb2gray(I);
    I_gray(~Mask) = 255;

    BW = edge(I_gray,'canny');

    [H,theta,rho] = hough(BW);
    % Find the peaks in the Hough transform matrix, H, using the houghpeaks function.
    P = houghpeaks(H,50,'threshold',ceil(0.3*max(H(:))), 'NHoodSize', 2.*round(size(H+1)/100)-1 );
    % Superimpose a plot on the image of the transform that identifies the peaks.
    x = theta(P(:,2));
    y = rho(P(:,1));
%     plot(x,y,'s','color','black');

    % Find lines in the image using the houghlines function.
    lines = houghlines(BW,theta,rho,P,'FillGap',1,'MinLength',10);

    % here build the weighted histgram of orientation of detected lines
    angles = zeros(length(lines),2);
    for iag = 1:1:length(lines)
        angles(iag,1) = norm( lines(iag).point1 - lines(iag).point2 );
        angles(iag,2) = lines(iag).theta; % angle of hough is between -90 to 90. base angle is vertical line
    end
    
    bin_step = 180/36;  % degree of 5
    bin_range = -90:bin_step:90;
    [bincounts, binidx] = histc( angles(:,2),bin_range );
    [max_weights, angle_idx] = max(bincounts);
    angle = bin_range(angle_idx);
    
end
