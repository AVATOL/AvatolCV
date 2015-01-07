classdef AlgorithmChoiceScreen < handle
    %ALGORITHM Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        algorithmChosen;
        session;
        ui;
        algorithms;
        progressIndicator;
        originalDir;
        
        messageText;
        showRunAlgorithmButton;
        message;
    end
    
    methods
        function obj = AlgorithmChoiceScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
            obj.algorithms = Algorithms();
        end
        function doAnotherCharacter(obj,hObject, eventData)
            obj.session.doAnotherCharacter();
        end
        
        function runAlgorithm(obj, hObject, eventData)
            fprintf('algorithm unhooked for demo');
            set(obj.messageText, 'String', 'Algorithms disengaged for B2 demo');
        end
        function runAlgorithmReal(obj, hObject, eventData)
            obj.ui.deleteObsoleteControls();
            messagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag','messagePanel',...
                                      'Position',[0.1 0.2 0.8 0.7]);

            startingAlgorithmString = sprintf('Starting %s algorithm...', obj.algorithmChosen);
            statusMessage = uicontrol('style', 'text' ,...
                                         'Parent',messagePanel,...
                                         'Units', 'normalized',...
                                         'position', [0 0 1 1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','messageText' ,...
                                         'Background',[1 1 1],...
                                         'String', startingAlgorithmString,...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...

            obj.progressIndicator = ProgressIndicator(statusMessage);
            obj.ui.activePanelTags = { 'messagePanel' };
            obj.ui.activeControlTags = {'messageText'}; 
            obj.originalDir = pwd();

            bundleRootDir = char(obj.session.morphobankBundle.getRootDir());
            cd(bundleRootDir);

            if (strcmp(obj.algorithmChosen,'CRF'))
                javaStringCRF = java.lang.String('CRF');
                chosenCharNameString = java.lang.String(obj.session.characterChoiceScreen.characterName);
                inputFilePathname = char(obj.session.morphobankBundle.getInputFilePathnameForCharacter(chosenCharNameString));
                outputFilePathname = char(obj.session.morphobankBundle.getOutputFilePathnameForCharacter(chosenCharNameString, javaStringCRF));
                options = struct;

                charId = obj.session.characterChoiceScreen.characterIdJavaString;
                detectionResultsPathname = char(obj.session.morphobankBundle.getDetectionResultsPathname(charId, javaStringCRF));
                options.DETECTION_RESULTS_FOLDER = detectionResultsPathname;

                dataset_path = char(obj.session.morphobankBundle.getRootDir());
                options.DATASET_PATH = dataset_path;

                crf_temp_path = char(obj.session.morphobankBundle.getTempDirForAlg('CRF'));
                mkdir(crf_temp_path);
                options.TEMP_PATH = crf_temp_path;

                hcsearch_dir = char(obj.session.avatolSystem.getCrfDir());
                options.BASE_PATH = hcsearch_dir;

                options.PROGRESS_INDICATOR = obj.progressIndicator;
                obj.algorithms.invoke_the_crf_system(inputFilePathname, outputFilePathname, options);
                obj.session.scoredSetMetadatas.persistForCRF(obj.session.chosenMatrix,chosenCharNameString);
                obj.session.showResultsForCurrentCharacter();
            else
                chosenCharNameString = java.lang.String(obj.session.characterChoiceScreen.characterName);
                mb = obj.session.morphobankBundle;
                chosenView = obj.session.dpmQuestionScreens.chosenView;
                viewId = mb.getViewIdForName(java.lang.String(chosenView));
                
                algIdString = java.lang.String('DPM');
                
                charNameStringList = obj.session.matlabListToJavaStringList(obj.session.dpmQuestionScreens.simplePresenceAbsenceCharacters);
                charIdStringList = java.util.ArrayList();
                for i=0:charNameStringList.size()-1
                    charName = charNameStringList.get(i);
                    charId = mb.getCharacterIdForName(charName);
                    charIdStringList.add(charId);
                end
                %taxonNames = obj.session.javaStringListToMatlabCharList(obj.session.morphobankBundle.getScorableTaxonNames());
                mb.filterInputs(charIdStringList, viewId, algIdString);
                
                inputFolderJavaString = mb.getFilteredInputDirName(charIdStringList, viewId, algIdString);
                outputFolderJavaString = mb.getFilteredOutputDirName(charIdStringList, viewId, algIdString);
                detectionResultsFolderJavaString = mb.getFilteredDetectionResultsDirName(charIdStringList, viewId, algIdString);
                
                input_folder = char(inputFolderJavaString);
                output_folder = char(outputFolderJavaString);
                detection_results_folder = char(detectionResultsFolderJavaString);
                
                list_of_characters = obj.session.javaStringListToMatlabCharList(charIdStringList);
                charId = obj.session.characterChoiceScreen.characterIdJavaString;
                obj.algorithms.invoke_the_dpm_system(list_of_characters, input_folder, output_folder, detection_results_folder);
                %obj.algorithms.invoke_the_dpm_system(list_of_characters, input_folder, output_folder, detection_results_folder, obj.progressIndicator);
                chosenMatrix = obj.session.matrixChoiceScreen.chosenMatrix;
                obj.session.scoredSetMetadatas.persistForDPM(chosenMatrix,chosenCharNameString,charId,java.lang.String(chosenView),charNameStringList,input_folder, output_folder, detection_results_folder);
                obj.session.showResultsForCurrentCharacter();
            end

            %here is where we show the results
            % FIXME - do the following two steps when leaving the results
            % area
            %showResults();
            %cd(obj.originalDir);
        end


        function chooseAlgorithm(obj)
            if (false)
                obj.message = 'DPM algorithm has been chosen for scoring.  Press Run Algorithm to begin.';
                obj.showRunAlgorithmButton = true;
                obj.algorithmChosen = 'DPM';
            else
                qs = obj.session.questionnaireScreens.questionSequencer;
                aq = qs.answeredQuestions;
                disqualifyingCRFMessage = obj.algorithms.getDisqualifyingMessageForCRF(aq);
                disqualifyingDPMMessage = obj.algorithms.getDisqualifyingMessageForDPM(aq);
                obj.showRunAlgorithmButton = false;
                if (strcmp(disqualifyingCRFMessage,''))
                    obj.message = 'CRF algorithm has been chosen for scoring.  Press Run Algorithm to begin.';
                    obj.showRunAlgorithmButton = true;
                    obj.algorithmChosen = 'CRF';
                elseif (strcmp(disqualifyingDPMMessage,''))
                    obj.message = 'DPM algorithm has been chosen for scoring.  Press Run Algorithm to begin.';
                    obj.showRunAlgorithmButton = true;
                    obj.algorithmChosen = 'DPM';
                else
                    general_error_msg = 'The answers chosen indicate the currently in-play algorithms are not a match for scoring the images:';
                    obj.message = sprintf('%s\n\n%s\n\n%s',general_error_msg, disqualifyingCRFMessage, disqualifyingDPMMessage);
                end
            end    
            
        end    
        function displayQuestionnaireCompleteScreen(obj) 

            obj.ui.deleteObsoleteControls();
            

            messagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag','messagePanel',...
                                      'Position',[0.1 0.2 0.8 0.7]);


            obj.messageText = uicontrol('style', 'text' ,...
                                         'Parent',messagePanel,...
                                         'Units', 'normalized',...
                                         'position', [0 0 1 1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','messageText' ,...
                                         'Background',[1 1 1],...
                                         'String', obj.message,...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...

            navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                         'BorderType', 'none',...
                                         'Units', 'normalized',...
                                         'Tag','navigationPanel' ,...
                                         'Position',obj.ui.getNavigationPanelPosition());

            doAnotherCharacter = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Try another character' ,...
                                         'Parent',navigationPanel,...
                                         'Units', 'normalized',...
                                         'position', [0 0 0.2 1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','doAnotherCharacter' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  

            if (obj.showRunAlgorithmButton)
                 runAlgorithm = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Run Algorithm' ,...
                                         'Parent',navigationPanel,...
                                         'Units', 'normalized',...
                                         'position', obj.ui.getButtonPositionRightBig() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','runAlgorithm' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
                obj.ui.activeControlTags = { 'messageText', 'doAnotherCharacter', 'runAlgorithm'}; 
                set(runAlgorithm, 'callback', {@obj.runAlgorithm});
            else
                done = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Exit' ,...
                                         'Parent',navigationPanel,...
                                         'Units', 'normalized',...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','done' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
                obj.ui.activeControlTags = { 'messageText', 'doAnotherCharacter', 'done'}; 
                set(done, 'callback', {@exit});
            end
            skipToResultsReview = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Skip to Results Review' ,...
                                         'Parent',navigationPanel,...
                                         'Units','normalized',...
                                         'position', [0.2 0 0.2 1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','skipToResultsReview' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            %H.prev = uicontrol('style', 'pushbutton' ,...
            %                             'String', 'Prev' ,...
            %                             'Parent',H.navigationPanel,...
            %                             'Units', 'normalized',...
            %                             'position', getButtonPositionRightA() ,...
            %                             'FontName', H.fontname ,...
            %                             'FontSize', H.fontsize ,...
            %                             'Tag','prev' ,...
            %                             'BackgroundColor', [0.8 0.8 0.8]);  

            obj.ui.activePanelTags = { 'messagePanel', 'navigationPanel' };
            %H.activeControlTags = { 'messageText', 'doAnotherCharacter', 'done', 'prev'};    


            set(doAnotherCharacter, 'callback', {@obj.doAnotherCharacter});
            
            set(skipToResultsReview, 'callback', {@obj.jumpToResultsReview});
            obj.session.mostRecentScreen = 'QUESTIONNAIRE_COMPLETE'; 
            obj.session.questionnaireScreens.questionSequencer.persist();
        end
        
        function jumpToResultsReview(obj,hObject, eventData)
            obj.session.jumpToResultsReview();
        end
        function exit(obj)
            obj.session.exit();
        end
        

    end
    
end

