classdef UI < handle
    %UI Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        activeControlTags = {};
        activePanelTags = {};
        activeAnswerControl;
        activeControlType;
        
        textPanel;
        imagePanel;
        navigationPanel
        questionPanel;
        titlePanel;
        answerPanel;
        scoredImagePanel;
        imageContextPanel;
        messagePanel;
        scoredTextPanel;
        feedbackPanel;
        trainingImagePanel;
        trainingTextPanel;
        mostRecentQAFlavor;
        resultsTopPanel;
        resultsLeftPanel;
        %resultsMiddlePanel;
        resultsRightPanel;
        imageBrowserPanel;
        
        scoredSetTitlePanel;
        scoredSetNavigationPanel;
        scoredSetMetadataPanel;
        checkboxPanePanel;
        imageNavigationPanel;
        
        lineHeight = 30;
        pushButtonWidth = 80;
        fontname = 'Helvetica';
        %fontname = 'Calibri';
        %fontname = 'Times New Roman';
        fontsize = 13;
        %fontsize = 14;
        fontsizeHeader = 16;
        fullWidth = 1260;
        fullHeight = 750;
        fig;
        figurePosition;
        
    end
    
    methods
        function obj = UI()
            screensize = get(0,'ScreenSize');
            xpos = ceil((screensize(3)-obj.fullWidth)/2); % center the figure on the screen horizontally
            ypos = ceil((screensize(4)-obj.fullHeight)/2); % center the figure on the screen vertically
            obj.figurePosition =  [xpos ypos obj.fullWidth obj.fullHeight];
            obj.fig = figure('position', obj.figurePosition ,... 
                'MenuBar', 'none' ,...
                'Name', 'AVATOL Computer Vision System',...
                'Color', [1 1 1]);
            %movegui(obj.fig,'center')
        end
        function deleteObsoleteControls(obj)
            obj.deleteControls(obj.activeControlTags);
            obj.activeControlTags = {};
            obj.deleteControls(obj.activePanelTags);
            obj.activePanelTags = {};
        end

        function deleteControls(obj, controlTags)
            handles = guihandles();
            if (not(isempty(handles)))
                for i=1:length(controlTags)
                    tag = controlTags{i};
                    %control = findobj('Tag',tag);
                    try
                        control = getfield(handles, tag);
                        delete(control);
                    catch ME
                        foo = 3;
                    end
                end
            end
        end

        function createSimpleTextScreenPanels(obj)
        
            obj.textPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'textPanel',...
                                  'Position',[0.2 0.3 0.6 0.4]);
                             
      
            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'navigationPanel',...
                                  'Position',obj.getNavigationPanelPosition());
            obj.activePanelTags = { 'textPanel', 'navigationPanel'};
        end
        
        function createTutorialPanels(obj)
            obj.titlePanel = obj.createTitlePanel();
                              
            obj.textPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'textPanel',...
                                  'Position',[0.05 0.70 0.9 0.18]);
                             
            obj.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag', 'imagePanel',...
                                  'Position',[ 0.15 0.2 0.7 0.60]);
                              
            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'navigationPanel',...
                                  'Position',obj.getNavigationPanelPosition());
            obj.activePanelTags = { 'titlePanel', 'textPanel', 'imagePanel', 'navigationPanel'}; 
        end
        %
        %
        %
        
 
        function titlePanel = createTitlePanel(obj)
            titlePanel = uipanel('Background', [1 1 1],...%[1 0.5 0.5]
                                      'BorderType', 'none',... %etchedin
                                      'Tag','titlePanel',...
                                      'Position',[0.02 0.89 .96 0.07]);

        end

        function createChoiceQAPanels(obj)
            obj.titlePanel = obj.createTitlePanel();

            obj.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'questionPanel',...
                                      'Position',[0.05 0.70 0.67 0.18]);

            obj.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'answerPanel',...
                                      'Position',[0.74 0.1 0.21 0.77]);

            obj.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                      'BorderType', 'none',...
                                      'Tag', 'imagePanel',...
                                      'Position',[ 0.02 0.1 0.7 0.60]);

            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'navigationPanel',...
                                      'Position',obj.getNavigationPanelPosition());
            obj.mostRecentQAFlavor = 'choice';
            obj.activePanelTags = {  'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel'}; 
        end


        function createResultsReviewPanelsOld(obj)
            %obj.titlePanel = obj.createTitlePanel();
            % [0.02 0.02 0.96 0.05]; is navigation panel
            obj.resultsLeftPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsLeftPanel',...
                                      'Position',[0.02 0.1 0.20 0.86]);
            obj.resultsMiddlePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsMiddlePanel',...
                                      'Position',[0.24 0.1 0.30 0.86]);
            obj.resultsRightPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsRightPanel',...
                                      'Position',[0.54 0.1 0.42 0.86]);
                                  
            %
            obj.scoredSetTitlePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoreSetTitlePanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.92 1.0 0.10]);
                                  
            obj.scoredSetNavigationPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoredSetNavigationPanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.85 1.0 0.05]);
                                  
            obj.scoredSetMetadataPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoredSetMetadataPanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.0 1.0 0.83]);
                                  
                                  
            obj.checkboxPanePanel = uipanel('Background', [0 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'checkboxPanelPanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.92 1.0 0.10]);

            obj.imageNavigationPanel = uipanel('Background', [1 1 1],...
                                      'BorderType', 'none',...
                                      'Tag', 'imageNavigationPanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.85 1.0 0.05]);
                                                               
            obj.imagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'imagePanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.1 1.0 0.73]);
                             
            obj.imageContextPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'imageContextPanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.00 1.0 0.05]);
                                              

            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'navigationPanel',...
                                      'Position',obj.getNavigationPanelPosition());
            obj.mostRecentQAFlavor = 'NA';
            obj.activePanelTags = {  'scoreSetTitlePanel', 'scoredSetNavigationPanel', 'scoredSetMetadataPanel', 'checkboxPanelPanel', 'imageContextPanel','imageNavigationPanel',  'imagePanel', 'navigationPanel'}; 
        end
        function createResultsReviewPanels(obj)
            %obj.titlePanel = obj.createTitlePanel();
            % [0.02 0.02 0.96 0.05]; is navigation panel
            obj.resultsTopPanel =  uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsTopPanel',...
                                      'Position',[0.00 0.95 0.96 0.05]);
            obj.resultsLeftPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsLeftPanel',...
                                      'Position',[0.00 0.05 0.35 0.9]);
           % obj.resultsMiddlePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
           %                           'BorderType', 'none',...
           %                           'Tag', 'resultsMiddlePanel',...
           %                           'Position',[0.24 0.1 0.30 0.86]);
            obj.resultsRightPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'resultsRightPanel',...
                                      'Position',[0.35 0.05 0.65 0.9]);
                                  
            %
            obj.scoredSetTitlePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoreSetTitlePanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.92 1.0 0.10]);
                                  
            obj.scoredSetNavigationPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoredSetNavigationPanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.85 1.0 0.05]);
                                  
            obj.scoredSetMetadataPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'scoredSetMetadataPanel',...
                                      'Parent', obj.resultsLeftPanel,...
                                      'Position',[0.0 0.0 1.0 0.83]);
                                
                                                               
            obj.imageBrowserPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'imageBrowserPanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.1 1.0 0.9]);
                             
            obj.imageContextPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'imageContextPanel',...
                                      'Parent', obj.resultsRightPanel,...
                                      'Position',[0.0 0.00 1.0 0.05]);
                                              

            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag', 'navigationPanel',...
                                      'Position',obj.getNavigationPanelPosition());
            obj.mostRecentQAFlavor = 'NA';
            obj.activePanelTags = { 'resultsTopPanel', 'scoreSetTitlePanel', 'scoredSetNavigationPanel', 'scoredSetMetadataPanel', 'checkboxPanelPanel', 'imageContextPanel','imageBrowserPanel', 'navigationPanel'}; 
        end

        function createCheckboxChoicePanels(obj)
        
            obj.titlePanel = obj.createTitlePanel();
                              
            obj.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','questionPanel',...
                                  'Position',[0.05 0.70 0.95 0.18]);
                              
            obj.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','answerPanel',...
                                  'Position',[0.05 0.1 0.95 0.67]);
                              
            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','navigationPanel',...
                                  'Position',obj.getNavigationPanelPosition());
                              
            obj.mostRecentQAFlavor = 'checkedInput';
            obj.activePanelTags = { 'titlePanel', 'questionPanel', 'answerPanel', 'navigationPanel' };
        end
        
        
        function createPopupChoicePanels(obj)
        
            obj.titlePanel = obj.createTitlePanel();
                              
            obj.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','questionPanel',...
                                  'Position',[0.05 0.70 0.95 0.18]);
                              
            obj.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','answerPanel',...
                                  'Position',[0.05 0.65 0.95 0.18]);
                              
            obj.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag','imagePanel',...
                                  'Position',[ 0.02 0.1 0.96 0.60]);
                              
            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','navigationPanel',...
                                  'Position',obj.getNavigationPanelPosition());
                              
            obj.mostRecentQAFlavor = 'typedInput';
            obj.activePanelTags = { 'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel' };
        end
        function createResultsReviewPanelsObsolete(obj)
            obj.titlePanel = createTitlePanel();

            obj.messagePanel     = uipanel('Background', [1 1 1],'BorderType','none','Tag','messagePanel',     'Position',[0.05 0.70 0.67 0.18]);
            obj.scoredImagePanel = uipanel('Background', [1 1 1],'BorderType','none','Tag','scoredImagePanel', 'Position',[0.74 0.70 0.21 0.18]);

            obj.scoredTextPanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                      'BorderType', 'none',...
                                      'Tag','scoredTextPanel',...
                                      'Position',[ 0.02 0.1 0.96 0.60]);

            obj.trainingImagePanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                      'BorderType', 'none',...
                                      'Tag','trainingImagePanel',...
                                      'Position',[0.74 0.70 0.21 0.18]);

            obj.trainingTextPanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                      'BorderType', 'none',...
                                      'Tag','trainingTextPanel',...
                                      'Position',[ 0.02 0.1 0.96 0.60]);

            obj.feedbackPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag','feedbackPanel',...
                                      'Position',obj.getNavigationPanelPosition());

            obj.activePanelTags = { 'titlePanel', 'messagePanel', 'scoredImagePanel', 'scoredTextPanel', 'trainingImagePanel', 'trainingTextPanel', 'feedbackPanel' };
        end
        function createTypedInputQAPanels(obj)
            obj.titlePanel = createTitlePanel();

            obj.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag','questionPanel',...
                                      'Position',[0.05 0.70 0.67 0.18]);

            obj.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                      'BorderType', 'none',...
                                      'Tag','answerPanel',...
                                      'Position',[0.74 0.70 0.21 0.18]);

            obj.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                      'BorderType', 'none',...
                                      'Tag','imagePanel',...
                                      'Position',[ 0.02 0.1 0.96 0.60]);

            obj.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                      'BorderType', 'none',...
                                      'Tag','navigationPanel',...
                                      'Position',obj.getNavigationPanelPosition());

            obj.mostRecentQAFlavor = 'typedInput';
            obj.activePanelTags = { 'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel' };
        end

 
        function control = getCharacterNameTitle(obj, titleString)
            control = uicontrol('style', 'text' ,...
                                     'String', titleString ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', obj.fontname ,...
                                     'FontSize', obj.fontsizeHeader ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','titleText' ,...
                                     'Parent',obj.titlePanel,...
                                     'HorizontalAlignment', 'center');
      
        end
        %
        % Positions
        %

        function position = getNavigationPanelPosition(obj)
            position = [0.02 0.02 0.96 0.05];
        end


        function answerPosition = getInputAnswerPosition(obj)
            answerPosition = [ 0,0.7,1, 0.3 ];
        end

        function answerPosition = getChoiceAnswerPosition(obj,answerIndex)

            answerPanelPosition = getpixelposition(obj.answerPanel,1);
            answerPanelWidth = answerPanelPosition(3);
            answerPanelHeight = answerPanelPosition(4);

            answerPositionY = answerPanelHeight -obj.lineHeight - (answerIndex*obj.lineHeight);
            answerPosition = [ 0 answerPositionY answerPanelWidth obj.lineHeight];
        end

        function navButtonPosition = getButtonPositionRightBig(obj)
            navButtonPosition = [0.75,0,0.2,1 ];
        end


        function navButtonPosition = getButtonPositionRightA(obj)
            navButtonPosition = [0.8,0,0.1,1 ];
        end

        function navButtonPosition = getButtonPositionRightB(obj)
            navButtonPosition = [0.9,0,0.1,1 ];
        end

        function navButtonPosition = getButtonPositionLeft(obj)
            navButtonPosition = [0,0,0.3,1 ];
        end

        %
        % image display
        %

        function displaySingleImage(obj, images)
            image1Path = images(1).imageFilePath;
            axes1Panel = uipanel('Parent',obj.imagePanel,...
                                 'Tag','image1panel' ,...
                                 'position',[0,0,1,1]);
            axes1 = axes('Parent',axes1Panel,...
                                 'Color',[1,1,1],...
                                 'FontName', obj.fontname ,...
                                 'FontSize', obj.fontsize ,...
                                 'Tag','image1' ,...
                                 'position',[0.02,0.2,0.96,0.76]);%[0.02,0.02,0.96,0.96]
            obj.activeControlTags = [ obj.activeControlTags, 'image1panel' ];
            imshow(image1Path);
            xlabel(images(1).imageCaption);
        end

        function displayImagePair(obj, images)
            image1Path = images(1).imageFilePath;
            axes1Panel = uipanel('Parent',obj.imagePanel,...
                                 'Tag','image1panel' ,...
                                 'position',[0,0,0.5,1]);
            axes1 = axes('Parent',axes1Panel,...
                                 'Color',[1,1,1],...
                                 'FontName', obj.fontname ,...
                                 'FontSize', obj.fontsize ,...
                                 'Tag','image1' ,...
                                 'position',[0.02,0.02,0.96,0.96]);
            obj.activeControlTags = [ obj.activeControlTags, 'image1panel' ];
            imshow(image1Path);
            xlabel(images(1).imageCaption);
            image2Path = images(2).imageFilePath;
            axes2Panel = uipanel('Parent',obj.imagePanel,...
                                 'Tag','image2panel' ,...
                                 'position',[0.5,0,0.5,1]);
            axes2 = axes('Parent',axes2Panel,...
                                 'Color',[1,1,1],...
                                 'FontName', obj.fontname ,...
                                 'FontSize', obj.fontsize ,...
                                 'Tag','image2' ,...
                                 'position',[0.02,0.02,0.96,0.96]);
            obj.activeControlTags = [ obj.activeControlTags, 'image2panel' ];
            imshow(image2Path);
            xlabel(images(2).imageCaption);
        end

        function displayImages(obj, images)
            imageCount = length(images);
            if (imageCount == 1)
                obj.displaySingleImage(images);
            elseif (imageCount == 2)
                obj.displayImagePair(images);
            end
        end


        function result = verifyAnswerPresent(obj)
            result = 1;
            if (strcmp(obj.activeControlType,'edit'))
                answer = get(obj.activeAnswerControl, 'String');
                if (strcmp(answer,''))
                    result = 0;
                end
            elseif (strcmp(obj.activeControlType,'popupMenu'))
                % something always chosen
                result = 1;
            else % must be 'choice'
                answer = get(obj.activeAnswerControl,'SelectedObject');
                if (isempty(answer))
                    result = 0;
                end
            end
        end
        function setPopupMenuToValue(obj, popupMenu, stringsLoadedIntoPopupMenu, value)
            indexArray = find(ismember(stringsLoadedIntoPopupMenu,value));
            if (isempty(indexArray))
                msg = sprintf('Invalid choice for popupmenu: %s', value);
                err = MException('UI:BadPopupEntry', msg);
                throw(err);
            else
                set(popupMenu,'value',indexArray);
            end
        end

    end
    
end

