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

%% make sure folders and files exist
logdebug('scoringOutputDir: %s', scoringOutputDir);
logdebug('trainingDataDir: %s', trainingDataDir);
logdebug('testImagesFile: %s', testImagesFile);

if ~exist(scoringOutputDir, 'dir')
    error('directory "%s" does not exist!', scoringOutputDir);
end
if ~exist(trainingDataDir, 'dir')
    error('directory "%s" does not exist!', trainingDataDir);
end
if ~exist(testImagesFile, 'file')
    error('file "%s" does not exist!', testImagesFile);
end

%% read in test images
testImages = read_test_images_file(testImagesFile);

%% read and parse training data
trainingDirList = dir([trainingDataDir filesep 'training_*.txt']);
nCharacters = length(trainingDirList);

trainingDataList = cell(nCharacters, 1);
scoringConcernIDList = cell(nCharacters, 1);
scoringConcernNameList = cell(nCharacters, 1);

for i = 1:nCharacters
    logdebug('parsing training file %d...', i);
    trainingFile = fullfile(trainingDataDir, trainingDirList(i).name);
    [trainingDataList{i}, scoringConcernIDList{i}, ...
        scoringConcernNameList{i}] = ...
        parse_training_images_file(trainingFile);
end

%% read and parse scoring data
scoringDirList = dir([trainingDataDir filesep 'scoring_*.txt']);
if nCharacters ~= length(scoringDirList)
    error('mismatch number of scoring files (%d) vs. training files (%d)', ...
        length(scoringDirList), nCharacters);
end

scoringDataList = cell(nCharacters, 1);

for i = 1:nCharacters
    logdebug('parsing scoring file %d...', i);
    scoringFile = fullfile(trainingDataDir, scoringDirList(i).name);
    scoringDataList{i} = ...
        parse_scoring_images_file(scoringFile);
end

%% set up directories
logdebug('setting up directories...');
[rootDir, ~, ~] = fileparts(testImagesFile);
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
logdebug('generating summary file...');
summaryFile = fullfile(inputDir, SUMMARY_FILE);
generate_summary_file(summaryFile, INPUT, OUTPUT, DETECTION_RESULTS, ...
    trainingDataList, scoringConcernIDList, scoringConcernNameList, ...
    scoringDataList);

%% generate annotation files
logdebug('generating annotation files...');
generate_annotation_files(trainingDataList, ...
    scoringConcernIDList, scoringConcernNameList, annotationsDir);

%% generate sorted_input_data_ files
for i = 1:nCharacters
    logdebug('generating sorted input file %d...', i);
    charId = scoringConcernIDList{i};
    fileName = ['sorted_input_data_' charId '_' ...
        scoringConcernNameList{i} '.txt'];
    filePath = fullfile(inputDir, fileName);
    generate_sorted_input_data_file(filePath, charId, trainingDataList{i}, ...
        scoringDataList{i}, ANNOTATIONS, rootDir);
end

end % translate

function logdebug(msg, varargin)

%% set whether to print debug messages here
PRINT_DEBUG_MESSAGES = true;

if PRINT_DEBUG_MESSAGES
    fprintf(strcat(msg, '\n'), varargin{:});
end

end % logdebug

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
RECTANGLE = 'rectangle';
POINT_COORDS_DELIMITER = '-';
RECTANGLE_POINTS_DELIMITER = ';';

%% parse file name
[~, fileName, ~] = fileparts(trainingFile);
fileNameComponents = strsplit(fileName, FILE_NAME_DELIMITER);
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
% % count number of lines in file
% nLines = 0;
% while 1
%     tline = fgetl(fh);
%     if ~ischar(tline)
%         break
%     end
%     nLines = nLines + 1;
% end
% frewind(fh);

% set up data structure
trainingData = struct;

% parse each line
lineNum = 0;
counter = 1;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end
    lineNum = lineNum + 1;
    
    %% parse line
    lineComponents = strsplit(tline, FILE_WITHIN_LINE_DELIMITER);
    if length(lineComponents) ~= EXPECTED_NUM_LINE_COMPONENTS
        error('expected %d components from parsing line(%d) in file "%s"', ...
            EXPECTED_NUM_LINE_COMPONENTS, lineNum, fileName);
    end
    
    [imagePath, scoringConcernValue, pointCoords, trainTestConcern, ...
        trainTestValue] = deal(lineComponents{:});
    
    %% parse point coordinates
    if strcmp(pointCoords, '') == 0
        components1 = strsplit(pointCoords, ':');
        if (strcmp(components1{1}, POINT))
            components2 = strsplit(components1{2}, POINT_COORDS_DELIMITER);
            [trainingData(counter).x, ...
            trainingData(counter).y] = ...
            deal(str2double(components2{1}), str2double(components2{2}));
            trainingData(counter).x2 = -1;
            trainingData(counter).y2 = -1;
        elseif (strcmp(components1{1}, RECTANGLE))
            rect_points = strsplit(components1{2}, RECTANGLE_POINTS_DELIMITER);
            pair1 = strsplit(rect_points{1}, POINT_COORDS_DELIMITER);
            pair2 = strsplit(rect_points{2}, POINT_COORDS_DELIMITER);
            trainingData(counter).x = str2double(pair1{1});
            trainingData(counter).y = str2double(pair1{2});
            trainingData(counter).x2 = str2double(pair2{1});
            trainingData(counter).y2 = str2double(pair2{2});
        else 
            error('expected point or rectangle from parsing line(%d) in file "%s"', ...
                lineNum, fileName);
        end
        
    else
        fprintf('omitting example due to no annotation: %s\n', ...
            imagePath);
        continue;
    end
    
    %% parse image path
    trainingData(counter).image = imagePath;
    
    %% parse scoring concern value
    components1 = strsplit(scoringConcernValue, ':');
    components2 = strsplit(components1{2}, '|');
    [trainingData(counter).charStateId, ...
        trainingData(counter).charStateName] = ...
        deal(components2{1}, components2{2});
    
    %% parse traintest concern
    components1 = strsplit(trainTestConcern, ':');
    components2 = strsplit(components1{2}, '|');
    trainingData(counter).trainTestConcern = deal(components2{2});
    
    %% parse traintest value
    components1 = strsplit(trainTestValue, ':');
    components2 = strsplit(components1{2}, '|');
    [trainingData(counter).taxonId, ...
        trainingData(counter).taxonName] = ...
        deal(components2{1}, components2{2});
    
    counter = counter + 1;
end

fclose(fh);

end % parse_training_images_file

function scoringData = parse_scoring_images_file(scoringFile)

%% constants
FILE_NAME_DELIMITER = '_';
SCORING = 'scoring';
SCORING_CONCERN_TYPE_CHARACTER = 'character';

FILE_WITHIN_LINE_DELIMITER = ',';
EXPECTED_NUM_LINE_COMPONENTS = 4;

%% parse file name
[~, fileName, ~] = fileparts(scoringFile);
fileNameComponents = strsplit(fileName, FILE_NAME_DELIMITER);
if length(fileNameComponents) ~= 4
    error('unexpected # of components from parsing file name "%s"', ...
        fileName);
end

[scoringString, scoringConcernType, ~, ~] = deal(fileNameComponents{:});
if strcmp(scoringString, SCORING) == 0 ...
        || strcmp(scoringConcernType, SCORING_CONCERN_TYPE_CHARACTER) == 0
    error('expected "%s" instead of "%s" when parse of file name "%s"', ...
        SCORING_CONCERN_TYPE_CHARACTER, scoringConcernType, fileName);
end

%% parse scoring file
fh = fopen(scoringFile, 'rt');

% set up data structure
scoringData = struct;

% parse each line
lineNum = 0;
counter = 1;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end
    lineNum = lineNum + 1;
    
    %% parse line
    lineComponents = strsplit(tline, FILE_WITHIN_LINE_DELIMITER);
    if length(lineComponents) ~= EXPECTED_NUM_LINE_COMPONENTS
        error('expected %d components from parsing line(%d) in file "%s"', ...
            EXPECTED_NUM_LINE_COMPONENTS, lineNum, fileName);
    end
    
    [imagePath, trainTestConcern, trainTestValue, pointCoords] = deal(lineComponents{:});
    
    %% parse image path
    scoringData(counter).image = imagePath;
    
    %% parse traintest concern
    components1 = strsplit(trainTestConcern, ':');
    components2 = strsplit(components1{2}, '|');
    scoringData(counter).trainTestConcern = deal(components2{2});
    
    %% parse traintest value
    components1 = strsplit(trainTestValue, ':');
    components2 = strsplit(components1{2}, '|');
    [scoringData(counter).taxonId, ...
        scoringData(counter).taxonName] = ...
        deal(components2{1}, components2{2});
    
    counter = counter + 1;
end

fclose(fh);

end % parse_scoring_images_file

function generate_summary_file(summaryFile, INPUT, OUTPUT, ...
    DETECTION_RESULTS, trainingDataList, scoringConcernIDList, ...
    scoringConcernNameList, scoringDataList)

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
        [~, mediaId, ~] = fileparts(mediaPath);
        
        if ~ismember(mediaId, mediaIdList)
            fprintf(fh, 'media,%s,%s,training\n', mediaId, mediaPath);
            mediaIdList{length(mediaIdList)+1} = mediaId;
        end
    end
end

for charIndex = 1:length(scoringDataList)
    scoringData = scoringDataList{charIndex};
    for exampleIndex = 1:length(scoringData)
        mediaPath = scoringData(exampleIndex).image;
        [~, mediaId, ~] = fileparts(mediaPath);
        
        if ~ismember(mediaId, mediaIdList)
            fprintf(fh, 'media,%s,%s,toScore\n', mediaId, mediaPath);
            mediaIdList{length(mediaIdList)+1} = mediaId;
        end
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
            fprintf(fh, 'taxon,%s,%s,%s\n', taxonId, taxonName, ...
                'training');
            taxonIdList{length(taxonIdList)+1} = taxonId;
        end
    end
end

for charIndex = 1:length(scoringDataList)
    scoringData = scoringDataList{charIndex};
    for exampleIndex = 1:length(scoringData)
        taxonId = scoringData(exampleIndex).taxonId;
        taxonName = scoringData(exampleIndex).taxonName;
        
        if ~ismember(taxonId, taxonIdList)
            fprintf(fh, 'taxon,%s,%s,%s\n', taxonId, taxonName, ...
                'scoring');
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
RECTANGLE_POINTS_DELIMITER = ';';
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
        x2 = trainingData(exampleIndex).x2;
        y2 = trainingData(exampleIndex).y2;
        charStateId = trainingData(exampleIndex).charStateId;
        charStateName = trainingData(exampleIndex).charStateName;
        
        %% write to file
        annotationFile = [mediaId '_' charId '.txt'];
        annotationPath = fullfile(annotationsDir, annotationFile);
        fh = fopen(annotationPath, 'wt');
        
        if (x2 == -1)
            fprintf(fh, [num2str(x) COORDS_DELIM ...
                num2str(y) DELIM ...
                charId DELIM ...
                charName DELIM ...
                charStateId DELIM ...
                charStateName '\n']);
        else 
            fprintf(fh, [num2str(x) COORDS_DELIM ...
                num2str(y) ...
                RECTANGLE_POINTS_DELIMITER ...
                num2str(x2) COORDS_DELIM ...
                num2str(y2) ...
                DELIM ...
                charId DELIM ...
                charName DELIM ...
                charStateId DELIM ...
                charStateName '\n']);
            
        end
        
        
        fclose(fh);
    end
end

end % generate_annotation_files

function generate_sorted_input_data_file(filePath, charId, trainingData, ...
    scoringData, ANNOTATIONS, rootDir)

%% constants
DELIM = '|';

%% write to file
fh = fopen(filePath, 'wt');

%% write training data
mediaIdList = cell(0);
for i = 1:length(trainingData)
    [~, mediaId, ~] = fileparts(trainingData(i).image);
    media = trainingData(i).image;
    charStateId = trainingData(i).charStateId;
    charStateName = trainingData(i).charStateName;
    annotationPath = fullfile(rootDir, ANNOTATIONS, [mediaId '_' charId '.txt']);
    taxonId = trainingData(i).taxonId;
    lineNum = '1';
    
    fprintf(fh, '%s%s%s%s%s%s%s%s%s%s%s%s%s\n', ...
        'training_data', DELIM, ...
        media, DELIM, ...
        charStateId, DELIM, ...
        charStateName, DELIM, ...
        annotationPath, DELIM, ...
        taxonId, DELIM, ...
        lineNum);
    
    if ~ismember(media, mediaIdList)
        mediaIdList{length(mediaIdList)+1} = media;
    end
end

%% write scoring data
for i = 1:length(scoringData)
    media = scoringData(i).image;
    taxonId = scoringData(i).taxonId;
    annotationPath = 'NA';
    lineNum = 'NA';
    
    if ismember(media, mediaIdList)
        fprintf('warning: test image found in training: %s\n', media);
    end
    
    fprintf(fh, '%s%s%s%s%s%s%s%s%s\n', ...
        'image_to_score', DELIM, ...
        media, DELIM, ...
        taxonId, DELIM, ...
        annotationPath, DELIM, ...
        lineNum);
end

fclose(fh);

end