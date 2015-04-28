package edu.oregonstate.eecs.iis.avatolcv.tutorial;

public class InfoPagesValidator {

}
/*
*
*classdef InfoPagesValidator < handle
   properties
        
    end
    methods
        function obj = InfoPagesValidator()
        end

        function result = validate(obj, info_pages)
            infoPagesCountValid = obj.validateInfoPagesCount(info_pages);
            if (not(infoPagesCountValid))
                msg = sprintf('Invalid number of tutorial pages.');
                err = MException('Validate:BadInfoPagesCount', msg);
                throw(err);
            end
            replicatedIds = obj.getDuplicateIds(info_pages);
            if (not(isempty(replicatedIds)))
                msg = sprintf('Duplicate key in infoPages file: %s', replicatedId);
                err = MException('Validate:DupicateId', msg);
                throw(err);
            end
            unusedIds = obj.getUnusedInfoPages(info_pages);
            if (not(isempty(unusedIds)))
                msg = sprintf('unused info_pages : %s', unusedIds);
                err = MException('Validate:UnusedIds', msg);
                throw(err);
            end
            infoPageMalformations = obj.getInfoPagesMalformations(info_pages);
            if (not(isempty(infoPageMalformations)))
                msg = 'malformed info_pages : ';
                infoPageMalformationCount = length(infoPageMalformations);
                for i=1:infoPageMalformationCount
                    msg = sprintf('%s%s ', msg, infoPageMalformations{i});
                end
                err = MException('Validate:InfoPageMalformed', msg);
                throw(err);
            end
            %noLoopsDetected = obj.validateNoLoops(questions);
            %noBadNextPointers = obj.validateNoBadNextPointers(questions);
            %result = questionCountValid & idsUnique & questionsAllUsed & allQuestionsWellFormed & noLoopsDetected & noBadNextPointers;
        end
        
        function malformations = getInfoPagesMalformations(obj,info_pages)
            malformations = {};
            for i=1:length(info_pages)
                info_page = info_pages(i);
                curInfoPageMalformations = obj.getInfoPageMalformations(info_page);
                if (not(isempty(curInfoPageMalformations)))
                    infoPageMalformationCount = length(curInfoPageMalformations);
                    for j=1,infoPageMalformationCount
                        malformations = [ malformations, curInfoPageMalformations(j) ];
                    end
                end
                
             end
        end
        
        function malformations = getInfoPageMalformations(obj, info_page)
            malformations = {};
            % id is not ''
            if (strcmp(info_page.id,''))
                malformations = [ malformations, 'info_page id empty' ];
            end
            % text is not ''
            if (strcmp(info_page.text,''))
                textError = sprintf('info_page text empty for %s',info_page.id);
                malformations = [ malformations, textError ];
            end
            
            imageMalformations = obj.getImageMalformationsForInfoPage(info_page);
            if (not(isempty(imageMalformations)))
                imageMalformationCount = length(imageMalformations)
                for i=1,imageMalformationCount
                    malformations =  [ malformations, imageMalformations(i) ];
                end
            end
           
        end
      
      
      
       
        
        
        function malformations = getImageMalformationsForInfoPage(obj, info_page)
            malformations = {};
            imageCount = length(info_page.images);
            if (imageCount > 0)
                for i=1:imageCount
                    image = info_page.images(i);
                    imageMalformations = obj.getImageMalformations(image);
                    if (not(isempty(imageMalformations)))
                        imageMalformationCount = length(imageMalformations);
                        for j=1,imageMalformationCount
                            malformations = [ malformations, imageMalformations(j) ];
                        end
                    end
                end
            end
        end
        
        function malformations = getImageMalformations(obj, qimage)
            malformations = {};
            % filename not ''
            currentDir = pwd();
            relPath = qimage.imageFilePath;
            if ispc
                imagePath = sprintf('%s\\%s',currentDir, relPath);
            else
                imagePath = sprintf('%s/%s',currentDir, relPath);
            end
            if (strcmp(qimage.imageFilePath,''))
                malformations = [ malformations,  'image filename empty'];
            elseif exist(imagePath, 'file') ~= 2
                error = sprintf('image filename does not exist: %s',relPath);
                malformations = [ malformations, error ] ;
            end
                
            % caption not ''
            if (strcmp(qimage.imageCaption,''))
                malformations = [ malformations, 'image caption empty' ];
            end
        end
        
       
        
        
        
        function result = validateInfoPagesCount(obj, info_pages)
            result = true;
            infoPagesLength = length(info_pages);
            if (infoPagesLength == 0)
                result = false;
            end
        end
        
        function replicatedIds = getDuplicateIds(obj, info_pages)
            replicatedIds = {};
            ids = {};
            infoPagesLength = length(info_pages);
            for i=1:infoPagesLength
                infoPage = info_pages(i);
                newId = infoPage.id;
                if (ismember( newId, ids))
                    replicatedIds = [ replicatedIds, newId ];
                else
                    ids = [ ids, newId ];
                end
            end
        end
        
        function unusedIds = getUnusedInfoPages(obj, info_pages)
           ids = {};
           infoPagesLength = length(info_pages);
           for i=2:infoPagesLength
               info_page = info_pages(i);
               ids = [ ids, info_page.id ];
           end
           
           for i=1:infoPagesLength
               info_page = info_pages(i);
               nextInfoPageId = info_page.next;
               ids = obj.removeMatchStringFromList(nextInfoPageId, ids);
           end
           unusedIds = ids;
        end
        
        function newList = removeMatchStringFromList(obj, s, oldList)
            newList = {};
            count = length(oldList);
            for i=1:count
                member = oldList(i);
                if (not(strcmp(member,s)))
                    newList = [ newList, member ];
                end
            end
        end
    end
end


*/