classdef Session < handle
    %SESSION Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        scoredSetMetadata;
        ui;
        welcomeScreen;
        questionnaireScreens;
        tutorialScreens;
        activeScreen;
        activeQuestionId;
        matrixChoiceScreen;
        characterChoiceScreen;
        algorithmChoiceScreen;
        dpmQuestionScreens;
        mostRecentScreen = 'NOT_STARTED';
        resultsReviewScreen;
       
        morphobankData;
        morphobankBundle;
        
        rootDir;
        avatolSystem;
    end
    
    methods
        function obj = Session(rootDir, avatolSystem)
            import edu.oregonstate.eecs.iis.avatolcv.* ;
            import edu.oregonstate.eecs.iis.avatolcv.mb.* ;
            obj.avatolSystem = avatolSystem;
            obj.rootDir = rootDir;
            obj.scoredSetMetadata = ScoredSetMetadata(rootDir);
            obj.ui = UI();
            obj.welcomeScreen = WelcomeScreen(obj.ui, obj);
            obj.questionnaireScreens = QuestionnaireScreens(obj.ui, obj);
            obj.tutorialScreens = TutorialScreens(obj.ui, obj);
            obj.matrixChoiceScreen = MatrixChoiceScreen(obj.ui, obj);
            obj.characterChoiceScreen = CharacterChoiceScreen(obj.ui, obj);
            obj.algorithmChoiceScreen = AlgorithmChoiceScreen(obj.ui, obj);
            obj.dpmQuestionScreens = DPMQuestionScreens(obj.ui, obj);
            obj.resultsReviewScreen = ResultsReviewScreen(obj.ui, obj);
            
            matrixDownloadsRootPath = obj.getFullPathForJava('matrix_downloads');
            obj.morphobankData = MorphobankData(matrixDownloadsRootPath);
            %obj.chosenMatrix = obj.morphobankData.getMatrixNameAtIndex(obj.matrixChoiceIndex);
            %fprintf('chosen Matrix %s', char(obj.chosenMatrix));
            obj.welcomeScreen.displayWelcomeScreen();
        end
            
    
        function full_path_as_string = getFullPathForJava(obj,partialPath)
            curDir = pwd();
            if ispc
                full_path = sprintf('%s\\%s',curDir, partialPath);
            else
                full_path = sprintf('%s/%s',curDir, partialPath);
            end
            full_path_as_string = java.lang.String(full_path);
        end    

        function matlabList = javaStringListToMatlabCharList(obj, javaList)
            matlabList = {};
            for i=0:javaList.size()-1
                javaString = javaList.get(i);
                matlabString = char(javaString);
                matlabList = [ matlabList, matlabString ];
            end
        end
        
        function javaStringList = matlabListToJavaStringList(obj, matlabList)
            javaStringList = java.util.ArrayList();
            for i=1:length(matlabList)
                matlabCharString = matlabList(i);
                javaString = java.lang.String(matlabCharString);
                javaStringList.add(javaString);
            end
        end
        
        function startQuestions(obj)
            obj.matrixChoiceScreen.displayMatrixQuestion();
        end
        function startTutorial(obj)
            obj.tutorialScreens.startTutorial();
        end
        function jumpToQuestionnaire(obj)
            if (strcmp(obj.mostRecentScreen,'NOT_STARTED'))
                currentFigure = gcf;
                set(currentFigure, 'pointer', 'watch')
                obj.matrixChoiceScreen.displayMatrixQuestion();
                set(currentFigure, 'pointer', 'arrow')
            elseif (strcmp(obj.mostRecentScreen,'QUESTIONNAIRE_COMPLETE'))
                obj.doAnotherCharacter();
            elseif (strcmp(obj.mostRecentScreen,'MATRIX_QUESTION'))
                obj.matrixChoiceScreen.displayMatrixQuestion();
            elseif (strcmp(obj.mostRecentScreen,'CHARACTER_QUESTION'))
                obj.characterChoiceScreen.showCharacterQuestion();
            else
                obj.questionnaireScreens.showCurrentQuestion();
            end
        end
        function showResultsForCurrentCharacter(obj)
            obj.resultsReviewScreen.reset();
            obj.resultsReviewScreen.showResults();
        end
        function jumpToResultsReview(obj)
            obj.resultsReviewScreen.reset();
            obj.resultsReviewScreen.showResults();
        end
        function jumpToTutorial(obj)
            if (strcmp(obj.tutorialScreens.mostRecentTutorialPage,'NOT_STARTED'))
                obj.tutorialScreens.startTutorial();
            elseif (strcmp(obj.tutorialScreens.mostRecentTutorialPage,'TUTORIAL_GOAL'))
                obj.tutorialScreens.startTutorial();
            elseif (strcmp(obj.tutorialScreens.mostRecentTutorialPage,'TUTORIAL_COMPLETE'))
                obj.tutorialScreens.infoPageSequencer.reset();
                obj.tutorialScreens.startTutorial();
            else
                obj.tutorialScreens.showCurrentTutorialPage();
            end
        end
        function backUpFromTutorial(obj)
            obj.welcomeScreen.displayWelcomeScreen();
        end
        function backUpFromQuestionnaire(obj)
            obj.matrixChoiceScreen.displayMatrixQuestion();
        end
        
        
        %
        %  NAVIGATION
        %
        function showPrevQuestion(obj)
            if strcmp(obj.activeQuestionId,'characterQuestion') %TESTME
                obj.backUpFromQuestionnaire();
                % set the chooser to the previous answer
                % get the index of the prev answer
                prevMatrixAnswer = obj.questionnaireScreens.questionSequencer.matrixName;
                % we know we're backing up to the matrix question, so it must
                % have a value to restore
                matrixDirNames = obj.ui.javaStringListToMatlabCharList(obj.morphobankData.getMatrixNames());
                obj.ui.setPopupMenuToValue(obj.matrixChoiceScreen.matrixChoiceWidget,matrixDirNames,prevMatrixAnswer);
                %indexOfMatrixAnswer = find(ismember(H.matrices.matrixDirNames,prevMatrixAnswer));
                %set(H.matrixChoice,'value',indexOfMatrixAnswer);
            elseif obj.questionnaireScreens.questionSequencer.canBackUp()
                prevAnsweredQuestion = obj.questionnaireScreens.questionSequencer.backUp();
                prevAnswer = prevAnsweredQuestion.answer;
                qquestion = obj.questionnaireScreens.questionSequencer.getCurrentQuestion();
                obj.questionnaireScreens.displayAppropriateQuestion(qquestion);
                obj.questionnaireScreens.displayPriorSetAnswer(prevAnswer,qquestion);
            else

                % backing up to char question, must set to previous answer
                obj.characterChoiceScreen.showCharacterQuestion();%TESTME
                prevCharacterAnswer = obj.questionnaireScreens.questionSequencer.characterName;
                obj.ui.setPopupMenuToValue(obj.characterChoiceScreen.characterChoiceWidget, obj.characterChoiceScreen.characterChoices,prevCharacterAnswer);
                % set the chooser to the previous answer
                % get the index of the prev answer
                %set(control,'String',H.questionSequencer.characterName);

            end

        end
        function answerToNextQuestion = registerDisplayedAnswer(obj)
            answerToNextQuestion = 'NOT_YET_SPECIFIED';
            try
                if strcmp(obj.activeQuestionId,'characterQuestion')
                    indexOfCharacterAnswer = get(obj.characterChoiceScreen.characterChoiceWidget,'value');
                    obj.characterChoiceScreen.characterName = char(obj.characterChoiceScreen.characterChoices(indexOfCharacterAnswer));
                    obj.questionnaireScreens.questionSequencer.characterName = obj.characterChoiceScreen.characterName;
                    if (not(isempty(obj.questionnaireScreens.questionSequencer.answeredQuestions)))
                        qanswer = obj.questionnaireScreens.questionSequencer.answeredQuestions(1);
                        answerToNextQuestion = qanswer.answer;
                    end
                elseif strcmp(obj.activeQuestionId,'matrixQuestion')
                    obj.matrixChoiceScreen.registerAnswer();
                    if (strcmp(obj.questionnaireScreens.questionSequencer.matrixName,obj.matrixChoiceScreen.chosenMatrix))
                        % being re-answered to the same value, no need to flush
                        % the characterName
                    else
                        % changing the choice (or its the initial choice) for
                        % matrix, need to clear characterName
                        obj.questionnaireScreens.questionSequencer.characterName = 'UNDEFINED';
                        %... and forget prior character choice index
                        obj.characterChoiceScreen.characterChoiceIndex = 1;
                    end
                    obj.questionnaireScreens.questionSequencer.matrixName = obj.matrixChoiceScreen.chosenMatrix;
                    %if (not(strcmp(obj.questionnaireScreens.questionSequencer.characterName,'UNSPECIFIED')))
                    if (not(strcmp(obj.questionnaireScreens.questionSequencer.characterName,'UNDEFINED')))
                        answerToNextQuestion = obj.questionnaireScreens.questionSequencer.characterName;
                    end
                else 
                    if strcmp(obj.ui.activeControlType,'edit')
                        answer = get(obj.ui.activeAnswerControl, 'String');
                        answerToNextQuestion = obj.questionnaireScreens.questionSequencer.answerQuestion(answer);
                    else
                        % must be choice
                        radioButton = get(obj.questionnaireScreens.buttonGroup,'SelectedObject');
                        answer = get(radioButton,'String');
                        answerToNextQuestion = obj.questionnaireScreens.questionSequencer.answerQuestion(answer);
                    end
                end
            catch exception
                warndlg(exception.message);
                answerToNextQuestion = 'ANSWER_BLOCKED_BY_ERROR';
            end
        end
        
        function doneWithDPMQuestions(obj)
            obj.algorithmChoiceScreen.displayQuestionnaireCompleteScreen();
        end    
        function doneWithQuestionnaire(obj)
            obj.algorithmChoiceScreen.chooseAlgorithm();
            if (strcmp(obj.algorithmChoiceScreen.algorithmChosen,'DPM'))
                obj.dpmQuestionScreens.showFirstQuestion();
            else 
                obj.algorithmChoiceScreen.displayQuestionnaireCompleteScreen();
            end    
            
        end
        function showNextQuestion(obj)
            if obj.ui.verifyAnswerPresent()
                currentFigure = gcf;
                set(currentFigure, 'pointer', 'watch');
                nextAnswer = obj.registerDisplayedAnswer();
                if (strcmp(nextAnswer,'ANSWER_BLOCKED_BY_ERROR'))
                    % stay on same question
                else 
                    if (strcmp(obj.activeQuestionId,'matrixQuestion'))
                        obj.characterChoiceScreen.showCharacterQuestion();
                        prevCharacterAnswer = obj.questionnaireScreens.questionSequencer.characterName;
                        if (strcmp(prevCharacterAnswer, 'UNDEFINED'))
                            % this is the first time visiting the character
                            % question since the matrix has been thusly set.
                            % Leave the default answer as the first in the
                            % matrix
                        else
                            % pre-existing answer can be loaded - we must have
                            % revisted the matrix question and not changed it.
                            obj.ui.setPopupMenuToValue(obj.characterChoiceScreen.characterChoiceWidget, obj.characterChoiceScreen.characterChoices,prevCharacterAnswer);
                            %indexOfCharacterAnswer = find(ismember(H.characterChoices,prevCharacterAnswer));
                            %set(H.characterChoice,'value',indexOfCharacterAnswer);)
                        end
                    else
                        obj.questionnaireScreens.showNextQuestionSequencerQuestion(nextAnswer);
                    end

                end
                set(currentFigure, 'pointer', 'arrow');
            else
                errordlg('Please answer the question before clicking "Next"')
            end
        end
        %function backFromEndMessageScreen(hObject, eventData)
        %    deleteObsoleteControls();
        %    if (strcmp(H.mostRecentQAFlavor,'typedInput'))
        %        createTypedInputQAPanels();
        %    else
        %        createChoiceQAPanels();
        %    end

        %    showPrevQuestion(hObject, eventData);
        %end

        function doAnotherCharacter(obj)
            cd(obj.rootDir);
            obj.questionnaireScreens.reset();
            obj.matrixChoiceScreen.displayMatrixQuestion();
        end

        function showMatrixQuestion(obj)
            obj.matrixChoiceScreen.displayMatrixQuestion 
        end    
        
        function exit(obj)
            close();
        end


    end
    
end

