classdef DPMQuestionScreens < handle
    %MATRIXCHOICE Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
       ui;
       session;
       
    end
    
    methods
        function obj = DPMQuestionScreens(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        
        function showFirstQuestion(obj)
            obj.ui.deleteObsoleteControls();
            obj.ui.createCheckboxChoicePanels();

            presenceAbsenceCharacterNamesJavaList = obj.session.morphobankBundle.getScorableCharacterNames();
            presenceAbsenceCharacterNames = obj.session.javaStringListToMatlabCharList(presenceAbsenceCharacterNamesJavaList);
            
            
            LEFT OFF -  TRY FAKING THE SCROLLBAR LIKE IN SLIDER TEST
            
            prompt = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.questionPanel,...
                                         'String', 'Please specify which of these are simple presence/absence characters.' ,...
                                         'position', [0,0,1,1] ,...
                                         'Units','normalized',...
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
            jList.add(0,'First');
            jList.add(1,'Second');
            jList.add(2,'Third');
            jList.add(3,'and last');


            % Next prepare a CheckBoxList component within a scroll-pane
            jCBList = com.mathworks.mwswing.checkboxlist.CheckBoxList(jList);
            jCBList.setFont(java.awt.Font(obj.ui.fontname,java.awt.Font.PLAIN,obj.ui.fontsize));
            jScrollPane = com.mathworks.mwswing.MJScrollPane(jCBList);
 
            % Now place this scroll-pane within a Matlab container (figure or panel)
            %[jhScroll,hContainer] = javacomponent(jScrollPane,[10,10,80,65],obj.ui.answerPanel);
            %[jhScroll,hContainer] = javacomponent(jScrollPane,[0.0,0.0,0.9,0.9],obj.ui.answerPanel);
            [jhScroll,hContainer] = javacomponent(jScrollPane,[10,10,600,300],obj.ui.answerPanel);
                                         %'FontName', obj.ui.fontname ,...
                                         %'FontSize', obj.ui.fontsize);
            %set(jhScroll,'units','norm', 'position',[0.2,0.3,0.4,0.5]);
            % Update some items' state programmatically
            jCBModel = jCBList.getCheckModel;
            jCBModel.checkAll;
            jCBModel.uncheckIndex(1);
            jCBModel.uncheckIndex(3);
 
            % Respond to checkbox update events
            jhCBModel = handle(jCBModel, 'CallbackProperties');
            set(jhCBModel, 'ValueChangedCallback', @myMatlabCallbackFcn);  

                                     
 %           checkboxes =  uibuttongroup('Parent',obj.ui.answerPanel,...
%                                         'Units','normalized',...
%                                         'Background',[0.5 0.2 1],...
 %                                        'Position',[ 0,0.71,.9, 0.4 ]);                       
%            
 %           obj.ui.activeControlTags = { 'prompt' };
%            for i=1:length(presenceAbsenceCharacterNames)
%                charName = char(presenceAbsenceCharacterNames(i));
                
 %               cbh = uicontrol('Parent',checkboxes,'Style','checkbox',...
 %                                        'Units','normalized',...
 %                                        'String',charName,...
 %                                        'Tag',charName,...
 %                                        'position', [0,0,1,1] ,...
  %                                       'Value',1);
 %               obj.ui.activeControlTags = [ obj.ui.activeControlTags , charName ];
  %          end

            

            next = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Next' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','next' ,...
                                         'BackgroundColor', [0.5 0.5 0.5]);  
           
            obj.ui.activeControlTags = [ obj.ui.activeControlTags , 'next' ];
            obj.ui.activeAnswerControl = hContainer;
            obj.session.activeQuestionId = 'dpmQuestionSimplePresenceAbsenceChars';
            obj.ui.activeControlType = 'buttonGroup';

            set(next, 'callback', {@obj.showNextQuestion});
            obj.session.mostRecentScreen = 'DPM_QUESTION_SIMPLE_PRESENCE_ABSENCE'; 
        end
        
        function showNextQuestion(obj, hObject, eventData)
            if strcmp(obj.session.activeQuestionId,'dpmQuestionSimplePresenceAbsenceChars')
                
                % need to persist answer !
                
                obj.showSecondScreen();
            else
                
                % need to persist answer !
                
                obj.session
            end    
        end
    end
    
end

