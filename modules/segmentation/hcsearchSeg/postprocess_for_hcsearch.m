function postprocess_for_hcsearch( segmentation_output_dir, test_images_file, train_images_file, gt_images_file, time_bound )
%INVOKE_HCSEARCHSEG_SYSTEM Summary of this function goes here
%   Detailed explanation goes here

%% input sanity checks

% print inputs
fprintf('running postprocess_for_hcsearch...\n')
fprintf('segmentation_output_dir: "%s"\n', segmentation_output_dir)
fprintf('test_images_file: "%s"\n', test_images_file)
fprintf('train_images_file: "%s"\n', train_images_file)
fprintf('gt_images_file: "%s"\n', gt_images_file)
fprintf('time_bound: "%s"\n', time_bound)

% file exist checks
if ~exist(segmentation_output_dir, 'dir')
    error('segmentation_output_dir file does not exist: %s', segmentation_output_dir);
end
if ~exist(test_images_file, 'file')
    error('test_images_file file does not exist: %s', test_images_file);
end
if strcmp(train_images_file, '') == 0 && ~exist(train_images_file, 'file')
    error('train_images_file file does not exist: %s', train_images_file);
end
if strcmp(gt_images_file, '') == 0 && ~exist(gt_images_file, 'file')
    error('gt_images_file file does not exist: %s', gt_images_file);
end

time_bound = str2num(time_bound);

%% paths

fprintf('preprocessing...\n');

addpath('nematocyst');
addpath(genpath('nematocyst/avatol_system'));
addpath(genpath('nematocyst/preprocess'));
addpath(genpath('nematocyst/postprocess'));
addpath(genpath('nematocyst/scripts'));
addpath(genpath('nematocyst/utilities'));

addpath(genpath('nematocyst/external/libsvm/matlab'));
addpath(genpath('nematocyst/external/liblinear/matlab'));
run('nematocyst/external/vlfeat/toolbox/vl_setup.m');

fprintf('MATLAB PATHS:\n');
fprintf('%s\n\n', path);

%% postprocess
[root_dir, ~, ~] = fileparts(test_images_file);
results_path = fullfile(root_dir, 'segmentationTemp', 'hcSearchOutput', 'results');
preprocessed_path = fullfile(root_dir, 'segmentationTemp', 'imagesPreprocessed');
output_path = fullfile(root_dir, 'segmentedData');
base_path = fullfile('nematocyst/');

all_data_path = fullfile(preprocessed_path, 'allData.mat');
if ~exist(all_data_path, 'file')
    error('allData.m does not exist: %s', all_data_path);
end
load(all_data_path);

fprintf('results_path: %s\n', results_path);
fprintf('preprocessed_path: %s\n', preprocessed_path);
fprintf('base_path: %s\n', base_path);

% process results
allData = postprocess_avatol(allData, results_path, time_bound);
save(all_data_path, 'allData', '-v7.3');

% convert to segmentation masks and save
save_segmentation_masks(allData, output_path);

end

function save_segmentation_masks(allData, output_path)

SUFFIX = '_mask';
EXT = '.png';

for i = 1:length(allData)
    tstart = tic;
    fprintf('Saving image %d/%d...', i, length(allData));
    allDataInstance = allData{i};
    if isfield(allDataInstance, 'segLabels')
        telapsed = toc(tstart);
        fprintf('no need to save training image. (%.1fs)\n', telapsed);
        continue;
    end
    
    % make segmentation mask
    img = allDataInstance.inferImg;
    img(img == -1) = 0;
    img(img == 1) = 255;
    img = uint8(img);
    
    % save path
    full_path = allDataInstance.avatol.pathToMedia;
    [~, file_base, ~] = fileparts(full_path);
    save_path = fullfile(output_path, [file_base SUFFIX EXT]);
    imwrite(img, save_path);
    
    telapsed = toc(tstart);
    fprintf('done. (%.1fs)\n', telapsed);
end

end