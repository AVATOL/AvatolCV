function avatol_cv

    %xmlFile = QuestionsXMLFile('tests/simple.xml');
    xmlFile = QuestionsXMLFile('data/Questionnaire.xml');
    qquestions = QQuestions(xmlFile.domNode);
    global H;
    H.questionSequencer = QuestionSequencer(qquestions);
    %qv = QuestionsValidator();
    %qv.validate(H.questionSequencer.qquestions.questions);
    %layout();
    %showCharacterQuestions();
    
    H.activeControlTags = {};
    layout();
    createQAPanels();
    showCharacterQuestions();
    
    %
    %  LAYOUT helper functions
    %
    
    function layout()
        
        H.lineHeight = 30;
        H.pushButtonWidth = 80;
        H.fontname = 'Helvetica';
        H.fontsize = 13;
        H.fullWidth = 900;
        H.fullHeight = 600;
        H.figurePosition =  [150 150 H.fullWidth H.fullHeight];
        H.fig = figure('position', H.figurePosition ,... 
                'MenuBar', 'none' ,...
                'Name', 'AVATOL Computer Vision System',...
                'Color', [1 1 1]);
    end

    function deleteObsoleteControls(controlTags)
        handles = guihandles();
        for i=1:length(controlTags)
            tag = controlTags{i};
            %control = findobj('Tag',tag);
            control = getfield(handles, tag);
            delete(control);
        end
    end

    function deleteQApanels()
        
        delete(H.questionPanel);
        delete(H.answerPanel);
        delete(H.imagePanel);
        delete(H.navigationPanel);
    end

    function deleteMessagePanel()
        
        delete(H.done);
        delete(H.doAnotherCharacter);
        delete(H.navigationPanel);
        delete(H.messageText);
        delete(H.messagePanel);
        delete(H.prev);
    end

    function createQAPanels()
        
        H.questionPanel = uipanel('Background', [1 0.3 0.3],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Position',[0.02 0.89 0.97 0.1]);
                              
        H.answerPanel = uipanel('Background', [0.3 1 0.3],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Position',[0.02 0.2 0.25 0.68]);
                              
        H.imagePanel = uipanel('Background', [0.3 0.3 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Position',[ 0.27 0.1 0.7 0.78]);
                              
        H.navigationPanel = uipanel('Background', [0.1 0.3 0.3],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Position',[0.02 0.01 0.97 0.08]);
        
    end

    function questionPosition = getQuestionPosition()
        
        questionPanelPosition = getpixelposition(H.questionPanel,1);
        questionPanelWidth = questionPanelPosition(3);
        questionPanelHeight = questionPanelPosition(4);
        
        questionPositionY = questionPanelHeight / 4;
        questionHeight = questionPanelHeight / 2;
        questionPosition = [ 0 questionPositionY questionPanelWidth questionHeight ];
    end

    function answerPosition = getInputAnswerPosition()
        
        answerPanelPosition = getpixelposition(H.answerPanel,1);
        answerPanelHeight = answerPanelPosition(4);
        
        answerPositionY = answerPanelHeight - H.lineHeight ;
        answerPosition = [ 0 answerPositionY 200 H.lineHeight];
    end

    function answerPosition = getChoiceAnswerPosition(answerIndex)
        
        answerPanelPosition = getpixelposition(H.answerPanel,1);
        answerPanelWidth = answerPanelPosition(3);
        answerPanelHeight = answerPanelPosition(4);
        
        answerPositionY = answerPanelHeight -H.lineHeight - (answerIndex*H.lineHeight);
        answerPosition = [ 0 answerPositionY answerPanelWidth H.lineHeight];
    end

    function navButtonPosition = getNavigationButtonPosition(buttonIndex)
        
        navButtonPanelPosition = getpixelposition(H.navigationPanel,1);
        navButtonPanelWidth = navButtonPanelPosition(3);
        
        navButtonPositionX = navButtonPanelWidth - H.pushButtonWidth - (buttonIndex*H.pushButtonWidth) - buttonIndex*5;
        navButtonPosition = [ navButtonPositionX 0 H.pushButtonWidth H.lineHeight];
    end

    

    %
    %  UI panel populators
    %
    
    function createEndMessagePanel(message) 
        
        H.messagePanel = uipanel('Background', [1 0.3 0.3],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Position',[0.2 0.4 0.6 0.2]);
                              
        H.messageText = uicontrol('style', 'text' ,...
                                     'Parent',H.messagePanel,...
                                     'Units', 'normalized',...
                                     'position', [0 0 1 1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','messageText' ,...
                                     'Background',[1 1 1],...
                                     'String', message,...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
                                 
        H.navigationPanel = uipanel('Background', [0.1 0.3 0.3],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Position',[0.02 0.01 0.97 0.08]);
                              
        H.doAnotherCharacter = uicontrol('style', 'pushbutton' ,...
                                     'String', 'More' ,...
                                     'position', getNavigationButtonPosition(1) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','doAnotherCharacter' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
                                
        H.done = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Done' ,...
                                     'position', getNavigationButtonPosition(0) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','done' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
         
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Prev' ,...
                                     'position', getNavigationButtonPosition(2) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                            
        set(H.doAnotherCharacter, 'callback', {@restart});
        set(H.done, 'callback', {@saveAndExit});
        set(H.prev, 'callback', {@backFromEndMessageScreen});
         
    end

 

    function showCharacterQuestions()
        
        deleteObsoleteControls(H.activeControlTags);
        H.characterNameInputText = uicontrol('style', 'edit' ,...
                                     'Parent',H.answerPanel,...
                                     'position', getInputAnswerPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','characterNameInputText' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...

                                 
        H.characterNamePrompt = uicontrol('style', 'text' ,...
                                     'Parent',H.questionPanel,...
                                     'String', 'What is the name of the character?' ,...
                                     'position', getQuestionPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','characterNamePrompt' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
        
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'Parent',H.navigationPanel,...
                                     'String', 'Next' ,...
                                     'position', getNavigationButtonPosition(0) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        H.activeControlTags = {'characterNameInputText'  'characterNamePrompt'  'next' };
        
        H.activeAnswerControl = H.characterNameInputText;
        H.activeQuestionId = 'characterQuestion';
        H.activeControlType = 'edit';
        
        set(H.next, 'callback', {@showNextQuestion});
    end

        
    function displayChoiceQuestion(qquestion)
        % Create the button group.
        
        
        H.buttonGroup = uibuttongroup('Visible','off',...
                                    'Tag','buttonGroup',...
                                    'BorderType','none',...
                                    'Background','white',...
                                    'Parent',H.answerPanel,...
                                    'Position',[0 0 1 1]);
                                
        
        H.activeControlTags = { 'buttonGroup' };    
        %set(H.buttonGroup,'SelectionChangeFcn',@setMostRecentChoice);
        
        H.questionText = uicontrol('style', 'text' ,...
                                     'String', qquestion.text ,...
                                     'position', getQuestionPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','questionText' ,...
                                     'Parent',H.questionPanel,...
                                     'HorizontalAlignment', 'left');
        H.activeControlTags = [ H.activeControlTags,  'questionText' ];   
        
        % Create radio buttons in the button group.       
                        
        H.radioButtons = {};
        for i=1:length(qquestion.answers)
            answerValue = qquestion.answers(i).value;
            tag = sprintf('radioButton%s',answerValue);
            
            thisButton = uicontrol('Style','radiobutton',...
                            'visible', 'on',...
                            'String',answerValue ,...
                            'Background', 'white' ,...
                            'Position',getChoiceAnswerPosition(i-1),...
                            'parent',H.buttonGroup,...
                            'Tag', tag,...
                            'HandleVisibility', 'off');
            %H.activeControlTags = [ H.activeControlTags, tag ];
            H.radioButtons = [ H.radioButtons, thisButton ];
        end

       
        set(H.buttonGroup,'SelectedObject',[]);  % No selection
        set(H.buttonGroup,'Visible','on');

        H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'position', getNavigationButtonPosition(0) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
        H.activeControlTags = [ H.activeControlTags, 'next' ];                         
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Prev' ,...
                                     'position', getNavigationButtonPosition(1) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = [ H.activeControlTags, 'prev' ];
        
        H.activeAnswerControl = H.buttonGroup;
        H.activeQuestionId = qquestion.id;
        H.activeControlType = 'buttonGroup';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.prev, 'callback', {@showPrevQuestion});
        
        if (not(isempty(qquestion.images)))
            displayImages(qquestion);
        end
    end

    function displaySingleImage(qquestion)
        image1Path = qquestion.images(1).imageFilePath;
        axes1Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image1panel' ,...
                             'position',[0,0,1,1]);
        axes1 = axes('Parent',axes1Panel,...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image1' ,...
                             'position',[0.05,0.05,0.9,0.9]);
        H.activeControlTags = [ H.activeControlTags, 'image1panel' ];
        imshow(image1Path);
        xlabel(qquestion.images(1).imageCaption);
    end

    function displayImagePair(qquestion)
        image1Path = qquestion.images(1).imageFilePath;
        axes1Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image1panel' ,...
                             'position',[0,0,0.5,1]);
        axes1 = axes('Parent',axes1Panel,...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image1' ,...
                             'position',[0.05,0.05,0.9,0.9]);
        H.activeControlTags = [ H.activeControlTags, 'image1panel' ];
        imshow(image1Path);
        xlabel(qquestion.images(1).imageCaption);
        image2Path = qquestion.images(2).imageFilePath;
        axes2Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image2panel' ,...
                             'position',[0.5,0,0.5,1]);
        axes2 = axes('Parent',axes2Panel,...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image2' ,...
                             'position',[0.05,0.05,0.9,0.9]);
        H.activeControlTags = [ H.activeControlTags, 'image2panel' ];
        imshow(image2Path);
        xlabel(qquestion.images(2).imageCaption);
end

    function displayImages(qquestion)
        imageCount = length(qquestion.images);
        if (imageCount == 1)
           displaySingleImage(qquestion);
        elseif (imageCount == 2)
            displayImagePair(qquestion);
        end
    end

    function displayIntegerInputQuestion(qquestion)
        
        H.questionText = uicontrol('style', 'text' ,...
                                     'String', qquestion.text ,...
                                     'position', getQuestionPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','questionText' ,...
                                     'Parent',H.questionPanel,...
                                     'HorizontalAlignment', 'left');
        
        
        H.integerInputText = uicontrol('style', 'edit' ,...
                                     'Parent',H.answerPanel,...
                                     'position', getInputAnswerPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','integerInput' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
                                 
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'position', getNavigationButtonPosition(0) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
        H.activeControlTags = [ H.activeControlTags, 'next' ];                     
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Prev' ,...
                                     'position', getNavigationButtonPosition(1) ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'questionText'  'integerInput' 'next' 'prev' };   
        
        H.activeAnswerControl = H.integerInputText;
        H.activeQuestionId = qquestion.id;
        H.activeControlType = 'edit';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.prev, 'callback', {@showPrevQuestion});
        
        if (not(isempty(qquestion.images)))
            displayImages(qquestion);
        end
    end


    %
    %  NAVIGATION
    %

    function backFromEndMessageScreen(hObject, eventData)
        deleteMessagePanel();
        createQAPanels();
        showPrevQuestion(hObject, eventData);
    end

    function restart(hObject, eventData)
        
        xmlFile = QuestionsXMLFile('tests/simple.xml');
        qquestions = QQuestions(xmlFile.domNode);
        H.questionSequencer = QuestionSequencer(qquestions);
        deleteMessagePanel();
        H.activeControlTags = {};
        createQAPanels();
        showCharacterQuestions();
    end

    function saveAndExit(hObject, eventData)
        
        H.questionSequencer.persist();
        close();
    end






    function answerToNextQuestion = registerDisplayedAnswer()
        answerToNextQuestion = 'NOT_YET_SPECIFIED';
        try
            if strcmp(H.activeQuestionId,'characterQuestion')
                characterName = get(H.activeAnswerControl, 'String');
                H.questionSequencer.characterName = characterName;
                if (not(isempty(H.questionSequencer.answeredQuestions)))
                    qanswer = H.questionSequencer.answeredQuestions(1)
                    answerToNextQuestion = qanswer.answer;
                end
            else 
                if strcmp(H.activeControlType,'edit')
                    answer = get(H.activeAnswerControl, 'String');
                    answerToNextQuestion = H.questionSequencer.answerQuestion(answer);
                else
                    % must be choice
                    radioButton = get(H.buttonGroup,'SelectedObject');
                    answer = get(radioButton,'String');
                    answerToNextQuestion = H.questionSequencer.answerQuestion(answer);
                end
            end
        catch exception
            warndlg(exception.message);
        end
    end

    function showPrevQuestion(hObject, eventData)
        
        deleteObsoleteControls(H.activeControlTags);
        if H.questionSequencer.canBackUp()
            prevAnsweredQuestion = H.questionSequencer.backUp();
            prevAnswer = prevAnsweredQuestion.answer;
            qquestion = H.questionSequencer.getCurrentQuestion();
            displayAppropriateQuestion(qquestion);
            displayPriorSetAnswer(prevAnswer,qquestion);
        else
            showCharacterQuestions();
            control = findobj('Tag','characterNameInputText');
            set(control,'String',H.questionSequencer.characterName);
        end
        
    end

    function displayPriorSetAnswer(prevAnswer, qquestion)
        
        if strcmp(qquestion.type,'input_integer')
            control = findobj('Tag','integerInput');
            set(control,'String',prevAnswer);
        else
            % must be 'choice'
            % for some reason findobj doesn't work for these radio buttons,
            % perhaps as they are inside a button group.  Looking in the
            % button group didn't work either
            choiceTag = sprintf('radioButton%s',prevAnswer);
            %radioButton = findobj(buttonGroup,'Tag',choiceTag);
            % so use this workaround:
            handles = guihandles();
            goodRB = getfield(handles, choiceTag);
            set(goodRB,'Value',1);
        end
    end
    function displayAppropriateQuestion(qquestion)
        if (strcmp(qquestion.type,'choice'))
            displayChoiceQuestion(qquestion);
        elseif (strcmp(qquestion.type,'input_integer'))
            displayIntegerInputQuestion(qquestion);
        else
            msg = sprintf('Invalid question type: %s', qquestion.type);
            err = MException('UI:BadQuestionType', msg);
            throw(err)
        end
    end

    function showNextQuestion(hObject, eventData)
        
        if verifyAnswerPresent()
            nextAnswer = registerDisplayedAnswer();
            deleteObsoleteControls(H.activeControlTags);
            qquestion = H.questionSequencer.getCurrentQuestion();
            if (strcmp(qquestion.id,'NO_MORE_QUESTIONS'))
                displayFinishedScreen();
            else
                displayAppropriateQuestion(qquestion);
                if (strcmp(nextAnswer,'NOT_YET_SPECIFIED'))
                    % no answer to apply
                else
                    % apply the previous answer
                    displayPriorSetAnswer(nextAnswer, qquestion);
                end
            end
        else
            errordlg('Please answer the question before clicking "Next"')
        end
    end

    function displayFinishedScreen()
        
        H.frimble = 3;
        deleteQApanels();
        msg = 'You have finished answering questions for this character.  Click "More" to do another character or "Done" if you are finished.';
        createEndMessagePanel(msg);
        
    end
    function result = verifyAnswerPresent()
        
        result = 1;
        if (strcmp(H.activeControlType,'edit'))
            answer = get(H.activeAnswerControl, 'String');
            if (strcmp(answer,''))
                result = 0;
            end
        else % must be 'choice'
            answer = get(H.activeAnswerControl,'SelectedObject');
            if (isempty(answer))
                result = 0;
            end
        end
    end

end
   
















