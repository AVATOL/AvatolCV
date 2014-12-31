classdef ResultsReviewScreen < handle
    %RESULTSREVIEW Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        ui;
        session;
        ssms;
        dataFocus = 'scoredImages';
        sessionData;
        runSelector;
        sessionDataForTaxa;
        resultMatrixColumn;
        imageControlTags = {};
        metadataControlTags = {};
        heightRatio;
        matrixHeight;
        
        javaTaxaScrollPane;
        javaTaxaConfidenceSlider;
        javaConfidenceLabel;
    end
    
    methods
        function obj = ResultsReviewScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        function reset(obj)
            obj.ssms = obj.session.scoredSetMetadatas;
            obj.ssms.loadAll();
            matrixOfMostRecentRun = char(obj.ssms.getMatrixNameFromKey(obj.ssms.getCurrentKey()));
            obj.session.matrixChoiceScreen.registerMatrixChoice(matrixOfMostRecentRun);
            obj.sessionData = obj.ssms.getSessionResultsData(obj.session.morphobankBundle);
        end
        function showResults(obj)
            
            obj.ui.deleteObsoleteControls();
            obj.ui.createResultsReviewPanels();
            % create panel for instructions
		
            doAnotherCharacter = uicontrol('style', 'pushbutton' ,...
                                         'String', 'Score a different character' ,...
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
        function showResultsOld(obj)
            
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
            scoredImagesValue = 0;
            unscoredImagesValue = 0;
            trainingValue = 0; 
            
            if obj.sessionData.isFocusScoredImages()
                scoredImagesValue = 1;
            elseif obj.sessionData.isFocusUnscoredImages()
                unscoredImagesValue = 1;
            else
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
            scoredImagesRadio = uicontrol('Style','radiobutton',...
                                     'visible', 'on',...
                                     'String','scored' ,...
                                     'Background', 'white' ,...
                                     'Units', 'normalized',...
                                     'Value',scoredImagesValue,...
                                     'Position',[0, 0, 0.3, 1.0],...
                                     'Parent',buttonGroup,...
                                     'FontName', obj.ui.fontname ,...
                                     'FontSize', obj.ui.fontsize ,...
                                     'Tag', 'scoredImagesRadio',...
                                     'HandleVisibility', 'off');
   
            unscoredImagesRadio = uicontrol('Style','radiobutton',...
                                     'visible', 'on',...
                                     'String','unscored' ,...
                                     'Background', 'white' ,...
                                     'Units', 'normalized',...
                                     'Value',unscoredImagesValue,...
                                     'Position',[0.35, 0, 0.3, 1.0],...
                                     'Parent',buttonGroup,...
                                     'FontName', obj.ui.fontname ,...
                                     'FontSize', obj.ui.fontsize ,...
                                     'Tag', 'unscoredImagesRadio',...
                                     'HandleVisibility', 'off');
            %obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'resultsRadio' ];
            trainingImagesRadio = uicontrol('Style','radiobutton',...
                                     'visible', 'on',...
                                     'String','training' ,...
                                     'Background', 'white' ,...
                                     'Units', 'normalized',...
                                     'Value',trainingValue,...
                                     'Position',[0.7, 0, 0.3, 1.0],...
                                     'Parent',buttonGroup,...
                                     'FontName', obj.ui.fontname ,...
                                     'FontSize', obj.ui.fontsize ,...
                                     'Tag', 'trainingImagesRadio',...
                                     'HandleVisibility', 'off');

            %obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'trainingImagesRadio' ];
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
            set(scoredImagesRadio, 'callback', {@obj.setFocusToScored});
            set(unscoredImagesRadio, 'callback', {@obj.setFocusToUnscored});
            set(trainingImagesRadio, 'callback', {@obj.setFocusToTraining});
            obj.session.mostRecentScreen = 'RESULTS_REVIEW_SCREEN'; 
        end
        function setFocusToUnscored(obj, hObject, eventData)
            obj.sessionData.setFocusAsUnscored();
            obj.showResults();
        end
        function setFocusToScored(obj, hObject, eventData)
            obj.sessionData.setFocusAsScored();
            obj.showResults();
        end
        function setFocusToTraining(obj, hObject, eventData)
            obj.sessionData.setFocusAsTraining();
            obj.showResults();
        end
        
        function loadImageWidgets(obj)
            obj.ui.deleteControls(obj.imageControlTags);
            obj.ui.imageNavigationPanel
            import edu.oregonstate.eecs.iis.avatolcv.ui.ImageBrowser;
            imageBrowser = obj.resultMatrixColumn.getActiveImageBrowser();
            imageBrowserHostPanel = imageBrowser.getImageBrowserHostPanel();
            import javax.swing.JTextArea;
            import javax.swing.JTabbedPane;
            import javax.swing.JLabel;
            import java.awt.Color;
            import java.awt.Dimension;
           
            dimension = Dimension(700,600);
            imageBrowserHostPanel.setPreferredSize(dimension);
            [jhPanel,hContainer] = javacomponent(imageBrowserHostPanel,[10,40,800,600],obj.ui.resultsRightPanel);
        end
        function loadImageWidgetsOld(obj)
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
            backImageButtonNeeded = obj.sessionData.canGoToPrevImage();
            if backImageButtonNeeded
                set(prevImage,'Enable','on');
            end
            %
            %
            positionInListString = char(obj.sessionData.getPositionInListString());
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
            imageContextString = char(obj.sessionData.getImageContextString());
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
            nextImageButtonNeeded = obj.sessionData.canGoToNextImage();
            if nextImageButtonNeeded
                set(nextImage,'Enable','on');
            end
            obj.loadImage();
        end
        %function generateMatrixColumn(obj)
        %    resultColumnMatrix = ResultMatrixColumn(obj.session.morphobankBundle, obj.sessionData);
        %    fprintf('cell count %c', resultColumnMatrix.getCount());    
        %end
       
        function slider_callback1(obj,src,eventdata,arg1)
            val = get(src,'Value');
            yHiddenDistance = obj.heightRatio - 1;
            yPosition =  -yHiddenDistance * val;
            set(arg1,'Position',[0 yPosition 1 obj.matrixHeight])
        end
        function loadMatrixColumn(obj)
           obj.sessionDataForTaxa = obj.ssms.getSessionDataForTaxa(obj.session.morphobankBundle);
           
            import edu.oregonstate.eecs.iis.avatolcv.ui.ResultMatrixColumn;
            obj.resultMatrixColumn = ResultMatrixColumn(obj.session.morphobankBundle, obj.sessionDataForTaxa);
            containingPanel = obj.resultMatrixColumn.getContainingPanel();
            %jScrollPane = com.mathworks.mwswing.MJScrollPane(obj.resultMatrixColumn);
            [hContainingPanel,hContainer] = javacomponent(containingPanel,[10,40,400,600],obj.ui.resultsLeftPanel);
            
            %label = obj.resultMatrixColumn.getConfidenceLabel();
            %[obj.javaConfidenceLabel,hContainer] = javacomponent(label,[0,610,380,30],obj.ui.resultsLeftPanel);
            
            %slider = obj.resultMatrixColumn.getConfidenceSlider();
            %[obj.javaTaxaConfidenceSlider,hContainer] = javacomponent(slider,[0,534,380,70],obj.ui.resultsLeftPanel);
             
        end
        function loadRunSelector(obj)
            import edu.oregonstate.eecs.iis.avatolcv.ui.RunSelector;
            obj.runSelector = RunSelector(obj.ssms);
           
            [runSelectorPanel,hContainer] = javacomponent(obj.runSelector,[0,5,1100,30],obj.ui.resultsTopPanel);
        end
        function loadMetadataWidgets(obj)
            obj.ui.deleteControls(obj.metadataControlTags);
            obj.loadRunSelector();
            obj.loadMatrixColumn();
        end
        function loadMetadataWidgetsOld(obj)
            curMetadata = obj.ssms.getDisplayableData();
            obj.ui.deleteControls(obj.metadataControlTags);
            obj.loadRunSelector();
            obj.loadMatrixColumn();
            
            metadataContent = uicontrol('style', 'text' ,...
                                         'Parent',obj.ui.scoredSetMetadataPanel,...
                                         'Units','normalized',...
                                         'String', char(curMetadata) ,...
                                         'position', [0.00,0.00,1.0,1.0] ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','metadataContent' ,...
                                         'Background',[1 1 1],...
                                         'HorizontalAlignment', 'left');%'BackgroundColor', [0.1 1 0.1] ,...
            
            obj.metadataControlTags = { 'metadataContent' };
            nextButtonNeeded = false;
            backButtonNeeded = false;
            if obj.ssms.getSetCount()==1
                % no navigationButtons
            else
                if obj.ssms.backButtonNeeded()
                    backButtonNeeded = true;
                end
                if obj.ssms.nextButtonNeeded()
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
            runPositionInListString = char(obj.ssm.getPositionInList());
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
            
        function showNextImage(obj, hObject, eventData)
            obj.sessionData.goToNextImage();
            obj.loadImageWidgets();
        end
        function showPrevImage(obj, hObject, eventData)
            obj.sessionData.goToPrevImage();
            obj.loadImageWidgets();
        end
        function loadImage(obj)
            if not(obj.sessionData.canShowImage())
                return;
            end
            currentFigure = gcf;
            set(currentFigure, 'pointer', 'watch')
            drawnow;
            
            fprintf('loadImage\n');
            
            resultImage = obj.sessionData.getCurrentResultImage();
            mediaPath = char(resultImage.getMediaPath());
            
            axesPanel = uipanel('Parent',obj.ui.imagePanel,...
                             'Tag','imagePanel' ,...
                             'position',[0,0,1,1]);
            axes1 = axes('Parent',axesPanel,...
                             'Color',[1,1,1],...
                             'FontName', obj.ui.fontname ,...
                             'FontSize', obj.ui.fontsize ,...
                             'Tag','resultImage' ,...
                             'position',[0,0,1,1]);%[0.02,0.02,0.96,0.76]
            %obj.ui.activeControlTags = [ obj.ui.activeControlTags,
            %'resultImage' ];
            image = imread(mediaPath);
            [imageHeight, imageWidth, imageDim] = size(image);
            imshow(mediaPath);
            if resultImage.hasAnnotationCoordinates()
                 fprintf('drawingCoords\n');
                 annotationCoordinates = resultImage.getAnnotationCoordinates();
                 %FIXME need to draw multiple coords and lines if needed
                 
                 pointAsPercent = annotationCoordinates.getPoints().get(0);
                 x = pointAsPercent.getXPixel(imageWidth);
                 y = pointAsPercent.getYPixel(imageHeight);
                 hold on;
                 plot(x,y,'r.','MarkerSize',8)
             end
               
             %xlabel(char(resultImage.getCharacterName()));
             set(currentFigure, 'pointer', 'arrow')
        end  
        function showNextMetadata(obj, hObject, eventData)
            %obj.metadataKeyIndex = obj.metadataKeyIndex + 1;
            %obj.updateFolders
            obj.ssm.goToNextSession();
            obj.sessionData = obj.ssm.getSessionResultsData(obj.session.morphobankBundle);
            obj.loadMetadataWidgets();
            obj.loadImageWidgets();
        end
        function showPreviousMetadata(obj, hObject, eventData)
            %obj.metadataKeyIndex = obj.metadataKeyIndex - 1;
            %obj.updateFolders();
            obj.ssm.goToPrevSession();
            obj.sessionData = obj.ssm.getSessionResultsData(obj.session.morphobankBundle);
            obj.loadMetadataWidgets();
            obj.loadImageWidgets();
        end
        function doAnotherCharacter(obj,hObject, eventData)
            delete(obj.javaConfidenceLabel);
            delete(obj.javaTaxaConfidenceSlider);
            delete(obj.javaTaxaScrollPane);
            obj.session.doAnotherCharacter();
        end
        function exit(obj, hobject, eventData)
            obj.session.exit();
        end
    end
    
end

