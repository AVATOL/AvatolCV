classdef AnsweredQuestion
    %ANSWEREDQUESTION Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        questionID;
        answer;
    end
    
    methods
        function obj = AnsweredQuestion(id, answer)
            obj.questionID = id;
            obj.answer = answer;
        end
    end
    
end

