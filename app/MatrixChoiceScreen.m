classdef MatrixChoiceScreen < handle
    %MATRIXCHOICE Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
       ui;
       session;
       matrixChoiceWidget;
       chosenMatrix;
       matrixChoiceIndex = 1;
       matrixChoices;
       matrixDir;
       statusLabel;
       progressBar;
    end
    
    methods
        function obj = MatrixChoiceScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        function setMatrixChoice(obj,hObject, eventData)
            obj.matrixChoiceIndex = get(obj.matrixChoiceWidget, 'value');
            matrixList = get(obj.matrixChoiceWidget, 'string');
            obj.chosenMatrix = char(matrixList(obj.matrixChoiceIndex));
        end
        function registerAnswer(obj)
            obj.matrixChoiceIndex = get(obj.matrixChoiceWidget,'value');
            obj.chosenMatrix = char(obj.matrixChoices(obj.matrixChoiceIndex));
            obj.registerMatrixChoice(obj.chosenMatrix);
        end    
        function registerMatrixChoice(obj, matrixName)
            obj.chosenMatrix = matrixName; % as this is called from Session as part of hack
            if ispc()
                 obj.matrixDir = sprintf('%s\\matrix_downloads\\%s', obj.session.rootDir, matrixName);
            else
                 obj.matrixDir = sprintf('%s/matrix_downloads/%s',  obj.session.rootDir, matrixName);
            end
            matrixNameJavaString = java.lang.String(matrixName);
            edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle.printString(matrixNameJavaString);
            obj.session.morphobankBundle = obj.session.morphobankData.loadMatrix(matrixNameJavaString);
            statusPanel = obj.session.morphobankBundle.getStatusPanel();
            %obj.statusPanel = obj.session.morphobankBundle.getJPanel();
            %position = obj.ui.getStatusPanelPosition();
            statusPanel.setLabel(obj.statusLabel);
            statusPanel.setProgressBar(obj.progressBar);
            obj.session.morphobankBundle.init();
            obj.session.javaUI.setCurrentBundle(obj.session.morphobankBundle);
        end    
        
        function registerMatrixChoiceNoProgressBar(obj, matrixName)
            obj.chosenMatrix = matrixName; % as this is called from Session as part of hack
            if ispc()
                 obj.matrixDir = sprintf('%s\\matrix_downloads\\%s', obj.session.rootDir, matrixName);
            else
                 obj.matrixDir = sprintf('%s/matrix_downloads/%s',  obj.session.rootDir, matrixName);
            end
            matrixNameJavaString = java.lang.String(matrixName);
            edu.oregonstate.eecs.iis.avatolcv.mb.MorphobankBundle.printString(matrixNameJavaString);
            obj.session.morphobankBundle = obj.session.morphobankData.loadMatrix(matrixNameJavaString);
   
            obj.session.morphobankBundle.init();
            obj.session.javaUI.setCurrentBundle(obj.session.morphobankBundle);
        end    
        function displayMatrixQuestion(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createPopupChoicePanels();

            matrixChoicePrompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'Units','normalized',...
                                         'String', 'Which matrix is the character in?' ,...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','matrixChoicePrompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            obj.matrixChoiceWidget = uicontrol('style', 'popupmenu' ,...
                                         'Parent',obj.ui.answerPanel,...
                                         'Units','normalized',...
                                         'position', [ 0,0.71,.9, 0.29 ] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','matrixChoice' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            matrixDirNames = obj.session.javaStringListToMatlabCharList(obj.session.morphobankData.getMatrixNames());
            set(obj.matrixChoiceWidget,'string',matrixDirNames);
            set(obj.matrixChoiceWidget,'Value',obj.matrixChoiceIndex);
            obj.matrixChoices = matrixDirNames;

            tutorial = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Tutorial' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', [0,0,0.15,1],...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','tutorial' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
                                     
            skipToResultsReviewButtonPosition = [0.17,0,0.23,1 ]; 
            skipToResultsReview = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Review Results' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', skipToResultsReviewButtonPosition ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','skipToResultsReview' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  

            next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            
            import javax.swing.JLabel;
            import javax.swing.JProgressBar;
            obj.statusLabel = JLabel();
            obj.progressBar = JProgressBar();
            
            [statusPanelHandle,hContainer] = javacomponent(obj.statusLabel,[500,0,200,36],obj.ui.navigationPanel);
            
            
            [statusPanelHandle,hContainer] = javacomponent(obj.progressBar,[720,0,300,36],obj.ui.navigationPanel);
            %H.activeControlTags = { 'matrixChoice',  'matrixChoicePrompt', 'next', 'tutorial' };
            
              if (obj.session.scoredSetMetadatas.hasSessionData())
                set(skipToResultsReview, 'Enable', 'on');
            else
                set(skipToResultsReview, 'Enable', 'inactive');
                set(skipToResultsReview, 'BackgroundColor', [0.8 0.8 0.8]);
                set(skipToResultsReview, 'ForegroundColor', [0.6 0.6 0.6]);
              end
             
            obj.ui.activeControlTags = { 'matrixChoicePrompt','skipToResultsReview',  'next', 'tutorial' };
            obj.ui.activeAnswerControl = obj.matrixChoiceWidget;
            obj.session.activeQuestionId = 'matrixQuestion';
            obj.ui.activeControlType = 'popupMenu';

            set(next, 'callback', {@obj.showNextQuestion});
            set(tutorial, 'callback', {@obj.jumpToTutorial});
            set(skipToResultsReview, 'callback', {@obj.jumpToResultsReview});
            set(obj.matrixChoiceWidget, 'callback', {@obj.setMatrixChoice});
            obj.session.mostRecentScreen = 'MATRIX_QUESTION'; 
        end
        function showNextQuestion(obj, hObject, eventData)
            currentFigure = gcf;
            set(currentFigure, 'Pointer', 'watch');
            obj.session.registerDisplayedAnswer();
            obj.session.characterChoiceScreen.showCharacterQuestion();
        end
        function jumpToTutorial(obj, hObject, eventData)
            obj.session.jumpToTutorial();
        end
        function jumpToResultsReview(obj,hObject, eventData)
            obj.session.jumpToResultsReview();
        end
    end
    
end

