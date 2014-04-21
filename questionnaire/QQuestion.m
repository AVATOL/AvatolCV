classdef QQuestion < handle
    properties
        type
        id
        text
        answers = {}
        images = {}
        
    end
    methods
        function obj = QQuestion(type, id, textString)
            obj.type = type;
            obj.id = id;
            obj.text = textString;
        end
        
        function addAnswer(obj, a)
            obj.answers = [obj.answers, a];
        end
        
        function addImage(obj, i)
            obj.images = [obj.images, i];
        end
        
        function result = isValidAnswer(obj, givenAnswer)
            result = false;
            if (strcmp(obj.type,'input_integer'))
                result = obj.isStringInteger(givenAnswer);
            elseif (strcmp(obj.type, 'choice'))
                for i = 1:length(obj.answers)
                    answer = obj.answers(i);
                    if (strcmp(answer.value, givenAnswer))
                        result = true;
                    end
                end
            else
                message = fprintf('unknown question type %s for question %s', obj.type,obj.id)
                exception = MException('QQuestion:BadType', message);
                throw(exception);
            end
        end
        
        function result = isStringInteger(obj, s)
            result = false;
            tf = isstrprop(s, 'digit');
            if (all(tf))
                result = true;
            end
        end
        
        function qanswer = getQAnswerForAnswerValue(obj, val)
            if (strcmp(obj.type,'choice'))
                qanswer = obj.getQAnswerForChoiceAnswerValue(val);
            elseif (strcmp(obj.type,'input_integer' ))
                qanswer = obj.answers(1);
            else
                message = fprintf('unknown question type %s for question %s', obj.type,obj.id)
                exception = MException('QQuestion:BadType', message);
                throw(exception);
            end
            
        end
        
        function qanswer = getQAnswerForChoiceAnswerValue(obj, val)
            qanswer = QAnswer('NULL','NULL');
            for i = 1:length(obj.answers)
                answer = obj.answers(i);
                if (strcmp(answer.value, val))
                    qanswer = answer;
                end
            end
        end
    end
end