classdef WelcomeScreen < handle
    %WELCOME Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
       ui;
       session;
    end
    
    methods 
        function obj = WelcomeScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        function displayWelcomeScreen(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createSimpleTextScreenPanels();
            introText = ['Welcome to the AVATOL Computer Vision System.  '...
                         'Click the buttons below to start the questionnaire, examine results or do the tutorial.'];

            tutorialText = uicontrol('style', 'text' ,...
                                         'String', introText ,...
                                         'Units','normalized',...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'BackgroundColor', [1 1 1] ,...
                                         'Tag','tutorialText' ,...
                                         'Parent',obj.ui.textPanel,...
                                         'HorizontalAlignment', 'left');

            beginTutorialPosition = [0.75,0,0.25,1 ];  
            skipToQuestionnaireButtonPosition = [0,0,0.25,1 ];
            skipToResultsReviewButtonPosition = [0.25,0,0.25,1 ];

            beginTutorial = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Begin Tutorial' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', beginTutorialPosition,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','beginTutorial' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  


            skipToQuestionnaire = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Skip to Questionnaire' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', skipToQuestionnaireButtonPosition ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','skipToQuestionnaire' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  
            skipToResultsReview = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Skip to Results Review' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', skipToResultsReviewButtonPosition ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','skipToResultsReview' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  

            obj.ui.activeControlTags = { 'tutorialText', 'beginTutorial', 'skipToQuestionnaire', 'skipToResultsReview' };   
            if (obj.session.scoredSetMetadatas.hasSessionData())
                set(skipToResultsReview, 'Enable', 'on');
            else
                set(skipToResultsReview, 'Enable', 'inactive');
                set(skipToResultsReview, 'BackgroundColor', [0.8 0.8 0.8]);
                set(skipToResultsReview, 'ForegroundColor', [0.6 0.6 0.6]);
            end
            
            obj.session.activeScreen = 'WelcomeScreen';
            set(beginTutorial, 'callback', {@obj.startTutorial});
            set(skipToQuestionnaire, 'callback', {@obj.jumpToQuestionnaire});
            set(skipToResultsReview, 'callback', {@obj.jumpToResultsReview});
            

        end
        function startTutorial(obj,hObject, eventData)
            obj.session.startTutorial();
        end
        function jumpToQuestionnaire(obj,hObject, eventData)
            obj.session.jumpToQuestionnaire();
            
        end
        function jumpToResultsReview(obj,hObject, eventData)
            obj.session.jumpToResultsReview();
        end
    end
    
    
end

