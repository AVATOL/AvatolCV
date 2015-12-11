function translate_input( scoringOutputDir, trainingDataDir, testImagesFile )
%TRANSLATE_INPUT Summary of this function goes here
%   Detailed explanation goes here

%% constants
INPUT = 'input';
OUTPUT = 'output';
DETECTION_RESULTS = 'detection_results';
ANNOTATIONS = 'annotations';
SUMMARY_FILE = 'summary.txt';

LEGACY_FOLDER = 'legacy_format';

%% read runConfig_scoring.txt
%logdebug('runConfig_scoring.txt path: %s', runConfigScoringPath);
%[scoringOutputDir, trainingDataDir, testImagesFile] = ...
%    read_run_config_scoring_path_file(runConfigScoringPath);

logdebug('scoringOutputDir: %s', scoringOutputDir);
logdebug('trainingDataDir: %s', trainingDataDir);
logdebug('testImagesFile: %s', testImagesFile);

if ~exist(scoringOutputDir, 'dir')
    error('directory "%s" does not exist!', scoringOutputDir);
end
if ~exist(trainingDataDir, 'dir')
    error('directory "%s" does not exist!', trainingDataDir);
end
if ~exist(testImagesFile, 'dir')
    error('directory "%s" does not exist!', testImagesFile);
end

%% read in test images
testImages = read_test_images_file(testImagesFile);

%% read and parse training data
logdebug('trainingDataDir pattern: %s', [trainingDataDir filesep '*.txt']);
trainingDirList = dir([trainingDataDir filesep '*.txt']);
nCharacters = length(trainingDirList);

trainingDataList = cell(nCharacters, 1);
scoringConcernIDList = cell(nCharacters, 1);
scoringConcernNameList = cell(nCharacters, 1);

for i = 1:nCharacters
    logdebug('parsing training file %d', i);
    trainingFile = fullfile(trainingDataDir, trainingDirList(i).name);
    [trainingDataList{i}, scoringConcernIDList{i}, ...
        scoringConcernNameList{i}] = ...
        parse_training_images_file(trainingFile);
end

%% set up directories
[rootDir, ~, ~] = fileparts(runConfigScoringPath);
rootDir = fullfile(rootDir, LEGACY_FOLDER);
if ~exist(rootDir, 'dir')
    mkdir(rootDir);
end

inputDir = fullfile(rootDir, INPUT);
if ~exist(inputDir, 'dir')
    mkdir(inputDir);
end

outputDir = fullfile(rootDir, OUTPUT);
if ~exist(outputDir, 'dir')
    mkdir(outputDir);
end

detectionResultsDir = fullfile(rootDir, DETECTION_RESULTS);
if ~exist(detectionResultsDir, 'dir')
    mkdir(detectionResultsDir);
end

annotationsDir = fullfile(rootDir, ANNOTATIONS);
if ~exist(annotationsDir, 'dir')
    mkdir(annotationsDir);
end

%% generate summary file
summaryFile = fullfile(inputDir, SUMMARY_FILE);
generate_summary_file(summaryFile, INPUT, OUTPUT, DETECTION_RESULTS, ...
    trainingDataList, scoringConcernIDList, scoringConcernNameList, ...
    testImages);

%% generate annotation files
generate_annotation_files(trainingDataList, ...
    scoringConcernIDList, scoringConcernNameList, annotationsDir);

%% generate sorted_input_data_ files
for i = 1:nCharacters
    charId = scoringConcernIDList{i};
    fileName = ['sorted_input_data_' charId '_' ...
        scoringConcernNameList{i} '.txt'];
    filePath = fullfile(inputDir, fileName);
    generate_sorted_input_data_file(filePath, charId, trainingDataList{i}, ...
        testImages, ANNOTATIONS);
end

end % translate

function logdebug(msg, varargin)

%% set whether to print debug messages here
PRINT_DEBUG_MESSAGES = true;

if PRINT_DEBUG_MESSAGES
    fprintf(strcat(msg, '\n'), varargin{:});
end

end % logdebug

function [scoringOutputDir, trainingDataDir, testImagesFile] = ...
    read_run_config_scoring_path_file(runConfigScoringPath)

%% constants
DELIMITER = '=';

%% read file
fh = fopen(runConfigScoringPath, 'rt');
results = textscan(fh, '%s %s', 'Delimiter', DELIMITER);
fclose(fh);

%% error handling
if size(results, 1) ~= 1 || size(results, 2) ~= 2 ...
        || length(results{1}) ~= length(results{2})
    error('unexepcted parse format for "%s"', runConfigScoringPath);
end

%% get key=value
scoringOutputDir = '';
trainingDataDir = '';
testImagesFile = '';
for i = 1:length(results{1})
    key = results{1}{i};
    value = results{2}{i};
    
    if strcmp(key, 'scoringOutputDir') == 1
        scoringOutputDir = value;
    elseif strcmp(key, 'trainingDataDir') == 1
        trainingDataDir = value;
    elseif strcmp(key, 'testImagesFile') == 1
        testImagesFile = value;
    else
        error('unknown key "%s" when parsing "%s"', key, ...
            runConfigScoringPath);
    end
end

%% error handling
if strcmp(scoringOutputDir, '') == 1
    error('empty, missing or unable to parse key "scoringOutputDir" in "%s"', ...
        runConfigScoringPath);
elseif strcmp(trainingDataDir, '') == 1
    error('empty, missing or unable to parse key "trainingDataDir" in "%s"', ...
        runConfigScoringPath);
elseif strcmp(testImagesFile, '') == 1
    error('empty, missing or unable to parse key "testImagesFile" in "%s"', ...
        runConfigScoringPath);
end

end % read_run_config_scoring_path_file

function testImages = read_test_images_file(testImagesFile)

%% read file
fh = fopen(testImagesFile, 'rt');
results = textscan(fh, '%s', 'Delimiter', '\n');
fclose(fh);

%% normalize paths for platform (sanity check) - okay to remove
nImages = length(results{1});
testImages = cell(nImages, 1);
for i = 1:nImages
    testImages{i} = fullfile(results{1}{i});
end

end % read_test_images_file

function [trainingData, scoringConcernID, scoringConcernName] = ...
    parse_training_images_file(trainingFile)

%% constants
FILE_NAME_DELIMITER = '_';
TRAINING = 'training';
SCORING_CONCERN_TYPE_CHARACTER = 'character';

FILE_WITHIN_LINE_DELIMITER = ',';
EXPECTED_NUM_LINE_COMPONENTS = 5;

POINT = 'point';
POINT_COORDS_DELIMITER = '-';

%% parse file name
[~, fileName, ~] = fileparts(trainingFile);
fileNameComponents = strsplit(fileName, FILE_NAME_DELIMITER, ...
    'CollapseDelimiters', false);
if length(fileNameComponents) ~= 4
    error('unexpected # of components from parsing file name "%s"', ...
        fileName);
end

[trainingString, scoringConcernType, scoringConcernID, ...
    scoringConcernName] = deal(fileNameComponents{:});
if strcmp(trainingString, TRAINING) == 0 ...
        || strcmp(scoringConcernType, SCORING_CONCERN_TYPE_CHARACTER) == 0
    error('expected "%s" instead of "%s" when parse of file name "%s"', ...
        SCORING_CONCERN_TYPE_CHARACTER, scoringConcernType, fileName);
end

%% parse training file
fh = fopen(trainingFile, 'rt');
% count number of lines in file
nLines = 0;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end
    nLines = nLines + 1;
end
frewind(fh);

% set up data structure
trainingData = struct;

% parse each line
lineNum = 0;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end
    lineNum = lineNum + 1;
    
    %% parse line
    lineComponents = strsplit(tline, FILE_WITHIN_LINE_DELIMITER, ...
        'CollapseDelimiters', false);
    if length(lineComponents) ~= EXPECTED_NUM_LINE_COMPONENTS
        error('expected %d components from parsing line(%d) in file "%s"', ...
            EXPECTED_NUM_LINE_COMPONENTS, lineNum, fileName);
    end
    
    [imagePath, scoringConcernValue, pointCoords, trainTestConcern, ...
        trainTestValue] = deal(lineComponents{:});
    
    %% parse image path
    trainingData(lineNum).image = imagePath;
    
    %% parse scoring concern value
    components1 = strsplit(scoringConcernValue, ':');
    components2 = strsplit(components1{2}, '|');
    [trainingData(lineNum).charStateId, ...
        trainingData(lineNum).charStateName] = ...
        deal(components2{1}, components2{2});
    
    %% parse point coordinates
    if strcmp(pointCoords, '') == 0
        components1 = strsplit(pointCoords, ':');
        if strcmp(components1{1}, POINT) == 0
            error('expected point from parsing line(%d) in file "%s"', ...
                lineNum, fileName);
        end
        
        components2 = strsplit(components1{2}, POINT_COORDS_DELIMITER);
        [trainingData(lineNum).x, ...
            trainingData(lineNum).y] = ...
            deal(str2double(components2{1}), str2double(components2{2}));
    end
    
    %% parse traintest concern
    components1 = strsplit(trainTestConcern, ':');
    components2 = strsplit(components1{2}, '|');
    trainingData(lineNum).trainTestConcern = deal(components2{2});
    
    %% parse traintest value
    components1 = strsplit(trainTestValue, ':');
    components2 = strsplit(components1{2}, '|');
    [trainingData(lineNum).taxonId, ...
        trainingData(lineNum).taxonName] = ...
        deal(components2{1}, components2{2});
    
end

fclose(fh);

end % parse_training_images_file

function generate_summary_file(summaryFile, INPUT, OUTPUT, ...
    DETECTION_RESULTS, trainingDataList, scoringConcernIDList, ...
    scoringConcernNameList, testImages)

%% write to file
fh = fopen(summaryFile, 'wt');

%% write input, output, detection_results
fprintf(fh, '%s,%s\n', 'inputDir', INPUT);
fprintf(fh, '%s,%s\n', 'outputDir', OUTPUT);
fprintf(fh, '%s,%s\n', 'detectionResultsDir', DETECTION_RESULTS);

%% write characters
for i = 1:length(scoringConcernIDList)
    fprintf(fh, 'character,%s,%s\n', scoringConcernIDList{i}, ...
        scoringConcernNameList{i});
end

%% write media
mediaIdList = cell(0);
for charIndex = 1:length(trainingDataList)
    trainingData = trainingDataList{charIndex};
    for exampleIndex = 1:length(trainingData)
        mediaPath = trainingData(exampleIndex).image;
        [~, mediaId, mediaExt] = fileparts(mediaPath);
        
        if ~ismember(mediaId, mediaIdList)
            fprintf(fh, 'media,%s,%s,training\n', mediaId, [mediaId mediaExt]);
            mediaIdList{length(mediaIdList)+1} = mediaId;
        end
    end
end

for exampleIndex = 1:length(testImages)
    mediaPath = testImages{exampleIndex};
    [~, mediaId, mediaExt] = fileparts(mediaPath);

    if ~ismember(mediaId, mediaIdList)
        fprintf(fh, 'media,%s,%s,toScore\n', mediaId, [mediaId mediaExt]);
        mediaIdList{length(mediaIdList)+1} = mediaId;
    end
end

%% write states
statesIdList = cell(0);
for charIndex = 1:length(trainingDataList)
    trainingData = trainingDataList{charIndex};
    charId = scoringConcernIDList{charIndex};
    for exampleIndex = 1:length(trainingData)
        charStateId = trainingData(exampleIndex).charStateId;
        charStateName = trainingData(exampleIndex).charStateName;
        
        if ~ismember(charStateId, statesIdList)
            fprintf(fh, 'state,%s,%s,%s\n', charStateId, ...
                charStateName, charId);
            statesIdList{length(statesIdList)+1} = charStateId;
        end
    end
end

%% write taxon
taxonIdList = cell(0);
for charIndex = 1:length(trainingDataList)
    trainingData = trainingDataList{charIndex};
    for exampleIndex = 1:length(trainingData)
        taxonId = trainingData(exampleIndex).taxonId;
        taxonName = trainingData(exampleIndex).taxonName;
        
        if ~ismember(taxonId, taxonIdList)
            fprintf(fh, 'taxon,%s,%s\n', taxonId, taxonName);
            taxonIdList{length(taxonIdList)+1} = taxonId;
        end
    end
end

%% write view
fprintf(fh, 'view,v3540,Skull - ventral\n');

fclose(fh);

end % generate_summary_file

function generate_annotation_files(trainingDataList, ...
    scoringConcernIDList, scoringConcernNameList, annotationsDir)

%% constants
DELIM = ':';
COORDS_DELIM = ',';

%% write annotation files
for charIndex = 1:length(trainingDataList)
    trainingData = trainingDataList{charIndex};
    charId = scoringConcernIDList{charIndex};
    charName = scoringConcernNameList{charIndex};
    for exampleIndex = 1:length(trainingData)
        mediaPath = trainingData(exampleIndex).image;
        [~, mediaId, ~] = fileparts(mediaPath);
        
        x = trainingData(exampleIndex).x;
        y = trainingData(exampleIndex).y;
        charStateId = trainingData(exampleIndex).charStateId;
        charStateName = trainingData(exampleIndex).charStateName;
        
        %% write to file
        annotationFile = [mediaId '_' charId '.txt'];
        annotationPath = fullfile(annotationsDir, annotationFile);
        if exist(annotationPath, 'file')
            error('annotation file already exists (%s)', annotationPath);
        end
        
        fh = fopen(annotationPath, 'wt');
        
        fprintf(fh, [num2str(x) COORDS_DELIM ...
            num2str(y) DELIM ...
            charId DELIM ...
            charName DELIM ...
            charStateId DELIM ...
            charStateName '\n']);
        
        fclose(fh);
    end
end

end % generate_annotation_files

function generate_sorted_input_data_file(filePath, charId, trainingData, ...
    testImages, ANNOTATIONS)

%% constants
DELIM = '|';

%% write to file
fh = fopen(filePath, 'wt');

%% write training data
for i = 1:length(trainingData)
    [~, mediaId, mediaExt] = fileparts(trainingData(i).image);
    media = [mediaId mediaExt];
    charStateId = trainingData(i).charStateId;
    charStateName = trainingData(i).charStateName;
    annotationPath = [ANNOTATIONS filesep mediaId '_' charId '.txt'];
    taxonId = trainingData(i).taxonId;
    lineNum = '1'; %TODO
    
    fprintf(fh, ['training_data' DELIM ...
        media DELIM ...
        charStateId DELIM ...
        charStateName DELIM ...
        annotationPath DELIM ...
        taxonId DELIM ...
        lineNum '\n']);
end

%% write test data
for i = 1:length(testImages)
    [~, mediaId, mediaExt] = fileparts(testImages{i});
    media = [mediaId mediaExt];
    taxonId = 'NA'; %TODO
    annotationPath = 'NA'; %TODO
    lineNum = '1'; %TODO
    
    fprintf(fh, ['image_to_score' DELIM ...
        media DELIM ...
        taxonId DELIM ...
        annotationPath DELIM ...
        lineNum '\n']);
end

fclose(fh);

end