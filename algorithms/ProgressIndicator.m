classdef ProgressIndicator  < handle
    %ProgressIndicator  communicates progress of algorithms
    
    properties
        statusWidget;
    end
    
    methods
        function obj = ProgressIndicator(statusWidget)
            obj.statusWidget = statusWidget;
        end
        
		function setStatus(obj, statusMessage)
            set(obj.statusWidget, 'string', statusMessage);
		end
    end
    
end

