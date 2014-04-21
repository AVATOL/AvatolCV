classdef QuestionsXMLFile 
    properties
        domNode
    end
    
    methods
        function obj = QuestionsXMLFile(path)
            try 
                obj.domNode = xmlread(path);
            catch ME
                fprintf('unable to parse xml file %s %s', path, ME.message)
            end
        end
        
        function domNode = getDomNode(obj)
        end
       
    end
end