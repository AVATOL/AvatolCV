classdef Annotation < handle
    properties
        coordinateList;
        type;
        charId;
        charNameText;
        charState;
        charStateText;
        lineNumber;
        mediaId;
        pathname;
    end
    
    methods
        function obj = Annotation(info, lineNumber,mediaId, pathname)
            parts = strsplit(info,':');
            fprintf('annotation info: %s\n',info);
            obj.coordinateList = char(parts(1));
            obj.type = obj.deriveType(obj.coordinateList);
            obj.charId = char(parts(2));
            obj.charNameText = char(parts(3));
            obj.charState = char(parts(4));
            obj.charStateText = char(parts(5));
            obj.lineNumber = lineNumber;
            obj.mediaId = mediaId;
            obj.pathname = pathname;
        end
        
        function type = deriveType(obj, coordinateList)
            parts = strsplit(coordinateList,';');
            if length(parts) == 1
                type = 'point';
            elseif length(parts) == 2
                type = 'box';    
            else
                type = 'polygon';
            end
        end
       
    end
end