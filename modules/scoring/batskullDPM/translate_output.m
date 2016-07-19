function translate_output( scoringOutputDir, trainingDataDir, testImagesFile )
%TRANSLATE_OUTPUT Summary of this function goes here
%   Detailed explanation goes here

%% constants
INPUT = 'input';
OUTPUT = 'output';
DETECTION_RESULTS = 'detection_results';
SUMMARY_FILE = 'summary.txt';

LEGACY_FOLDER = 'legacy_format';

SCORING_CONCERN_TYPE = 'character';

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

inputDir = fullfile(rootDir, INPUT);
if ~exist(inputDir, 'dir')
    error('input directory from batskull algorithm does not exist');
end

outputDir = fullfile(rootDir, OUTPUT);
if ~exist(outputDir, 'dir')
    error('output directory from batskull algorithm does not exist');
end

detectionResultsDir = fullfile(rootDir, DETECTION_RESULTS);
if ~exist(detectionResultsDir, 'dir')
    error('detection_results directory from batskull algorithm does not exist');
end

%% read and parse summary file for characters and states
outputDirList = dir([outputDir filesep 'sorted_output_data*.txt']);
nCharacters = length(outputDirList);

summaryFile = fullfile(inputDir, SUMMARY_FILE);
[charMetaList, charIdList, charNameList] = parse_summary_file(summaryFile, nCharacters);

%% read and parse output
scoredDataList = cell(nCharacters, 1);
unscoredDataList = cell(nCharacters, 1);

for i = 1:nCharacters
    logdebug('parsing output file %d', i);
    outputFile = fullfile(outputDir, outputDirList(i).name);
    [scoredDataList{i}, unscoredDataList{i}] = ...
        parse_output_file(outputFile, charMetaList{i});
end

%% write output
for i = 1:nCharacters
    logdebug('writing output file %d', i);
    
    outputFile = fullfile(scoringOutputDir, ...
        ['scored_' SCORING_CONCERN_TYPE '_' ...
        charIdList{i} '_' charNameList{i} '.txt']);
    write_output_file(outputFile, charMetaList{i}, ...
        scoredDataList{i}, unscoredDataList{i});
end

end % translate

function logdebug(msg, varargin)

%% set whether to print debug messages here
PRINT_DEBUG_MESSAGES = true;

if PRINT_DEBUG_MESSAGES
    fprintf(strcat(msg, '\n'), varargin{:});
end

end % logdebug

function [scoredData, unscoredData] = ...
    parse_output_file(outputFile, charMeta)

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
DETECTION_RESULTS_DELIMITER = ':';

%% parse file name
[~, fileName, ~] = fileparts(outputFile);
fileNameComponents = strsplit(fileName, FILE_NAME_DELIMITER);
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
fh = fopen(outputFile, 'rt');

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
    lineComponents = strsplit(tline, FILE_WITHIN_LINE_DELIMITER);
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
        scoredData(scoredCounter).charStateName = '???';
        for i = 1:length(charMeta)
            if strcmp(charMeta(i).id, charStateId) == 1
                scoredData(scoredCounter).charStateName = charMeta(i).name;
            end
        end
        if strcmp(scoredData(scoredCounter).charStateName, '???') == 1
            error('charStateId does not match')
        end
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
        lineComponentsDet = strsplit(det_tline, DETECTION_RESULTS_DELIMITER);
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

function [charMeta, charIdList, charNameList] = ...
    parse_summary_file(summaryFile, nCharacters)

%% constants
DELIMITER = ',';

%% parse score file
fh = fopen(summaryFile, 'rt');

% parse characters
charIdList = cell(nCharacters, 1);
charNameList = cell(nCharacters, 1);
charIdx = 1;
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end

    strs = strsplit(tline, DELIMITER);

    if strcmp(strs{1},'character')
        charNameList{charIdx} = strs{3};
        charIdList{charIdx} = strs{2};
        charIdx = charIdx + 1;
    elseif strcmp(strs{1},'media')
        continue;
    elseif strcmp(strs{1},'state')
        continue;
    elseif strcmp(strs{1},'taxon')
        continue;
    elseif strcmp(strs{1},'view')
        continue;
    elseif strcmp(strs{1},'inputDir')
        continue
    elseif strcmp(strs{1},'outputDir')
        continue
    elseif strcmp(strs{1},'detectionResultsDir')
        continue
    else
        error('summary.txt: unknown line');
    end
end

fclose(fh);
fh = fopen(summaryFile, 'rt');

%% parse states
charMeta = cell(nCharacters, 1);
while 1
    tline = fgetl(fh);
    if ~ischar(tline)
        break
    end

    strs = strsplit(tline, DELIMITER);

    if strcmp(strs{1},'state')
        stateId = strs{2};
        stateName = strs{3};
        charId = strs{4};
        for i = 1:nCharacters % inefficient loop
            if strcmp(charIdList(i), charId) == 1
                charMeta{i}(end+1).id = stateId;
                charMeta{i}(end).name = stateName;
                break;
            end
        end
    else
        continue;
    end
end

fclose(fh);

end % parse_summary_file

function write_output_file(outputFile, charMeta, scoredData, unscoredData)

%% constants
DELIM = ',';
CHARACTER_STATE = 'characterState';
POINT = 'point';
POINT_COORDS_DELIM = '-';

%% write to file
fh = fopen(outputFile, 'wt');

%% write class header
class1 = [CHARACTER_STATE ':' charMeta(1).id ...
        '|' charMeta(1).name];
try
    class2 = [CHARACTER_STATE ':' charMeta(2).id ...
        '|' charMeta(2).name];
catch
    class2 = '?';
end
fprintf(fh, 'classNames=%s,%s\n', class1, class2);

%% write scored data
if isfield(scoredData, 'image')
    for i = 1:length(scoredData)
        fullpathtoUnScoredImage = scoredData(i).image;
        classValue = [CHARACTER_STATE ':' scoredData(i).charStateId ...
            '|' scoredData(i).charStateName];
        pointAnnotations = [POINT ':' num2str(scoredData(i).x) ...
            POINT_COORDS_DELIM num2str(scoredData(i).y)];

        confClass1 = str2double(scoredData(i).confidence);
        confClass2 = 1.0-confClass1;

        fprintf(fh, '%s%s%s%s%s%s%s%s%s\n', ...
            fullpathtoUnScoredImage, DELIM, ...
            classValue, DELIM, ...
            pointAnnotations, DELIM, ...
            num2str(confClass1), DELIM, ...
            num2str(confClass2));
    end
end

%% write unscored data
if isfield(unscoredData, 'image')
    for i = 1:length(unscoredData)
        fullpathtoUnScoredImage = unscoredData(i).image;
        classValue = 'NOT_SCORE';

        fprintf(fh, '%s%s%s%s%s%s%s%s%s\n', ...
            fullpathtoUnScoredImage, DELIM, ...
            classValue, DELIM, ...
            '?', DELIM, ...
            '?', DELIM, ...
            '?');
    end
end

fclose(fh);

end % write_output_file