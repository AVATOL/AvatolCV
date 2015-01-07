classdef DPMQuestionScreens < handle
    %MATRIXCHOICE Summary of this class goes here
    %   Ask two questions special to the DPM:
    % First, specify which chars are simple characters
    % second, which "view"  is desired
    
    properties
       ui;
       session;
       %taxonNameChoice;
       %taxonChoiceWidget;
       %taxonChoiceIndex = 1;
       %taxonChoices;
       %chosenTaxon;
       jCBList;
       simplePresenceAbsenceCharacters;
       viewChoiceWidget;
       chosenView;
       viewChoiceIndex = 1;
       viewChoices;
    end
    
    methods
        function obj = DPMQuestionScreens(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        %function setTaxonChoice(obj,hObject, eventData)
        %    obj.taxonChoiceIndex = get(obj.taxonChoiceWidget, 'value');
        %    taxonList = get(obj.taxonChoiceWidget, 'string');
         %   obj.chosenTaxon = char(taxonList(obj.taxonChoiceIndex));
        %end
        function showFirstQuestionObsolete(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createPopupChoicePanels();

            taxonChoicePrompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'Units','normalized',...
                                         'String', 'Which taxon do you want to score?' ,...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','taxonChoicePrompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            obj.taxonChoiceWidget = uicontrol('style', 'popupmenu' ,...
                                         'Parent',obj.ui.answerPanel,...
                                         'Units','normalized',...
                                         'position', [ 0,0.71,.9, 0.29 ] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','taxonChoice' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            taxonNames = obj.session.javaStringListToMatlabCharList(obj.session.morphobankBundle.getScorableTaxonNames());
            set(obj.taxonChoiceWidget,'string',taxonNames);
            set(obj.taxonChoiceWidget,'Value',obj.taxonChoiceIndex);
            obj.taxonChoices = taxonNames;
            obj.chosenTaxon = char(taxonNames(obj.taxonChoiceIndex));

            next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            %H.activeControlTags = { 'matrixChoice',  'matrixChoicePrompt', 'next', 'tutorial' };
            obj.ui.activeControlTags = { 'taxonChoicePrompt', 'taxonChoice', 'next' };
            obj.ui.activeAnswerControl = obj.taxonChoiceWidget;
            obj.session.activeQuestionId = 'DPMTaxonQuestion';
            obj.ui.activeControlType = 'popupMenu';

            set(next, 'callback', {@obj.showNextQuestion});
            set(obj.taxonChoiceWidget, 'callback', {@obj.setTaxonChoice});
            obj.session.mostRecentScreen = 'DPM_TAXON_QUESTION'; 
        end
        
        
        function showFirstQuestion(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createCheckboxChoicePanels();

            presenceAbsenceCharacterNamesJavaList = obj.session.morphobankBundle.getScorableCharacterNames();
            presenceAbsenceCharacterNames = obj.session.javaStringListToMatlabCharList(presenceAbsenceCharacterNamesJavaList);
            
            
            % COULD TRY FAKING THE SCROLLBAR LIKE IN SLIDER TEST, but for
            % now, stick with using the java component
            instructions = 'Please specify which of the following presence/absence characters refer to only a single part. ';
            prompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'String', instructions ,... 
                                         'Units','normalized',...
                                         'position', [0,0,1,1] ,...%changed this last [0,0,800,100]
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','prompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
            obj.ui.activeControlTags = { 'prompt' };
            % First create the data model
            import com.mathworks.mwswing.checkboxlist.CheckBoxList;
            import com.mathworks.mwswing.MJScrollPane;
            jList = java.util.ArrayList;  % any java.util.List will be ok
            for i=1:length(presenceAbsenceCharacterNames)
                charName = char(presenceAbsenceCharacterNames(i));
                jList.add(i-1,charName);
            end    
           
            % Next prepare a CheckBoxList component within a scroll-pane
            obj.jCBList = com.mathworks.mwswing.checkboxlist.CheckBoxList(jList);
            cellRenderer = obj.jCBList.getCellRenderer();
            %desiredFont = java.awt.Font(obj.ui.fontname,java.awt.Font.PLAIN,obj.ui.fontsize);
            desiredFont = java.awt.Font(obj.ui.fontname,java.awt.Font.PLAIN,14);
            %obj.jCBList.setFont(desiredFont);
            cellRenderer.setFont(desiredFont);
            jScrollPane = com.mathworks.mwswing.MJScrollPane(obj.jCBList);
            
 
            % Now place this scroll-pane within a Matlab container (figure or panel)
            %[jhScroll,hContainer] = javacomponent(jScrollPane,[10,10,80,65],obj.ui.answerPanel);
            %[jhScroll,hContainer] = javacomponent(jScrollPane,[0.0,0.0,0.9,0.9],obj.ui.answerPanel);
            [jhScroll,hContainer] = javacomponent(jScrollPane,[10,0,800,400],obj.ui.answerPanel);
                                         %'FontName', obj.ui.fontname ,...
                                         %'FontSize', obj.ui.fontsize);
            %set(jhScroll,'units','norm', 'position',[0.2,0.3,0.4,0.5]);
            % Update some items' state programmatically
            jCBModel = obj.jCBList.getCheckModel;
            jCBModel.uncheckAll;
            
            % Respond to checkbox update events
            %jhCBModel = handle(jCBModel, 'CallbackProperties');
 
            next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
           
            obj.ui.activeControlTags = [ obj.ui.activeControlTags , 'next' ];
            obj.ui.activeAnswerControl = hContainer;
            obj.session.activeQuestionId = 'dpmQuestionSimplePresenceAbsenceChars';
            obj.ui.activeControlType = 'buttonGroup';

            set(next, 'callback', {@obj.showNextQuestion});
            obj.session.mostRecentScreen = 'DPM_QUESTION_SIMPLE_PRESENCE_ABSENCE'; 
        end
        
        
        function showNextQuestion(obj, hObject, eventData)
            %if strcmp(obj.session.activeQuestionId,'DPMTaxonQuestion')
            %    obj.taxonNameChoice = get(obj.taxonChoiceWidget, 'value');
            %    obj.showSecondQuestion();
            %else
            if strcmp(obj.session.activeQuestionId,'dpmQuestionSimplePresenceAbsenceChars')
                checkedValues = obj.jCBList.getCheckedValues();
                obj.simplePresenceAbsenceCharacters = obj.session.javaStringListToMatlabCharList(checkedValues);
                obj.showSecondQuestion();
            else
                obj.session.doneWithDPMQuestions();
            end    
        end
        
        function showSecondQuestion(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createPopupChoicePanels();

            viewChoicePrompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'Units','normalized',...
                                         'String', 'Which view do you want scored?' ,...
                                         'position', [0,0,1,1] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','viewChoicePrompt' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            obj.viewChoiceWidget = uicontrol('style', 'popupmenu' ,...
                                         'Parent',obj.ui.answerPanel,...
                                         'Units','normalized',...
                                         'position', [ 0,0.71,.9, 0.29 ] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','viewChoiceWidget' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...

            viewNames = obj.session.javaStringListToMatlabCharList(obj.session.morphobankBundle.getViewNames());
            set(obj.viewChoiceWidget,'string',viewNames);
            set(obj.viewChoiceWidget,'Value',obj.viewChoiceIndex);
            obj.viewChoices = viewNames;
            obj.chosenView = char(viewNames(obj.viewChoiceIndex));
            next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            obj.ui.activeControlTags = { 'viewChoicePrompt', 'next', 'viewChoiceWidget' };
            obj.ui.activeAnswerControl = obj.viewChoiceWidget;
            obj.session.activeQuestionId = 'DPMViewQuestion';
            obj.ui.activeControlType = 'popupMenu';

            set(next, 'callback', {@obj.showNextQuestion});
            set(obj.viewChoiceWidget, 'callback', {@obj.setViewChoice});
            obj.session.mostRecentScreen = 'VIEW_QUESTION';  
        end    
        function setViewChoice(obj,hObject, eventData)
            obj.viewChoiceIndex = get(obj.viewChoiceWidget, 'value');
            viewList = get(obj.viewChoiceWidget, 'string');
            obj.chosenView = char(viewList(obj.viewChoiceIndex));
        end 
    end
    
end

