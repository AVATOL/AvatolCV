package edu.oregonstate.eecs.iis.avatolcv.tutorial;

public class InfoPages {

}
/*
 * classdef InfoPages < handle
   properties
        info_pages = {};
    end
    methods
        function obj = InfoPages(domNode)
            obj.parseDomNodeIntoInfoPages(domNode);
        end
        function value = getInfoPages(obj)
            value = obj.info_pages; 
        end
        function parseDomNodeIntoInfoPages(obj,domNode)
            infoPagesNode = domNode.getDocumentElement;
            %fprintf('docNode name %s\n', char(infoPagesNode.getNodeName()));
            infoNodesAndWhiteSpaceNodes = infoPagesNode.getChildNodes;
            count = infoNodesAndWhiteSpaceNodes.getLength; 
            for i=0:count-1
                infoNode = infoNodesAndWhiteSpaceNodes.item(i);
                nodeName = infoNode.getNodeName();
                if (strcmp(nodeName, '#text'))
                    % skip blank text
                else
                    %fprintf('infoNode name %s\n', char(infoNode.getNodeName()));
                    infoPage = obj.createInfoPage(infoNode);
                    obj.info_pages = [ obj.info_pages, infoPage ];
                end
            end
%            validator = InfoPagesValidator();
%            validator.validate(obj.info_pages);
        end
        function info_page = createInfoPage(obj,qnode)
            childNodes = qnode.getChildNodes;
            images = {};
            infoPageText = '';
            infoPageNext = '';
            infoPageId = '';
            childCount = childNodes.getLength;
            for i=0:childCount-1
                child = childNodes.item(i);
                name = child.getNodeName();
                if (strcmp(name,'#text'))
                    %ignore
                elseif (strcmp(name,'text'))
                    infoPageText = char(child.getTextContent);
                elseif (strcmp(name,'image'))
                    image = obj.createQImage(child);
                    images = [ images, image ];
                else
                    msg = sprintf('Unrecognized element in InfoPageNode: %s',char(name));
                    err = MException('InfoPagesParse:UnrecognizedElement', msg);
                    throw(err);
                end
            end
            infoPageNext = char(qnode.getAttribute('next'));
            infoPageId = char(qnode.getAttribute('id'));
            info_page = InfoPage(infoPageId, infoPageText, infoPageNext);
            imageCount = numel(images);
            for i = 1:imageCount
                image = images(i);
                info_page.addImage(image);
            end
        end
         % <image filename="elephant.jpg" caption="elephants are bigger than a breadbox"/> 
        function image = createQImage(obj,inode)
            filename = char(inode.getAttribute('filename'));
            %fprintf('filename : %s\n', filename);
            caption = char(inode.getAttribute('caption'));
            %fprintf('caption : %s\n', caption);
            image = QImage(filename, caption);
        end
        
        function infoPage = findInfoPageById(obj, id)
            infoPage = InfoPage('NULL', 'NULL', 'NULL');
            for i=1:length(obj.info_pages)
                cur_info_page = obj.info_pages(i);
                if (strcmp(cur_info_page.id,id))
                    infoPage = cur_info_page;
                    break;
                end
            end
        end
    end
end


 */
