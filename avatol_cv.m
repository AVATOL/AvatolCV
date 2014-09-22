function avatol_cv
    if ispc
        javaaddpath('java\\bin');
        javaaddpath('java\\lib');
    else
        javaaddpath('java/bin');
        javaaddpath('java/lib');
    end
   
    import edu.oregonstate.eecs.iis.avatolcv.*;
    import java.util.List;
    import java.lang.String;
    import java.lang.System.*;
    %
    clearvars();
    clearvars -global H;
    global H;
    H.rootDir = pwd();
    %H.questionnaireXML = 'tests/simple.xml';
    if ispc
        H.questionnaireXML = 'data\\questionnaire\\Questionnaire.xml';
    else
        H.questionnaireXML = 'data/questionnaire/Questionnaire.xml';
    end
    
    questionsXmlFile = QuestionsXMLFile(H.questionnaireXML);
    qquestions = QQuestions(questionsXmlFile.domNode);
    H.questionSequencer = QuestionSequencer(qquestions);
    qv = QuestionsValidator();
    qv.validate(H.questionSequencer.qquestions.questions);
    
    %H.tutorialXML = 'tests/simpleTutorial.xml';
    if ispc
        H.tutorialXML = 'data\\tutorial\\Tutorial.xml';
    else
        H.tutorialXML = 'data/tutorial/Tutorial.xml';
    end
    
    tutorialXmlFile = QuestionsXMLFile(H.tutorialXML);
    infoPages = InfoPages(tutorialXmlFile.domNode);
    H.infoPageSequencer = InfoPageSequencer(infoPages);
    ipv = InfoPagesValidator();
    ipv.validate(H.infoPageSequencer.info_pages.info_pages);
    
    
    H.activeControlTags = {};
    H.activePanelTags = {};
    
    H.mostRecentTutorialPage = 'NOT_STARTED';
    H.mostRecentQuestionnairePage = 'NOT_STARTED';
    
    matrixDownloadsRootPath = getFullPathForJava('matrix_downloads');
    H.morphobankData = MorphobankData(matrixDownloadsRootPath);
    %H.matrices = MorphobankMatrices('matrix_downloads');
    H.matrixChoiceIndex = 1;
    H.chosenMatrix = H.morphobankData.getMatrixNameAtIndex(H.matrixChoiceIndex);  
    H.characterChoiceIndex = 1;
    layout();
    displayWelcomeScreen();
    
    function full_path = getFullPathForJava(partialPath)
        curDir = pwd();
        if ispc
            full_path = sprintf('%s\\%s',curDir, partialPath);
        else
            full_path = sprintf('%s/%s',curDir, partialPath);
        end
    end    
    %
    %  LAYOUT helper functions
    %
    
    function layout()
        
        H.lineHeight = 30;
        H.pushButtonWidth = 80;
        H.fontname = 'Helvetica';
        %H.fontname = 'Calibri';
        %H.fontname = 'Times New Roman';
        H.fontsize = 13;
        %H.fontsize = 14;
        H.fontsizeHeader = 16;
        H.fullWidth = 900;
        H.fullHeight = 600;
        H.figurePosition =  [150 150 H.fullWidth H.fullHeight];
        H.fig = figure('position', H.figurePosition ,... 
                'MenuBar', 'none' ,...
                'Name', 'AVATOL Computer Vision System',...
                'Color', [1 1 1]);
    end

    function deleteObsoleteControls()
        deleteControls(H.activeControlTags);
        H.activeControlTags = {};
        deleteControls(H.activePanelTags);
        H.activePanelTags = {};
    end

    function deleteControls(controlTags)
        handles = guihandles();
        if (not(isempty(handles)))
            for i=1:length(controlTags)
                tag = controlTags{i};
                %control = findobj('Tag',tag);
                control = getfield(handles, tag);
                delete(control);
            end
        end
    end

    function createPopupChoicePanels()
        
        H.titlePanel = createTitlePanel();
                              
        H.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','questionPanel',...
                                  'Position',[0.05 0.70 0.95 0.18]);
                              
        H.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','answerPanel',...
                                  'Position',[0.05 0.65 0.95 0.18]);
                              
        H.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag','imagePanel',...
                                  'Position',[ 0.02 0.1 0.96 0.60]);
                              
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','navigationPanel',...
                                  'Position',getNavigationPanelPosition());
                              
        H.mostRecentQAFlavor = 'typedInput';
        H.activePanelTags = { 'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel' };
    end
	function createResultsReviewPanels()
        H.titlePanel = createTitlePanel();
                              
        H.messagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','messagePanel',...
                                  'Position',[0.05 0.70 0.67 0.18]);
                              
        H.scoredImagePanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','scoredImagePanel',...
                                  'Position',[0.74 0.70 0.21 0.18]);
                              
        H.scoredTextPanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag','scoredTextPanel',...
                                  'Position',[ 0.02 0.1 0.96 0.60]);
                              
        H.trainingImagePanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','trainingImagePanel',...
                                  'Position',[0.74 0.70 0.21 0.18]);
                              
        H.trainingTextPanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag','trainingTextPanel',...
                                  'Position',[ 0.02 0.1 0.96 0.60]);
                              
        H.feedbackPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','feedbackPanel',...
                                  'Position',getNavigationPanelPosition());
                
        H.activePanelTags = { 'titlePanel', 'messagePanel', 'scoredImagePanel', 'scoredTextPanel', 'trainingImagePanel', 'trainingTextPanel', 'feedbackPanel' };
    end
    function createTypedInputQAPanels()
        H.titlePanel = createTitlePanel();
                              
        H.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','questionPanel',...
                                  'Position',[0.05 0.70 0.67 0.18]);
                              
        H.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag','answerPanel',...
                                  'Position',[0.74 0.70 0.21 0.18]);
                              
        H.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag','imagePanel',...
                                  'Position',[ 0.02 0.1 0.96 0.60]);
                              
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','navigationPanel',...
                                  'Position',getNavigationPanelPosition());
                              
        H.mostRecentQAFlavor = 'typedInput';
        H.activePanelTags = { 'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel' };
    end

    function titlePanel = createTitlePanel()
        titlePanel = uipanel('Background', [1 1 1],...%[1 0.5 0.5]
                                  'BorderType', 'none',... %etchedin
                                  'Tag','titlePanel',...
                                  'Position',[0.02 0.89 .96 0.07]);
        
    end

    function createChoiceQAPanels()
        H.titlePanel = createTitlePanel();
                              
        H.questionPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'questionPanel',...
                                  'Position',[0.05 0.70 0.67 0.18]);
                              
        H.answerPanel = uipanel('Background', [1 1 1],...%[0.3 1 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'answerPanel',...
                                  'Position',[0.74 0.1 0.21 0.77]);
                              
        H.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag', 'imagePanel',...
                                  'Position',[ 0.02 0.1 0.7 0.60]);
                              
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'navigationPanel',...
                                  'Position',getNavigationPanelPosition());
        H.mostRecentQAFlavor = 'choice';
        H.activePanelTags = {  'titlePanel', 'questionPanel', 'answerPanel', 'imagePanel', 'navigationPanel'}; 
    end


    function createSimpleTextScreenPanels()
        
        H.textPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'textPanel',...
                                  'Position',[0.2 0.3 0.6 0.4]);
                             
      
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'navigationPanel',...
                                  'Position',getNavigationPanelPosition());
        %H.mostRecentQAFlavor = 'na';
        H.activePanelTags = { 'textPanel', 'navigationPanel'};
    end

    function createTutorialPanels()
        H.titlePanel = createTitlePanel();
                              
        H.textPanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'textPanel',...
                                  'Position',[0.05 0.70 0.9 0.18]);
                             
        H.imagePanel = uipanel('Background',[1 1 1],...%[0.3 0.3 1]
                                  'BorderType', 'none',...
                                  'Tag', 'imagePanel',...
                                  'Position',[ 0.15 0.2 0.7 0.60]);
                              
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag', 'navigationPanel',...
                                  'Position',getNavigationPanelPosition());
        %H.mostRecentQAFlavor = 'na';
        H.activePanelTags = { 'titlePanel', 'textPanel', 'imagePanel', 'navigationPanel'}; 
    end

     %
     % Positions
     %

    function position = getNavigationPanelPosition()
        position = [0.02 0.02 0.96 0.05];
    end


    function answerPosition = getInputAnswerPosition()
        answerPosition = [ 0,0.7,1, 0.3 ];
    end

    function answerPosition = getChoiceAnswerPosition(answerIndex)
        
        answerPanelPosition = getpixelposition(H.answerPanel,1);
        answerPanelWidth = answerPanelPosition(3);
        answerPanelHeight = answerPanelPosition(4);
        
        answerPositionY = answerPanelHeight -H.lineHeight - (answerIndex*H.lineHeight);
        answerPosition = [ 0 answerPositionY answerPanelWidth H.lineHeight];
    end

    function navButtonPosition = getButtonPositionRightBig()
        navButtonPosition = [0.75,0,0.2,1 ];
    end

    
    function navButtonPosition = getButtonPositionRightA()
        navButtonPosition = [0.8,0,0.1,1 ];
    end

    function navButtonPosition = getButtonPositionRightB()
        navButtonPosition = [0.9,0,0.1,1 ];
    end

    function navButtonPosition = getButtonPositionLeft()
        navButtonPosition = [0,0,0.3,1 ];
    end

  
    %
    %  UI panel populators
    %
    
    function control = getCharacterNameTitle(titleString)
        control = uicontrol('style', 'text' ,...
                                     'String', titleString ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsizeHeader ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','titleText' ,...
                                     'Parent',H.titlePanel,...
                                     'HorizontalAlignment', 'center');
      
    end

    function displayQuestionnaireCompleteScreen() 
        
        deleteObsoleteControls();
        H.algorithms = Algorithms();
        disqualifyingCRFMessage = H.algorithms.getDisqualifyingMessageForCRF(H.questionSequencer.answeredQuestions);
        disqualifyingDPMMessage = H.algorithms.getDisqualifyingMessageForDPM(H.questionSequencer.answeredQuestions);
        showRunAlgorithmButton = false;
        if (strcmp(disqualifyingCRFMessage,''))
            message = 'CRF algorithm has been chosen for scoring.  Press Run Algorithm to begin.';
            showRunAlgorithmButton = true;
			H.algorithmChosen = 'CRF';
        elseif (strcmp(disqualifyingDPMMessage,''))
            message = 'DPM algorithm has been chosen for scoring.  Press Run Algorithm to begin.';
            showRunAlgorithmButton = true;
			H.algorithmChosen = 'DPM';
        else
            general_error_msg = 'The answers chosen indicate the currently in-play algorithms are not a match for scoring the images:';
            message = sprintf('%s\n\n%s\n\n%s',general_error_msg, disqualifyingCRFMessage, disqualifyingDPMMessage);
        end
        
        H.messagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','messagePanel',...
                                  'Position',[0.1 0.2 0.8 0.7]);
        
        
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
                                 
        H.navigationPanel = uipanel('Background', [1 1 1],...%[0.1 0.3 0.3]
                                     'BorderType', 'none',...
                                     'Units', 'normalized',...
                                     'Tag','navigationPanel' ,...
                                     'Position',getNavigationPanelPosition());
                              
        H.doAnotherCharacter = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Try another character' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units', 'normalized',...
                                     'position', getButtonPositionLeft() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','doAnotherCharacter' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
        if (showRunAlgorithmButton)
             H.runAlgorithm = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Run Algorithm' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units', 'normalized',...
                                     'position', getButtonPositionRightBig() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','runAlgorithm' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
            H.activeControlTags = { 'messageText', 'doAnotherCharacter', 'runAlgorithm'}; 
			set(H.runAlgorithm, 'callback', {@runAlgorithm});
        else
            H.done = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Exit' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units', 'normalized',...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','done' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
            H.activeControlTags = { 'messageText', 'doAnotherCharacter', 'done'}; 
        end
        %H.prev = uicontrol('style', 'pushbutton' ,...
        %                             'String', 'Prev' ,...
        %                             'Parent',H.navigationPanel,...
        %                             'Units', 'normalized',...
        %                             'position', getButtonPositionRightA() ,...
        %                             'FontName', H.fontname ,...
        %                             'FontSize', H.fontsize ,...
        %                             'Tag','prev' ,...
        %                             'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activePanelTags = { 'messagePanel', 'navigationPanel' };
        %H.activeControlTags = { 'messageText', 'doAnotherCharacter', 'done', 'prev'};    
           
        
        set(H.doAnotherCharacter, 'callback', {@doAnotherCharacter});
        set(H.done, 'callback', {@saveAndExit});
        %set(H.prev, 'callback', {@backFromEndMessageScreen});
        H.mostRecentQuestionnairePage = 'QUESTIONNAIRE_COMPLETE'; 
        H.questionSequencer.persist();
    end
    function runAlgorithm()
		deleteObsoleteControls();
		H.messagePanel = uipanel('Background', [1 1 1],...%[1 0.3 0.3]
                                  'BorderType', 'none',...
                                  'Tag','messagePanel',...
                                  'Position',[0.1 0.2 0.8 0.7]);
        
        startingAlgorithmString = sprintf('Starting %s algorithm...', H.algorithmChosen);
        H.statusMessage = uicontrol('style', 'text' ,...
                                     'Parent',H.messagePanel,...
                                     'Units', 'normalized',...
                                     'position', [0 0 1 1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','messageText' ,...
                                     'Background',[1 1 1],...
                                     'String', startingAlgorithmString,...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
		
		H.progressIndicator = ProgressIndicator(H.statusMessage);
        H.activePanelTags = { 'messagePanel' };
        H.activeControlTags = { 'messageText', 'statusMessage'};  
		H.algorothms.invoke_algorithm(obj, alg, list_of_characters, input_folder, output_folder, detection_results_folder, H.progressIndicator);
		%here is where we show the results
		showResults();
	end
	function showResults()
		deleteObsoleteControls();
		% create panel for instructions
		
		% create panel for training images
		
		% create panel for scored images
		
		% create panel for feedback
	end
    function displayWelcomeScreen()
        deleteObsoleteControls();
        createSimpleTextScreenPanels();
        introText = ['Welcome to the AVATOL Computer Vision System.  '...
                     'Click the buttons below to either begin the tutorial, or skip to the questionnaire'];
        
        H.tutorialText = uicontrol('style', 'text' ,...
                                     'String', introText ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','tutorialText' ,...
                                     'Parent',H.textPanel,...
                                     'HorizontalAlignment', 'left');
                                 
        beginTutorialPosition = [0.75,0,0.25,1 ];  
        skipToQuestionnaireButtonPosition = [0,0,0.25,1 ];
        
        H.beginTutorial = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Begin Tutorial' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', beginTutorialPosition,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','beginTutorial' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
                         
        H.skipToQuestionnaire = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Skip to Questionnaire' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', skipToQuestionnaireButtonPosition ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','skipToQuestionnaire' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'tutorialText', 'beginTutorial', 'skipToQuestionnaire' };   
        
        H.activeScreen = 'WelcomeScreen';
        set(H.beginTutorial, 'callback', {@showTutorialGoalPage});
        set(H.skipToQuestionnaire, 'callback', {@jumpToQuestionnaire});
 
    end

    function displayTutorialGoalPage()
        deleteObsoleteControls();
        createSimpleTextScreenPanels();
        lineA = 'The goals of this tutorial are to:';
        lineA1 = '';
        lineA2 = '';
        lineB = '    1) Map biological terminology into computer vision terminology';
        lineC = '';
        lineD = '        - What is a biological character in computer vision terms?';
        lineE = '';
        lineF = '        - What is a character score in computer vision terms?';
        lineG = '';
        lineH = '';
        lineI = '    2) Explain general concepts about images and computer vision';
        goalText = { lineA lineA1 lineA2 lineB lineC lineD lineE lineF lineG lineH lineI };
        
        H.tutorialText = uicontrol('style', 'text' ,...
                                     'String', goalText ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','tutorialText' ,...
                                     'Parent',H.textPanel,...
                                     'HorizontalAlignment', 'left');
                                 
        skipToQuestionnaireButtonPosition = [0,0,0.25,1 ];
        H.skipToQuestionnaire = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Skip to Questionnaire' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', skipToQuestionnaireButtonPosition ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','skipToQuestionnaire' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
         H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'parent',H.navigationPanel,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                         
         H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Back' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightA() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Parent',H.navigationPanel,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'tutorialText', 'next', 'prev', 'skipToQuestionnaire'};  
        H.activeScreen = 'TUTORIAL_GOAL';
        set(H.next, 'callback', {@showCurrentTutorialPage});
        set(H.prev, 'callback', {@showWelcomeScreen});
        set(H.skipToQuestionnaire, 'callback', {@jumpToQuestionnaire});
        H.mostRecentTutorialPage = 'TUTORIAL_GOAL';
    end
    function displayFinishedTutorialScreen() 
        deleteObsoleteControls();
        createSimpleTextScreenPanels();
        summaryText = ['You have completed the tutorial.  '...
                     'You may either begin the questionnaire or exit the application.'];
        
        H.summaryText = uicontrol('style', 'text' ,...
                                     'String', summaryText ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','summaryText' ,...
                                     'Parent',H.textPanel,...
                                     'HorizontalAlignment', 'left');
        
        skipToQuestionnaireButtonPosition = [0,0,0.25,1 ];  
        prevButtonPosition =                [0.6,0,0.15,1];                       
        beginQuestionnaireButtonPosition =  [0.75,0,0.25,1 ];
        H.beginQuestionnaire = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Begin Questionnaire' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', beginQuestionnaireButtonPosition,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','beginQuestionnaire' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
         H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Prev' ,...
                                     'Units','normalized',...
                                     'position', prevButtonPosition ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Parent',H.navigationPanel,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);                   
        H.exit = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Exit' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', skipToQuestionnaireButtonPosition ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','exit' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'summaryText', 'beginQuestionnaire', 'exit', 'prev' };   
        
        H.activeScreen = 'SummaryScreen';
        set(H.beginQuestionnaire, 'callback', {@startQuestionnaire});
        set(H.exit, 'callback', {@exit});
        set(H.prev, 'callback', {@showPrevTutorialPage});
        H.mostRecentTutorialPage = 'TUTORIAL_COMPLETE';
    end

    function startQuestionnaire(hObject, eventData)
        displayMatrixQuestion();
    end

    function displayTutorialPage(infoPage)
        deleteObsoleteControls();
        createTutorialPanels();
        
        titleString = sprintf('Tutorial');
        H.characterNameText = getCharacterNameTitle(titleString);
        
        H.tutorialText = uicontrol('style', 'text' ,...
                                     'String', infoPage.text ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','tutorialText' ,...
                                     'Parent',H.textPanel,...
                                     'HorizontalAlignment', 'left');
        skipToQuestionnaireButtonPosition = [0,0,0.25,1 ];
        H.skipToQuestionnaire = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Skip to Questionnaire' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', skipToQuestionnaireButtonPosition ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','skipToQuestionnaire' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
         H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'parent',H.navigationPanel,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                         
         H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Back' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightA() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Parent',H.navigationPanel,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'tutorialText', 'next', 'prev', 'skipToQuestionnaire'};  
        H.activeScreen = 'TutorialPage';
        set(H.next, 'callback', {@showNextTutorialPage});
        set(H.prev, 'callback', {@showPrevTutorialPage});
        set(H.skipToQuestionnaire, 'callback', {@jumpToQuestionnaire});
        if (not(isempty(infoPage.images)))
            displayImages(infoPage.images);
        end
        H.mostRecentTutorialPage = infoPage.id;
    end

    function displayMatrixQuestion()
        deleteObsoleteControls();
        createPopupChoicePanels();
        
        H.matrixChoicePrompt = uicontrol('style', 'text' ,...
                                     'Parent',H.questionPanel,...
                                     'Units','normalized',...
                                     'String', 'Which matrix is the character in?' ,...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','matrixChoicePrompt' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
                                 
        H.matrixChoice = uicontrol('style', 'popupmenu' ,...
                                     'Parent',H.answerPanel,...
                                     'Units','normalized',...
                                     'position', [ 0,0.71,.9, 0.29 ] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','matrixChoice' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
        
        matrixDirNames = javaStringListToMatlabCharList(H.morphobankData.getMatrixNames());
        set(H.matrixChoice,'string',matrixDirNames);
        set(H.matrixChoice,'Value',H.matrixChoiceIndex);
        H.matrixChoices = matrixDirNames;
                    
        H.tutorial = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Tutorial' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', [0,0,0.15,1],...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','tutorial' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'String', 'Next' ,...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        %H.activeControlTags = { 'matrixChoice',  'matrixChoicePrompt', 'next', 'tutorial' };
        H.activeControlTags = { 'matrixChoicePrompt', 'next', 'tutorial' };
        H.activeAnswerControl = H.matrixChoice;
        H.activeQuestionId = 'matrixQuestion';
        H.activeControlType = 'popupMenu';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.tutorial, 'callback', {@jumpToTutorial});
        set(H.matrixChoice, 'callback', {@setMatrixChoice});
        H.mostRecentQuestionnairePage = 'MATRIX_QUESTION'; 
    end
    function matlabList = javaStringListToMatlabCharList(javaList)
        matlabList = {};
        for i=0:javaList.size()-1
            javaString = javaList.get(i);
            matlabString = char(javaString);
            matlabList = [ matlabList, matlabString ];
        end
    end
    function setMatrixChoice(hObject, eventData)
        H.matrixChoiceIndex = get(H.matrixChoice, 'value');
        matrixList = get(H.matrixChoice, 'string');
        H.chosenMatrix = char(matrixList(H.matrixChoiceIndex));
    end
    function setCharacterChoice(hObject, eventData)
        H.characterChoiceIndex = get(H.characterChoice, 'value');
        characterList = get(H.characterChoice, 'string');
        H.characterName = char(characterList(H.characterChoiceIndex));
    end
    function displayCharacterQuestion()
        deleteObsoleteControls();
        createPopupChoicePanels();
        
        H.characterChoicePrompt = uicontrol('style', 'text' ,...
                                     'Parent',H.questionPanel,...
                                     'Units','normalized',...
                                     'String', 'Which character do you want to score?' ,...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','characterChoicePrompt' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
       H.characterChoice = uicontrol('style', 'popupmenu' ,...
                                     'Parent',H.answerPanel,...
                                     'Units','normalized',...
                                     'position', [ 0,0.71,.9, 0.29 ] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','characterChoice' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
        
        %import edu.oregonstate.eecs.iis.avatolcv.*;
        chosenMatrixString = java.lang.String(H.chosenMatrix);
        edu.oregonstate.eecs.iis.avatolcv.MorphobankBundle.printString(chosenMatrixString);
        H.morphobankBundle = H.morphobankData.loadMatrix(chosenMatrixString);
        presenceAbsenceCharNamesJavaList = H.morphobankBundle.getPresenceAbsenceCharacterNames();
        for i=0:presenceAbsenceCharNamesJavaList.size() - 1
            fprintf('matlab name %s\n',char(presenceAbsenceCharNamesJavaList.get(i)));
        end
        
        presenceAbsenceCharNames = javaStringListToMatlabCharList(presenceAbsenceCharNamesJavaList);
        for i=1:length(presenceAbsenceCharNames)
            fprintf('presenceAbsenceCharName : %s',char(presenceAbsenceCharNames(i)));
        end
        
        %sddXMLFilePath = H.matrices.getSDDFilePath(H.chosenMatrix);
        %fullSddXMLFilePathForJava = getFullPathForJava(sddXMLFilePath);
        %matrix = Matrix(fullSddXMLFilePathForJava);
        
        %sddXMLFile = XMLFile(sddXMLFilePath);
        %domNode = sddXMLFile.domNode;
        %matrixCharacters = MatrixCharacters(domNode,H.chosenMatrix, H.matrixDir);
        %    testCase.verifyEqual(matrixCharacters.characters(1).name,'GEN skull, dorsal margin, shape at juncture of braincase and rostrum in lateral view');
        %    testCase.verifyEqual(matrixCharacters.characters(2).name,'GEN skull, posterior extension of alveolar line and occiput, intersection');
        %characterClassList = matrixCharacters.charactersPresenceAbsence;
        %characterNameList = {};
        %for i=1:length(characterClassList)
        %    charName = char(characterClassList(i).name);
        %    characterNameList = [ characterNameList, charName ];
        %end
        %matrixCharacters.generateInputDataFiles();
        %H.characterChoices = characterNameList;
        H.characterChoices = presenceAbsenceCharNames;
        set(H.characterChoice,'string',H.characterChoices);
        %H.characterName = char(characterNameList(1));
        set(H.characterChoice,'Value',H.characterChoiceIndex);
       
        
            
            
            
        H.tutorial = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Tutorial' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', [0,0,0.15,1],...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','tutorial' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'String', 'Next' ,...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Back' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightA() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Parent',H.navigationPanel,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        %H.activeControlTags = {'characterChoice',  'characterChoicePrompt', 'next', 'tutorial', 'prev' };
        H.activeControlTags = {'characterChoicePrompt', 'next', 'tutorial', 'prev' };
        
        H.activeAnswerControl = H.characterChoice;
        H.activeQuestionId = 'characterQuestion';
        H.activeControlType = 'popupMenu';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.tutorial, 'callback', {@jumpToTutorial});
        set(H.prev, 'callback', {@showPrevQuestion});
        set(H.characterChoice, 'callback', {@setCharacterChoice});
        H.mostRecentQuestionnairePage = 'CHARACTER_QUESTION'; 
    end
    function displayChoiceQuestion(qquestion)
        % Create the button group.
        deleteObsoleteControls();
        createChoiceQAPanels();
        
        titleString = sprintf('Character :  %s',H.characterName);
        H.characterNameText = getCharacterNameTitle(titleString);
        
        H.buttonGroup = uibuttongroup('Visible','off',...
                                    'Tag','buttonGroup',...
                                    'BorderType','none',...
                                    'Background','white',...
                                    'Parent',H.answerPanel,...
                                    'Position',[0 0 1 1]);
                                
        
        H.activeControlTags = { 'buttonGroup', 'titleText' };    
       
        H.questionText = uicontrol('style', 'text' ,...
                                     'String', qquestion.text ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','questionText' ,...
                                     'Parent',H.questionPanel,...
                                     'HorizontalAlignment', 'left');
        H.activeControlTags = [ H.activeControlTags,  'questionText' ];   
        
        % Create radio buttons in the button group.       
                        
        for i=1:length(qquestion.answers)
            answerValue = qquestion.answers(i).value;
            tag = sprintf('radioButton%s',answerValue);
            thisButton = uicontrol('Style','radiobutton',...
                            'visible', 'on',...
                            'String',answerValue ,...
                            'Background', 'white' ,...
                            'Position',getChoiceAnswerPosition(i-1),...
                            'parent',H.buttonGroup,...
                            'FontName', H.fontname ,...
                            'FontSize', H.fontsize ,...
                            'Tag', tag,...
                            'HandleVisibility', 'off');
            % buttons not found for deletion, presumably because Button
            % group gets deleted forst, so, don't bother to remember here
            % for later deletion.
            %H.activeControlTags = [ H.activeControlTags, tag ]; 
        end

       
        set(H.buttonGroup,'SelectedObject',[]);  % No selection
        set(H.buttonGroup,'Visible','on');

        H.tutorial = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Tutorial' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', [0,0,0.15,1],...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','tutorial' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        H.activeControlTags = [ H.activeControlTags, 'tutorial' ];                              
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightB() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'parent',H.navigationPanel,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
        H.activeControlTags = [ H.activeControlTags, 'next' ];                         
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Back' ,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightA() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Parent',H.navigationPanel,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = [ H.activeControlTags, 'prev' ];
        
        H.activeAnswerControl = H.buttonGroup;
        H.activeQuestionId = qquestion.id;
        H.activeControlType = 'buttonGroup';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.prev, 'callback', {@showPrevQuestion});
        set(H.tutorial, 'callback', {@jumpToTutorial});
        
        if (not(isempty(qquestion.images)))
            displayImages(qquestion.images);
        end
        H.mostRecentQuestionnairePage = qquestion.id; 
    end

    function displayInputQuestion(qquestion)
        deleteObsoleteControls();
        
        createTypedInputQAPanels();
        titleString = sprintf('Character :  %s', H.characterName);
        H.characterNameText = getCharacterNameTitle(titleString);
        
        H.questionText = uicontrol('style', 'text' ,...
                                     'String', qquestion.text ,...
                                     'Units','normalized',...
                                     'position', [0,0,1,1] ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'BackgroundColor', [1 1 1] ,...
                                     'Tag','questionText' ,...
                                     'Parent',H.questionPanel,...
                                     'HorizontalAlignment', 'left');
        
        
        H.inputText = uicontrol('style', 'edit' ,...
                                     'Parent',H.answerPanel,...
                                     'Units','normalized',...
                                     'position', getInputAnswerPosition() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','input' ,...
                                     'Background',[1 1 1],...
                                     'HorizontalAlignment', 'left');%'BackgroundColor', [1 0.1 0.1] ,...
        
        H.tutorial = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Tutorial' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', [0,0,0.15,1],...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','tutorial' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.next = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Next' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightB(),...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','next' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
        
        H.activeControlTags = [ H.activeControlTags, 'next' ];                     
        H.prev = uicontrol('style', 'pushbutton' ,...
                                     'String', 'Back' ,...
                                     'Parent',H.navigationPanel,...
                                     'Units','normalized',...
                                     'position', getButtonPositionRightA() ,...
                                     'FontName', H.fontname ,...
                                     'FontSize', H.fontsize ,...
                                     'Tag','prev' ,...
                                     'BackgroundColor', [0.5 0.5 0.5]);  
                                 
        H.activeControlTags = { 'titleText', 'questionText',  'input', 'next', 'prev', 'tutorial' };   
        
        H.activeAnswerControl = H.inputText;
        H.activeQuestionId = qquestion.id;
        H.activeControlType = 'edit';
        
        set(H.next, 'callback', {@showNextQuestion});
        set(H.prev, 'callback', {@showPrevQuestion});
        set(H.tutorial, 'callback', {@jumpToTutorial});
        
        if (not(isempty(qquestion.images)))
            displayImages(qquestion.images);
        end
        H.mostRecentQuestionnairePage = qquestion.id; 
    end

    %
    % sequential navigation logic
    %
    
    function showWelcomeScreen(hObject, eventData)
        displayWelcomeScreen();
    end

    function showCurrentTutorialPage(hObject, eventData)
        infoPage = H.infoPageSequencer.getCurrentInfoPage();
        displayTutorialPage(infoPage);
    end
    
    function showTutorialGoalPage(hObject, eventData)
        displayTutorialGoalPage();
    end
    function showNextTutorialPage(hObject, eventData)
        % we've already started the tutorial, check to see if we can
        % move forward
        infoPage = H.infoPageSequencer.getCurrentInfoPage();
        if (strcmp(infoPage.next,'NO_MORE_PAGES'))
            displayFinishedTutorialScreen();
        else
            H.infoPageSequencer.moveToNextPage();
            infoPage = H.infoPageSequencer.getCurrentInfoPage();
            displayTutorialPage(infoPage);
        end
    end

    function showPrevTutorialPage(hObject, eventData)
        if (strcmp(H.activeScreen,'SummaryScreen'))
            % we're backing up from the TutorialDone screen - just
            % show the currently referenced page
            infoPage = H.infoPageSequencer.getCurrentInfoPage();
            displayTutorialPage(infoPage);
        elseif (H.infoPageSequencer.canBackUp())
            H.infoPageSequencer.backUp();
            infoPage = H.infoPageSequencer.getCurrentInfoPage();
            displayTutorialPage(infoPage);
        else
            displayTutorialGoalPage();
        end
    end

    %
    % jumping between tutorial and questionnaire
    %
    
    function jumpToTutorial(hObject, eventData)
        if (strcmp(H.mostRecentTutorialPage,'NOT_STARTED'))
            showTutorialGoalPage(hObject, eventData);
        elseif (strcmp(H.mostRecentTutorialPage,'TUTORIAL_GOAL'))
            showTutorialGoalPage(hObject, eventData);
        elseif (strcmp(H.mostRecentTutorialPage,'TUTORIAL_COMPLETE'))
            H.infoPageSequencer.reset();
            showTutorialGoalPage(hObject, eventData);
        else
            showCurrentTutorialPage(hObject, eventData);
        end
    end

    function jumpToQuestionnaire(hObject, eventData)
        if (strcmp(H.mostRecentQuestionnairePage,'NOT_STARTED'))
           displayMatrixQuestion();
        elseif (strcmp(H.mostRecentQuestionnairePage,'QUESTIONNAIRE_COMPLETE'))
            doAnotherCharacter(hObject, eventData);
        elseif (strcmp(H.mostRecentQuestionnairePage,'MATRIX_QUESTION'))
            displayMatrixQuestion();
        elseif (strcmp(H.mostRecentQuestionnairePage,'CHARACTER_QUESTION'))
            displayCharacterQuestion();
        else
            showCurrentQuestion(hObject, eventData);
        end
    end
    
    %
    % image display
    %

    function displaySingleImage(images)
        image1Path = images(1).imageFilePath;
        axes1Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image1panel' ,...
                             'position',[0,0,1,1]);
        axes1 = axes('Parent',axes1Panel,...
                             'Color',[1,1,1],...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image1' ,...
                             'position',[0.02,0.2,0.96,0.76]);%[0.02,0.02,0.96,0.96]
        H.activeControlTags = [ H.activeControlTags, 'image1panel' ];
        imshow(image1Path);
        xlabel(images(1).imageCaption);
    end

    function displayImagePair(images)
        image1Path = images(1).imageFilePath;
        axes1Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image1panel' ,...
                             'position',[0,0,0.5,1]);
        axes1 = axes('Parent',axes1Panel,...
                             'Color',[1,1,1],...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image1' ,...
                             'position',[0.02,0.02,0.96,0.96]);
        H.activeControlTags = [ H.activeControlTags, 'image1panel' ];
        imshow(image1Path);
        xlabel(images(1).imageCaption);
        image2Path = images(2).imageFilePath;
        axes2Panel = uipanel('Parent',H.imagePanel,...
                             'Tag','image2panel' ,...
                             'position',[0.5,0,0.5,1]);
        axes2 = axes('Parent',axes2Panel,...
                             'Color',[1,1,1],...
                             'FontName', H.fontname ,...
                             'FontSize', H.fontsize ,...
                             'Tag','image2' ,...
                             'position',[0.02,0.02,0.96,0.96]);
        H.activeControlTags = [ H.activeControlTags, 'image2panel' ];
        imshow(image2Path);
        xlabel(images(2).imageCaption);
    end

    function displayImages(images)
        imageCount = length(images);
        if (imageCount == 1)
           displaySingleImage(images);
        elseif (imageCount == 2)
            displayImagePair(images);
        end
    end



    %
    %  NAVIGATION
    %

    function backFromEndMessageScreen(hObject, eventData)
        deleteObsoleteControls();
        if (strcmp(H.mostRecentQAFlavor,'typedInput'))
            createTypedInputQAPanels();
        else
            createChoiceQAPanels();
        end
        
        showPrevQuestion(hObject, eventData);
    end

    function doAnotherCharacter(hObject, eventData)
        %H.questionSequencer.persist();
        xmlFile = QuestionsXMLFile(H.questionnaireXML);
        qquestions = QQuestions(xmlFile.domNode);
        H.questionSequencer = QuestionSequencer(qquestions);
        %deleteObsoleteControls();
        %createTypedInputQAPanels();
        displayMatrixQuestion();
    end

    function saveAndExit(hObject, eventData)
        
        %H.questionSequencer.persist();
        close();
    end

    function exit(hObject, eventData)
        close();
    end





    function answerToNextQuestion = registerDisplayedAnswer()
        answerToNextQuestion = 'NOT_YET_SPECIFIED';
        try
            if strcmp(H.activeQuestionId,'characterQuestion')
                indexOfCharacterAnswer = get(H.characterChoice,'value');
                H.characterName = char(H.characterChoices(indexOfCharacterAnswer));
                H.questionSequencer.characterName = H.characterName;
                if (not(isempty(H.questionSequencer.answeredQuestions)))
                    qanswer = H.questionSequencer.answeredQuestions(1);
                    answerToNextQuestion = qanswer.answer;
                end
            elseif strcmp(H.activeQuestionId,'matrixQuestion')
                indexOfMatrixAnswer = get(H.matrixChoice,'value');
                H.chosenMatrix = char(H.matrixChoices(indexOfMatrixAnswer));
                if ispc()
                    H.matrixDir = sprintf('%s\\matrix_downloads\\%s', H.rootDir, H.chosenMatrix);
                else
                    H.matrixDir = sprintf('%s/matrix_downloads/%s', H.rootDir, H.chosenMatrix);
                end
                
                if (strcmp(H.questionSequencer.matrixName,H.chosenMatrix))
                    % being re-answered to the same value, no need to flush
                    % the characterName
                else
                    % changing the choice (or its the initial choice) for
                    % matrix, need to clear characterName
                    H.questionSequencer.characterName = 'UNDEFINED';
                    %... and forget prior character choice index
                    H.characterChoiceIndex = 1;
                end
                H.questionSequencer.matrixName = H.chosenMatrix;
                if (not(strcmp(H.questionSequencer.characterName,'UNSPECIFIED')))
                    answerToNextQuestion = H.questionSequencer.characterName;
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
            answerToNextQuestion = 'ANSWER_BLOCKED_BY_ERROR';
        end
    end

    function showPrevQuestion(hObject, eventData)
        if strcmp(H.activeQuestionId,'characterQuestion') %TESTME
            displayMatrixQuestion();
            % set the chooser to the previous answer
            % get the index of the prev answer
            prevMatrixAnswer = H.questionSequencer.matrixName;
            % we know we're backing up to the matrix question, so it must
            % have a value to restore
            setPopupMenuToValue(H.matrixChoice,H.matrices.matrixDirNames,prevMatrixAnswer);
            %indexOfMatrixAnswer = find(ismember(H.matrices.matrixDirNames,prevMatrixAnswer));
            %set(H.matrixChoice,'value',indexOfMatrixAnswer);
        elseif H.questionSequencer.canBackUp()
            prevAnsweredQuestion = H.questionSequencer.backUp();
            prevAnswer = prevAnsweredQuestion.answer;
            qquestion = H.questionSequencer.getCurrentQuestion();
            displayAppropriateQuestion(qquestion);
            displayPriorSetAnswer(prevAnswer,qquestion);
        else
             
            % backing up to char question, must set to previous answer
            displayCharacterQuestion();%TESTME
            prevCharacterAnswer = H.questionSequencer.characterName;
            setPopupMenuToValue(H.characterChoice, H.characterChoices,prevCharacterAnswer);
            % set the chooser to the previous answer
            % get the index of the prev answer
            %set(control,'String',H.questionSequencer.characterName);
            
        end
        
    end

    function setPopupMenuToValue(popupMenu, stringsLoadedIntoPopupMenu, value)
        indexArray = find(ismember(stringsLoadedIntoPopupMenu,value));
        if (isempty(indexArray))
            msg = sprintf('Invalid choice for popupmenu: %s', value);
            err = MException('UI:BadPopupEntry', msg);
            throw(err);
        else
            set(popupMenu,'value',indexArray);
        end
    end

    function displayPriorSetAnswer(prevAnswer, qquestion)
        
        if strcmp(qquestion.type,'input_integer')
            control = findobj('Tag','input');
            set(control,'String',prevAnswer);
        elseif strcmp(qquestion.type,'input_string')
            control = findobj('Tag','input');
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
            displayInputQuestion(qquestion);
        elseif (strcmp(qquestion.type,'input_string'))
            displayInputQuestion(qquestion);
        else
            msg = sprintf('Invalid question type: %s', qquestion.type);
            err = MException('UI:BadQuestionType', msg);
            throw(err)
        end
    end

    function showNextQuestion(hObject, eventData)
        
        if verifyAnswerPresent()
            nextAnswer = registerDisplayedAnswer();
            if (strcmp(nextAnswer,'ANSWER_BLOCKED_BY_ERROR'))
                % stay on same question
            else 
                if (strcmp(H.activeQuestionId,'matrixQuestion'))
                    displayCharacterQuestion();
                    prevCharacterAnswer = H.questionSequencer.characterName;
                    if (strcmp(prevCharacterAnswer, 'UNDEFINED'))
                        % this is the first time visiting the character
                        % question since the matrix has been thusly set.
                        % Leave the default answer as the first in the
                        % matrix
                    else
                        % pre-existing answer can be loaded - we must have
                        % revisted the matrix question and not changed it.
                        setPopupMenuToValue(H.characterChoice, H.characterChoices,prevCharacterAnswer);
                        %indexOfCharacterAnswer = find(ismember(H.characterChoices,prevCharacterAnswer));
                        %set(H.characterChoice,'value',indexOfCharacterAnswer);)
                    end
                else
                    showNextQuestionSequencerQuestion(nextAnswer);
                end
                
            end
        else
            errordlg('Please answer the question before clicking "Next"')
        end
    end

    function showNextQuestionSequencerQuestion(nextAnswer)
        qquestion = H.questionSequencer.getCurrentQuestion();
        if (strcmp(qquestion.id,'NO_MORE_QUESTIONS'))
            displayQuestionnaireCompleteScreen();
        else
            displayAppropriateQuestion(qquestion);
            if (strcmp(nextAnswer,'NOT_YET_SPECIFIED'))
                % no answer to apply
            else
                % apply the previous answer
                displayPriorSetAnswer(nextAnswer, qquestion);
            end
        end
    end


    function showCurrentQuestion(hObject, eventData)
        qquestion = H.questionSequencer.getCurrentQuestion();
        if (strcmp(qquestion.id,'NO_MORE_QUESTIONS'))
            displayQuestionnaireCompleteScreen();
        else
            displayAppropriateQuestion(qquestion);
        end
    end

    function result = verifyAnswerPresent()
        
        result = 1;
        if (strcmp(H.activeControlType,'edit'))
            answer = get(H.activeAnswerControl, 'String');
            if (strcmp(answer,''))
                result = 0;
            end
            
        elseif (strcmp(H.activeControlType,'popupMenu'))
            % something always chosen
            result = 1;
        else % must be 'choice'
            answer = get(H.activeAnswerControl,'SelectedObject');
            if (isempty(answer))
                result = 0;
            end
        end
    end

end
   
















