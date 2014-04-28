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
                if (obj.isStringInteger(givenAnswer))
                    result = true;
                else
                    exception = MException('QQuestion:BadType', 'answer must be integer');
                    throw(exception);
                end
            elseif (strcmp(obj.type,'input_string'))
                if (obj.isStringString(givenAnswer))
                    result = true;
                else
                    exception = MException('QQuestion:BadType', 'answer must be string');
                    throw(exception);
                end
            elseif (strcmp(obj.type, 'choice'))
                for i = 1:length(obj.answers)
                    answer = obj.answers(i);
                    if (strcmp(answer.value, givenAnswer))
                        result = true;
                    end
                end
            else
                message = sprintf('unknown question type %s for question %s', obj.type,obj.id)
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
        
        function result = isStringString(obj, s)
            result = true;
            s = strrep(s, '\n', '')
            s = strrep(s, '\t', '')
            if (strcmp(s,''))
                result = false;
            end
        end
        function qanswer = getQAnswerForAnswerValue(obj, val)
            if (strcmp(obj.type,'choice'))
                qanswer = obj.getQAnswerForChoiceAnswerValue(val);
            elseif (strcmp(obj.type,'input_integer' ))
                qanswer = obj.answers(1);
            elseif (strcmp(obj.type,'input_string' ))
                qanswer = obj.answers(1);
            else
                message = sprintf('unknown question type %s for question %s', obj.type,obj.id)
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