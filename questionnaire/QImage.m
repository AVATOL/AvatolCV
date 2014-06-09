classdef QImage < handle 
    properties
        imageFilePath;
        imageCaption;
    end
    methods
        function obj = QImage(path, caption)
            if ispc
                path = strrep(path, '/', '\\');
            end
            fprintf('new path is %s\n',path);
            obj.imageFilePath = path;
            obj.imageCaption = caption;
        end
        
        function imageFilePath = getImagePath(obj)
        end
        
        function imageCaption = getImageCaption(obj)
        end
    end
end