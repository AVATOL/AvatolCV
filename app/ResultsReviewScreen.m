classdef ResultsReviewScreen < handle
    %RESULTSREVIEW Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        ui;
        session;
        metadataKeyIndex = 0;
        metadataKeyCount = 0;
        keys;
        ssm;
        input_folder;
        output_folder;
        detection_results_folder;
        %focusTrainingOrResults = 'results';
        focusTrainingOrResults = 'training';
        currentCharName = 'unknown';
        currentCharIdJavaString;
        inputFilesForCharacter;
        inputFile;
        trainingSamplesForCharacter;
        trainingSampleIndex;
        outputFileList;
        detectionResultsFileList;
        
        imageControlTags = {};
        metadataControlTags = {};
    end
    
    methods
        function obj = ResultsReviewScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        function reset(obj)
            obj.ssm = obj.session.scoredSetMetadata;
            obj.ssm.loadAll();
            obj.keys = obj.ssm.getKeys();
            obj.metadataKeyCount = obj.keys.size();
            obj.metadataKeyIndex = obj.metadataKeyCount - 1;
            obj.updateFolders();
        end
        function setCurrentCharName(obj, charName)
            obj.currentCharName = charName;
            charNameJavaString = java.lang.String(obj.currentCharName);
            obj.currentCharIdJavaString = obj.session.morphobankBundle.getCharacterIdForName(charNameJavaString);
        end
        function showResults(obj)
            
            obj.ui.deleteObsoleteControls();
            obj.ui.createResultsReviewPanels();
            % create panel for instructions
		
            scoredSetPrompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.scoredSetTitlePanel,...
                                         'Units','normalized',...
                                         'String', 'Select run results.' ,...
                                         'position', [0,0,1,0.6] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','scoredSetPrompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
                                     
            obj.ui.activeControlTags = { 'scoredSetPrompt' }
            
            if strcmp(obj.focusTrainingOrResults,'results') == 1
                resultsValue = 1;
                trainingValue = 0;
            else
                resultsValue = 0;
                trainingValue = 1;
            end
            % create panel for training vs results showing
            buttonGroup = uibuttongroup('Visible','on',...
                                        'Tag','buttonGroup',...
                                        'BorderType','none',...
                                        'Background','white',...
                                        'Parent',obj.ui.checkboxPanePanel,...
                                        'Position',[0 0 1 1]);
            
            obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'buttonGroup' ];    
            resultsRadio = uicontrol('Style','radiobutton',...
                                     'visible', 'on',...
                                     'String','show results' ,...
                                     'Background', 'white' ,...
                                     'Units', 'normalized',...
                                     'Value',resultsValue,...
                                     'Position',[0, 0, 0.3, 1.0],...
                                     'Parent',buttonGroup,...
                                     'FontName', obj.ui.fontname ,...
                                     'FontSize', obj.ui.fontsize ,...
                                     'Tag', 'resultsRadio',...
                                     'HandleVisibility', 'off');

            %obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'resultsRadio' ];
            trainingRadio = uicontrol('Style','radiobutton',...
                                     'visible', 'on',...
                                     'String','show training examples' ,...
                                     'Background', 'white' ,...
                                     'Units', 'normalized',...
                                     'Value',trainingValue,...
                                     'Position',[0.45, 0, 0.6, 1.0],...
                                     'Parent',buttonGroup,...
                                     'FontName', obj.ui.fontname ,...
                                     'FontSize', obj.ui.fontsize ,...
                                     'Tag', 'trainingRadio',...
                                     'HandleVisibility', 'off');

            %obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'trainingRadio' ];
            % create panel for images//
		
            
            doAnotherCharacter = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Try another character' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units', 'normalized',...
                                         'position', obj.ui.getButtonPositionLeft() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','doAnotherCharacter' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  
            obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'doAnotherCharacter' ];
            
            exit = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Exit' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','exit' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  
            obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'exit' ];
          
           
            obj.loadMetadataWidgets();
            obj.loadImageWidgets();
            %
            %
            % ???????? obj.ui.activeAnswerControl = obj.taxonChoiceWidget;
            obj.session.activeQuestionId = 'ResultsReview';
            obj.ui.activeControlType = 'ResultsReview';

            set(exit, 'callback', {@obj.exit});
            set(doAnotherCharacter, 'callback', {@obj.doAnotherCharacter});
            obj.session.mostRecentScreen = 'RESULTS_REVIEW_SCREEN'; 
        end
        
        function loadImageWidgets(obj)
            fprintf('loadImageWidgets\n');
            obj.ui.deleteControls(obj.imageControlTags);
            %
            %
            prevImage = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Previous Image' ,...
                                         'Enable','off',...
                                         'Units','normalized',...
                                         'position', [0,0,0.4,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Parent',obj.ui.imageNavigationPanel,...
                                         'Tag','prevImage'); 
            set(prevImage, 'callback', {@obj.showPrevImage});
            obj.imageControlTags = { 'prevImage' };
            backImageButtonNeeded = obj.isPrevImageButtonNeeded();
            if backImageButtonNeeded
                set(prevImage,'Enable','on');
            end
            %
            %
            positionInListString = obj.getPositionInList();
            positionInList = uicontrol('style', 'text' ,...
                                         'String', positionInListString ,...
                                         'Units','normalized',...
                                         'position', [0.4,0,0.2,0.8] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','positionInList' ,...
                                         'parent',obj.ui.imageNavigationPanel,...
                                         'BackgroundColor', [1 1 1]); 
            obj.imageControlTags = [  obj.imageControlTags , 'positionInList' ];
            %
            %
            nextImage = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Next Image' ,...
                                         'Enable','off',...
                                         'Units','normalized',...
                                         'position', [0.6,0,0.4,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','nextImage' ,...
                                         'parent',obj.ui.imageNavigationPanel);   
            %
            %
            imageContextString = obj.getImageContextString();
            imageContext = uicontrol('style', 'text' ,...
                                         'String', imageContextString ,...
                                         'Units','normalized',...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','imageContext' ,...
                                         'parent',obj.ui.imageContextPanel,...
                                         'BackgroundColor', [1 1 1]); 
            obj.imageControlTags = [  obj.imageControlTags , 'imageContext' ];   
            
            set(nextImage, 'callback', {@obj.showNextImage});
            obj.imageControlTags = [  obj.imageControlTags , 'nextImage' ];
            nextImageButtonNeeded = obj.isNextImageButtonNeeded();
            if nextImageButtonNeeded
                set(nextImage,'Enable','on');
            end
            obj.loadImage();
        end
        function loadMetadataWidgets(obj)
            curKey = obj.keys.get(obj.metadataKeyIndex);
            curMetadata = obj.ssm.getDisplayableDataForKey(curKey);
            obj.ui.deleteControls(obj.metadataControlTags);
            metadataContent = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.scoredSetMetadataPanel,...
                                         'Units','normalized',...
                                         'String', char(curMetadata) ,...
                                         'position', [0.02,0,0.98,0.98] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','metadataContent' ,...
                                         'Background',[0.9 0.9 0.9],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
            
            obj.metadataControlTags = { 'metadataContent' };
            nextButtonNeeded = false;
            backButtonNeeded = false;
            if obj.metadataKeyCount==1
                % no navigationButtons
            else
                if obj.metadataKeyIndex > 0
                    backButtonNeeded = true;
                end
                if obj.metadataKeyIndex < obj.metadataKeyCount - 1
                    nextButtonNeeded = true;
                end
            end
            
                   
            %
            %
            prev = uicontrol('style', 'pushbutton' ,...
                                         'Enable', 'off',...
                                         'String', 'Previous Run' ,...
                                         'Units','normalized',...
                                         'position', [0,0,0.4,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Parent',obj.ui.scoredSetNavigationPanel,...
                                         'Tag','prev'); 
            set(prev, 'callback', {@obj.showPreviousMetadata});
            obj.metadataControlTags = [  obj.metadataControlTags , 'prev' ];
            if backButtonNeeded
                set(prev,'Enable','on');
            end
            %
            %
            runPositionInListString = obj.getRunPositionInList();
            runPositionInList = uicontrol('style', 'text' ,...
                                         'String', runPositionInListString ,...
                                         'Units','normalized',...
                                         'position', [0.4,0,0.2,0.8] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','runPositionInList' ,...
                                         'parent',obj.ui.scoredSetNavigationPanel,...
                                         'BackgroundColor', [1 1 1]); 
            obj.metadataControlTags = [  obj.metadataControlTags , 'runPositionInList' ];
            %
            %
            next = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Next Run' ,...
                                         'Enable','off',...
                                         'Units','normalized',...
                                         'position', [0.6,0,0.4,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'parent',obj.ui.scoredSetNavigationPanel);   
                
            set(next, 'callback', {@obj.showNextMetadata});
            obj.metadataControlTags = [  obj.metadataControlTags , 'next' ];
            if nextButtonNeeded
                set(next,'Enable','on');
            end
        end
        
        function imageContextString = getImageContextString(obj)
            trainingSample = obj.trainingSamplesForCharacter.get(obj.trainingSampleIndex);
            characterName = char(trainingSample.getCharacterName());
            characterStateName = char(trainingSample.getCharacterStateName());
            imageContextString = sprintf('%s , %s',characterName, characterStateName);
        end
            
        function positionInList = getPositionInList(obj)
            positionInList = sprintf('%d / %d',obj.trainingSampleIndex + 1,obj.trainingSamplesForCharacter.size());
        end    
        
        function runPositionInList = getRunPositionInList(obj)
            runPositionInList = sprintf('%d / %d',obj.metadataKeyIndex + 1, obj.metadataKeyCount);
        end
        function nextImageButtonNeeded = isNextImageButtonNeeded(obj)
            nextImageButtonNeeded = false;
            if (strcmp(obj.focusTrainingOrResults,'training'))
                sampleCount = obj.trainingSamplesForCharacter.size();
                fprintf('sampleCount %d index %d\n', sampleCount, obj.trainingSampleIndex);
                if obj.trainingSampleIndex < sampleCount - 1
                    nextImageButtonNeeded = true;
                end
            else
                % results
                junk = 3;
            end
        end
        function prevImageButtonNeeded = isPrevImageButtonNeeded(obj)
             prevImageButtonNeeded = false;
            if (strcmp(obj.focusTrainingOrResults,'training'))
                if obj.trainingSampleIndex > 0
                    prevImageButtonNeeded = true;
                end
            else
                % results
                junk = 3;
            end
        end
        function showNextImage(obj, hObject, eventData)
            obj.trainingSampleIndex = obj.trainingSampleIndex + 1;
            obj.loadImageWidgets();
        end
        function showPrevImage(obj, hObject, eventData)
            obj.trainingSampleIndex = obj.trainingSampleIndex - 1;
            obj.loadImageWidgets();
        end
        function loadImage(obj)
            currentFigure = gcf;
            set(currentFigure, 'pointer', 'watch')
            drawnow;
            
            fprintf('loadImage\n');
            if strcmp(obj.focusTrainingOrResults,'results') == 1
                 %TBD
                 junk = 3;
            else
                 % load "current" training example
                 trainingSample = obj.trainingSamplesForCharacter.get(obj.trainingSampleIndex);
                 mediaPath = char(trainingSample.getMediaPath());
                  
                 
                 axesPanel = uipanel('Parent',obj.ui.imagePanel,...
                                 'Tag','imagePanel' ,...
                                 'position',[0,0,1,1]);
                 axes1 = axes('Parent',axesPanel,...
                                 'Color',[1,1,1],...
                                 'FontName', obj.ui.fontname ,...
                                 'FontSize', obj.ui.fontsize ,...
                                 'Tag','trainingImage' ,...
                                 'position',[0,0,1,1]);%[0.02,0.02,0.96,0.76]
                 %obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'trainingImage' ];
                 imshow(mediaPath);
                 if trainingSample.hasAnnotationCoordinates()
                     fprintf('drawingCoords\n');
                     annotationCoordinates = trainingSample.getAnnotationCoordinates();
                    point = annotationCoordinates.getPoints().get(0);
                    x = point.getX();
                    y = point.getY();
                    hold on;
                    plot(x,y,'r.','MarkerSize',8)
                 end
                 
                 xlabel('foo');
                 set(currentFigure, 'pointer', 'arrow')
             end
        end    
        function updateFolders(obj)
            curKey = obj.keys.get(obj.metadataKeyIndex);
            obj.input_folder = obj.ssm.getInputFolderForKey(curKey);
            fprintf('input folder : %s', char(obj.input_folder));
            obj.output_folder = obj.ssm.getOutputFolderForKey(curKey);
            obj.detection_results_folder = obj.ssm.getDetectionResultsFolderForKey(curKey);
            mb = obj.session.morphobankBundle;
            bar = obj.input_folder;
            foo = mb.getInputFilesForCharacter(obj.input_folder);
            obj.inputFilesForCharacter = obj.session.morphobankBundle.getInputFilesForCharacter(obj.input_folder);
            if strcmp(obj.currentCharName,'unknown') == 1
                obj.currentCharIdJavaString = obj.ssm.getFocusCharIdForKey(curKey);
                currentCharNameJavaString = obj.session.morphobankBundle.getCharacterNameForId(obj.currentCharIdJavaString);
                obj.currentCharName = char(currentCharNameJavaString);
            end
            obj.inputFile = obj.inputFilesForCharacter.get(obj.currentCharIdJavaString);
            obj.trainingSamplesForCharacter = obj.inputFile.getTrainingSamples();
            obj.trainingSampleIndex = 0;
            %uncoment below when have real output to show.
            %obj.detectionResultsFileList = obj.session.morphobankBundle.inputFiles.getDetectionResultsFiles(obj.detection_results_folder);
            %obj.outputFileList = obj.session.morphobankBundle.outputFiles.getOutputFiles(obj.output_folder);
        end
        function showNextMetadata(obj, hObject, eventData)
            obj.metadataKeyIndex = obj.metadataKeyIndex + 1;
            obj.updateFolders();
            obj.loadMetadataWidgets();
            obj.loadImageWidgets();
        end
        function showPreviousMetadata(obj, hObject, eventData)
            obj.metadataKeyIndex = obj.metadataKeyIndex - 1;
            obj.updateFolders();
            obj.loadMetadataWidgets();
            obj.loadImageWidgets();
        end
        function doAnotherCharacter(obj,hObject, eventData)
            obj.session.doAnotherCharacter();
        end
        function exit(obj, hobject, eventData)
            obj.session.exit();
        end
    end
    
end

