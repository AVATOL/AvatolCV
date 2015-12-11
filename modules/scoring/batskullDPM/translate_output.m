function translate_output( scoringOutputDir, trainingDataDir, testImagesFile )
%TRANSLATE_OUTPUT Summary of this function goes here
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

%% make sure directories are there
[rootDir, ~, ~] = fileparts(testImagesFile);
rootDir = fullfile(rootDir, LEGACY_FOLDER);
if ~exist(rootDir, 'dir')
    error('root directory from batskull algorithm does not exist');
end

outputDir = fullfile(rootDir, OUTPUT);
if ~exist(outputDir, 'dir')
    error('output directory from batskull algorithm does not exist');
end

detectionResultsDir = fullfile(rootDir, DETECTION_RESULTS);
if ~exist(detectionResultsDir, 'dir')
    error('detection_results directory from batskull algorithm does not exist');
end

%% read and parse output TODO
outputDirList = dir([outputDir filesep '*.txt']);
nCharacters = length(outputDirList);

scoredDataList = cell(nCharacters, 1);
unscoredDataList = cell(nCharacters, 1);
charIdList = cell(nCharacters, 1);
charNameList = cell(nCharacters, 1);

for i = 1:nCharacters
    logdebug('parsing output file %d', i);
    outputFile = fullfile(outputDir, outputDirList(i).name);
    [scoredDataList{i}, unscoredDataList{i}, charIdList{i}, ...
        charNameList{i}] = parse_output_file(outputFile);
end

end % translate

function logdebug(msg, varargin)

%% set whether to print debug messages here
PRINT_DEBUG_MESSAGES = true;

if PRINT_DEBUG_MESSAGES
    fprintf(strcat(msg, '\n'), varargin{:});
end

end % logdebug

function [scoredData, unscoredData, charId, charName] = ...
    parse_output_file(outputFile)

%% constants
FILE_NAME_DELIMITER = '_';
SORTED = 'sorted';
OUTPUT = 'output';
DATA = 'data';

FILE_WITHIN_LINE_DELIMITER = '|';
EXPECTED_NUM_LINE_COMPONENTS = 8;
IMAGE_SCORED = 'image_scored';
COULD_NOT_SCORE = 'COULD_NOT_SCORE';

EXPECTED_DET_NUM_LINE_COMPONENTS = 5;
POINT_COORDS_DELIMITER = ',';
DETECTION_RESULTS_DELIMITER = '|';

%% parse file name
[~, fileName, ~] = fileparts(outputFile);
fileNameComponents = strsplit(fileName, FILE_NAME_DELIMITER, ...
    'CollapseDelimiters', false);
if length(fileNameComponents) ~= 5
    error('unexpected # of components from parsing file name "%s"', ...
        fileName);
end

[sortedString, outputString, dataString, charId, ...
    charName] = deal(fileNameComponents{:});
if strcmp(sortedString, SORTED) == 0 ...
        || strcmp(outputString, OUTPUT) == 0 ...
        || strcmp(dataString, DATA) == 0
    error('unexpected parse of file name "%s"', fileName);
end

%% parse score file
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
scoredData = struct;
unscoredData = struct;

% parse each line
scoredCounter = 1;
unscoredCounter = 1;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end
    
    %% parse line
    lineComponents = strsplit(tline, FILE_WITHIN_LINE_DELIMITER, ...
        'CollapseDelimiters', false);
    if length(lineComponents) ~= EXPECTED_NUM_LINE_COMPONENTS
        error('expected %d components from parsing line(%d) in file "%s"', ...
            EXPECTED_NUM_LINE_COMPONENTS, scoredCounter+unscoredCounter-1, fileName);
    end
    
    % TODO - deal with lineNum?
    [imageScoredString, mediaPath, charStateId, charStateName, ...
        detectionResultsPath, taxonId, lineNum, confidence] = ...
        deal(lineComponents{:});
    if strcmp(imageScoredString, IMAGE_SCORED) == 0
        error('expected "%s" token in file "%s"', IMAGE_SCORED, fileName);
    end
    
    %% could not score vs. scored
    if strcmp(confidence, COULD_NOT_SCORE) == 1
        unscoredData(unscoredCounter).image = mediaPath;
        unscoredCounter = unscoredCounter + 1;
    else
        scoredData(scoredCounter).image = mediaPath;
        scoredData(scoredCounter).charStateId = charStateId;
        scoredData(scoredCounter).charStateName = charStateName;
        scoredData(scoredCounter).taxonId = taxonId;
        scoredData(scoredCounter).confidence = confidence;
        
        %% parse detection_results file
        det_fh = fopen(detectionResultsPath, 'rt');
        det_tline = fgetl(det_fh);
        if ~ischar(tline)
            error('detection_results file "%s" is invalid', ...
                detectionResultsPath);
        end
        
        %% get line
        lineComponentsDet = strsplit(det_tline, DETECTION_RESULTS_DELIMITER, ...
            'CollapseDelimiters', false);
        if length(lineComponentsDet) ~= EXPECTED_DET_NUM_LINE_COMPONENTS
            error('expected %d components from parsing file "%s"', ...
            EXPECTED_DET_NUM_LINE_COMPONENTS, detectionResultsPath);
        end
        
        [pointCoords, charIdDet, charNameDet, charStateIdDet, ...
            charStateNameDet] = ...
            deal(lineComponentsDet{:});
        
        %% sanity checks
        if strcmp(charStateIdDet, charStateId) == 0 ...
                || strcmp(charStateNameDet, charStateNameDet) == 0
            error(['charStateId or charStateName does not match ' ...
                'beween detection_results and sorted_output_data files']);
        end
        
        if strcmp(charIdDet, charId) == 0 ...
                || strcmp(charNameDet, charNameDet) == 0
            error(['charId or charName does not match ' ...
                'beween detection_results and sorted_output_data files']);
        end
        
        %% parse coordinates
        pointCoordsParse = strsplit(pointCoords, POINT_COORDS_DELIMITER);
        [x, y] = deal(pointCoordsParse{:});
        scoredData(scoredCounter).x = x;
        scoredData(scoredCounter).y = y;
        
        %% close detection_results file
        fclose(det_fh);
        
        scoredCounter = scoredCounter + 1;
    end
end

fclose(fh);

end % parse_output_file