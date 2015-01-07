classdef CharacterChoiceScreen < handle
    %CHARACTERCHOICE Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
         characterChoiceIndex = 1;
         ui;
         session;
         characterName;
         characterIdJavaString;
         characterChoiceWidget;
         characterChoices;
    end
    
    methods
        function obj = CharacterChoiceScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        
        function setCharacterChoice(obj, hObject, eventData)
            obj.setCharacterInfo();
        end
        function setCharacterInfo(obj)
            obj.characterChoiceIndex = get(obj.characterChoiceWidget, 'value');
            characterList = get(obj.characterChoiceWidget, 'string');
            obj.characterName = char(characterList(obj.characterChoiceIndex));
            obj.characterIdJavaString = obj.session.morphobankBundle.getCharacterIdForName(java.lang.String(obj.characterName));
        end
        function showCharacterQuestion(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createPopupChoicePanels();

            characterChoicePrompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'Units','normalized',...
                                         'String', 'Which presence/absence character do you want to score?' ,...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','characterChoicePrompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
           obj.characterChoiceWidget = uicontrol('style', 'popupmenu' ,...
                                         'Parent',obj.ui.answerPanel,...
                                         'Units','normalized',...
                                         'position', [ 0,0.71,.9, 0.29 ] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','characterChoice' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...

            
            presenceAbsenceCharNamesJavaList = obj.session.morphobankBundle.getScorableCharacterNames();
            %for i=0:presenceAbsenceCharNamesJavaList.size() - 1
            %    fprintf('matlab name %s\n',char(presenceAbsenceCharNamesJavaList.get(i)));
            %end

            presenceAbsenceCharNames = obj.session.javaStringListToMatlabCharList(presenceAbsenceCharNamesJavaList);
            %for i=1:length(presenceAbsenceCharNames)
            %    fprintf('presenceAbsenceCharName : %s',char(presenceAbsenceCharNames(i)));
            %end

            tutorial = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Tutorial' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'position', [0,0,0.15,1],...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','tutorial' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  


            prev = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Back' ,...
                                         'Units','normalized',...
                                         'position', obj.ui.getButtonPositionRightA() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Tag','prev' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            if (length(presenceAbsenceCharNames) == 0)
                set(characterChoicePrompt, 'string', 'No presence/absence characters with training data detected. Go back and try another matrix.');
                obj.ui.activeControlTags = {'characterChoicePrompt', 'tutorial', 'prev' };
            else 
                next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]); 

                set(next, 'callback', {@obj.showNextQuestion});
                obj.characterChoices = presenceAbsenceCharNames;
                set(obj.characterChoiceWidget,'string',obj.characterChoices);
                %H.characterName = char(characterNameList(1));
                set(obj.characterChoiceWidget,'Value',obj.characterChoiceIndex);
                obj.ui.activeControlTags = {'characterChoicePrompt', 'next', 'tutorial', 'prev' };
            end
            %H.activeControlTags = {'characterChoice',  'characterChoicePrompt', 'next', 'tutorial', 'prev' };


            obj.ui.activeAnswerControl = obj.characterChoiceWidget;
            obj.session.activeQuestionId = 'characterQuestion';
            obj.ui.activeControlType = 'popupMenu';

            set(tutorial, 'callback', {@obj.jumpToTutorial});
            set(prev, 'callback', {@obj.showPrevQuestion});
            set(obj.characterChoiceWidget, 'callback', {@obj.setCharacterChoice});
            obj.session.mostRecentScreen = 'CHARACTER_QUESTION'; 
        end

        function showPrevQuestion(obj,  hObject, eventData)
            obj.session.showMatrixQuestion();
        end    
        
        function showNextQuestion(obj,  hObject, eventData)
            obj.setCharacterInfo();
            obj.session.showNextQuestion();
        end    
        function jumpToTutorial(obj,  hObject, eventData)
            obj.session.jumpToTutorial();
        end    
    end
    
end

