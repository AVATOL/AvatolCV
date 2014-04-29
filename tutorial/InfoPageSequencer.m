classdef InfoPageSequencer < handle
    properties
        
        info_pages;
        nextInfoPageIndex;
        currentInfoPage;
        noMoreInfoPagesMarker;
    end
    methods
        
        function obj = InfoPageSequencer(info_pages)
            obj.nextInfoPageIndex = 1;
            obj.info_pages = info_pages;
            obj.currentInfoPage = obj.info_pages.info_pages(obj.nextInfoPageIndex);
            obj.noMoreInfoPagesMarker = QQuestion('NO_MORE_QUESTIONS','NO_MORE_QUESTIONS','NO_MORE_QUESTIONS');
        end
        
        function currentInfoPage = getCurrentInfoPage(obj)
            currentInfoPage = obj.currentInfoPage;
        end
        
        function moveToNextPage(obj)
            nextId = obj.currentInfoPage.next;
            if (strcmp(nextId,'NO_MORE_PAGES'))
                message = sprintf('currentInfoPage %s is the final page, cannot move forward', obj.currentInfoPage.id);
                exception = MException('InfoPageSequencer:NavigationError', message);
                throw(exception);
            end
            obj.currentInfoPage = obj.info_pages.findInfoPageById(nextId);
            obj.nextInfoPageIndex = obj.nextInfoPageIndex + 1;
        end
      
        function result = isAllInfoPagesShown(obj)
            result = false;
            if (strcmp(obj.currentInfoPage.next,'NO_MORE_PAGES'))
                result = true;
            end
        end
        
        function question = findInfoPageById(obj, id)
            question = obj.info_pages.findInfoPageById(id);
        end
        
        function result = canBackUp(obj)
            result = true;
            if (obj.nextInfoPageIndex == 1)
                result = false;
            end
        end
        
        function backUp(obj)
            if (obj.nextInfoPageIndex == 1)
                message = sprintf('currentInfoPage %s is the first page, cannot back up', obj.currentInfoPage.id);
                exception = MException('InfoPageSequencer:NavigationError', message);
                throw(exception);
            end 
            obj.nextInfoPageIndex = obj.nextInfoPageIndex - 1;
            
            
            %obj.answeredQuestions = obj.answeredQuestions(1:length(obj.answeredQuestions) - 1);
            obj.currentInfoPage = obj.info_pages.info_pages(obj.nextInfoPageIndex);
        end
    end
    
    
end

