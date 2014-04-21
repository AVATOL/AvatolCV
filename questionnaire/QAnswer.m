classdef QAnswer < handle
    properties
        value
        nextQuestion
    end
    methods
        function obj = QAnswer(val, next)
            fprintf('value coming in as %s\n',val);
            obj.value = val;
            fprintf('value now is %s\n', obj.value);
            obj.nextQuestion = next
            fprintf('nextQuestion now is %s\n', obj.nextQuestion);
        end
        
        function value = getValue(obj)
        end
        
        function nextQuestion = getNextQuestion(obj)
        end
    end
end