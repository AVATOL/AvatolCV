classdef ResultsReviewScreen < handle
    %RESULTSREVIEW Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        ui;
        session;
        ssms;
        dataFocus = 'scoredImages';
        sessionData;
        sessionDataForTaxa;
        resultMatrixColumn;
        imageControlTags = {};
        %metadataControlTags = {};
        heightRatio;
        matrixHeight;
        javaPanel;
        
        imageBrowserHostPanel;
        resultMatrixPanel;
        resultMatrixPanelGUIHandle;
        runSelector;
    end
    
    methods
        function obj = ResultsReviewScreen(ui, session)
            obj.ui = ui;
            obj.session = session;
        end
        function reset(obj)
            obj.ssms = obj.session.scoredSetMetadatas;
            obj.ssms.loadAll();
            obj.session.javaUI.setScoredSetMetadatas(obj.session.scoredSetMetadatas);
            matrixOfMostRecentRun = char(obj.ssms.getMatrixNameFromKey(obj.ssms.getCurrentKey()));
            obj.session.matrixChoiceScreen.registerMatrixChoiceNoProgressBar(matrixOfMostRecentRun);
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
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'doAnotherCharacter' ];
            
            exit = uicontrol('style', 'pushbutton' ,...
                                         'Parent',obj.ui.navigationPanel,...
                                         'Units','normalized',...
                                         'String', 'Exit' ,...
                                         'position', obj.ui.getButtonPositionRightB() ,...
                                         'FontName', obj.ui.fontname ,...
                                         'FontSize', obj.ui.fontsize ,...
                                         'Tag','exit' ,...
                                         'BackgroundColor', [0.8 0.8 0.8]);  
            obj.ui.activeControlTags = [ obj.ui.activeControlTags, 'exit' ];
          
           
            %obj.ui.deleteControls(obj.metadataControlTags);
           %  obj.loadRunSelector();
           %  obj.loadMatrixColumn();
           %  obj.loadImageWidgets();
            
            
            
            obj.sessionDataForTaxa = obj.ssms.getSessionDataForTaxa(obj.session.morphobankBundle);
            obj.session.javaUI.createComponents(obj.sessionDataForTaxa);
            obj.javaPanel =  obj.session.javaUI.getContainingPanel();
            [hjavaPanel,javaPanelHandle] = javacomponent(obj.javaPanel,[10,40,1200,670],obj.ui.javaPanel);
            %
            %
            % ???????? obj.ui.activeAnswerControl = obj.taxonChoiceWidget;
            obj.session.activeQuestionId = 'ResultsReview';
            obj.ui.activeControlType = 'ResultsReview';

            set(exit, 'callback', {@obj.exit});
            set(doAnotherCharacter, 'callback', {@obj.doAnotherCharacter});
            obj.session.mostRecentScreen = 'RESULTS_REVIEW_SCREEN'; 
        end
        
       
        
        function loadRunSelector(obj)
            import edu.oregonstate.eecs.iis.avatolcv.ui.RunSelector;
            obj.runSelector = obj.session.javaUI.createRunSelector();
           
            [runSelectorPanel,hContainer] = javacomponent(obj.runSelector,[0,5,1200,30],obj.ui.resultsTopPanel);
        end
        
        function loadMatrixColumn(obj)
            obj.sessionDataForTaxa = obj.ssms.getSessionDataForTaxa(obj.session.morphobankBundle);
            obj.resultMatrixColumn = obj.session.javaUI.createResultMatrixColumn(obj.sessionDataForTaxa);
            
            obj.resultMatrixPanel = obj.resultMatrixColumn.getContainingPanel();
            [hContainingPanel,obj.resultMatrixPanelGUIHandle] = javacomponent(obj.resultMatrixPanel,[10,40,400,600],obj.ui.resultsLeftPanel);
       
        end
        function loadImageWidgets(obj)
            obj.ui.deleteControls(obj.imageControlTags);
            obj.ui.imageNavigationPanel
            import edu.oregonstate.eecs.iis.avatolcv.ui.ImageBrowser;
            imageBrowser = obj.resultMatrixColumn.getActiveImageBrowser();
            obj.imageBrowserHostPanel = imageBrowser.getImageBrowserHostPanel();
            import javax.swing.JTextArea;
            import javax.swing.JTabbedPane;
            import javax.swing.JLabel;
            import java.awt.Color;
            import java.awt.Dimension;
           
            dimension = Dimension(700,600);
            obj.imageBrowserHostPanel.setPreferredSize(dimension);
            [jhPanel,hContainer] = javacomponent(obj.imageBrowserHostPanel,[10,40,800,600],obj.ui.resultsRightPanel);
        end
       
        
      
        %function showNextImage(obj, hObject, eventData)
        %    obj.sessionData.goToNextImage();
        %    obj.loadImageWidgets();
        %end
       % function showPrevImage(obj, hObject, eventData)
        %    obj.sessionData.goToPrevImage();
        %    obj.loadImageWidgets();
        %end
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
       
        function doAnotherCharacter(obj,hObject, eventData)
            obj.imageBrowserHostPanel.removeAll();
            obj.imageBrowserHostPanel.revalidate();
            obj.runSelector.removeAll();
            obj.runSelector.revalidate();
            obj.resultMatrixPanel.removeAll();
            obj.resultMatrixPanel.revalidate();
            obj.session.doAnotherCharacter();
        end
        function exit(obj, hobject, eventData)
            obj.session.exit();
        end
    end
    
end

