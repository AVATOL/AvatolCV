function preprocess_for_hcsearch( segmentation_output_dir, test_images_file, train_images_file, gt_images_file, shipped_training_images_file, third_party_path )
%INVOKE_HCSEARCHSEG_SYSTEM Summary of this function goes here
%   Detailed explanation goes here

%% input sanity checks

% print inputs
fprintf('running preprocess_for_hcsearch...\n')
fprintf('segmentation_output_dir: "%s"\n', segmentation_output_dir)
fprintf('test_images_file: "%s"\n', test_images_file)
fprintf('train_images_file: "%s"\n', train_images_file)
fprintf('gt_images_file: "%s"\n', gt_images_file)
fprintf('shipped_training_images_file: "%s"\n', shipped_training_images_file)
fprintf('third_party_path: "%s"\n', third_party_path)

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
if strcmp(shipped_training_images_file, '') == 0 && ~exist(shipped_training_images_file, 'file')
    error('shipped_training_images_file file does not exist: %s', shipped_training_images_file);
end

use_pretrained_model = strcmp(train_images_file, '') == 1 && strcmp(gt_images_file, '') == 1 && strcmp(shipped_training_images_file, '') == 1;

%% extract test file names

% read from file
fprintf('reading test_images_file...\n')
fid = fopen(test_images_file, 'r');
tline = fgetl(fid);
linesCell = cell(0,1);
while ischar(tline)
    linesCell{end+1, 1} = tline;
    tline = fgetl(fid);
end
fclose(fid);

% put into appropriate list format
scoring_list = cell(1, length(linesCell));
fprintf('printing test_images_file...\n')
for i = 1:length(linesCell)
   lineString = linesCell{i};
   scoring_list{i}.pathToMedia = lineString;
end

%% extract training file names

if ~use_pretrained_model
    % read from file
    fprintf('reading shipped_training_images_file...\n')
    fid = fopen(shipped_training_images_file, 'r');
    tline = fgetl(fid);
    linesCell = cell(0,1);
    while ischar(tline)
        linesCell{end+1, 1} = tline;
        tline = fgetl(fid);
    end
    fclose(fid);
    
    % put into appropriate list format
    training_list = cell(1, length(linesCell));
    fprintf('printing training_images_file...\n')
    for i = 1:length(linesCell)
       lineString = linesCell{i};
       training_list{i}.pathToMedia = [lineString '.jpg'];
       training_list{i}.pathToAnnotation = [lineString '.jpg'];
    end
else
    training_list = {};
end

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

%% preprocess
[root_dir, ~, ~] = fileparts(test_images_file);
dataset_path = fullfile(root_dir, 'segmentationTemp', 'images');
preprocessed_path = fullfile(root_dir, 'segmentationTemp', 'imagesPreprocessed');
base_path = fullfile('nematocyst/');

fprintf('dataset_path: %s\n', dataset_path);
fprintf('preprocessed_path: %s\n', preprocessed_path);
fprintf('base_path: %s\n', base_path);

color2label = containers.Map({0, 255}, {-1, 1});
[allData, ~] = preprocess_avatol(dataset_path, base_path, training_list, scoring_list, ...
    [], [], color2label, preprocessed_path);

end

