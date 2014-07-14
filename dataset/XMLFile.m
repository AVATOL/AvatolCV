classdef XMLFile 
    properties
        domNode;
    end
    
    methods
        function obj = XMLFile(path)
            try 
                obj.domNode = xmlread(path);
            catch ME
                disp('unable to parse xml file %s %s', path, ME.message);
            end
        end
        
        function domNode = getDomNode(obj)
        end
       
    end
end