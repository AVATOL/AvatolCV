classdef QQuestions < handle
    properties
        questions = {}
    end
    methods
        function obj = QQuestions(domNode)
            obj.parseDomNodeIntoQuestions(domNode) 
        end
        function value = getQuestions(obj)
            value = obj.questions; 
        end
        function parseDomNodeIntoQuestions(obj,domNode)
            questionsNode = domNode.getDocumentElement;
            fprintf('docNode name %s\n', char(questionsNode.getNodeName()));
            questionNodesAndWhiteSpaceNodes = questionsNode.getChildNodes;
            count = questionNodesAndWhiteSpaceNodes.getLength; 
            for i=0:count-1
                questionNode = questionNodesAndWhiteSpaceNodes.item(i);
                nodeName = questionNode.getNodeName();
                if (strcmp(nodeName, '#text'))
                    % skip blank text
                else
                    fprintf('questionNode name %s\n', char(questionNode.getNodeName()));
                    question = obj.createQQuestion(questionNode)
                    obj.questions = [ obj.questions, question ]
                end
            end
            validator = QuestionsValidator();
            validator.validate(obj.questions);
        end
        function question = createQQuestion(obj,qnode)
            childNodes = qnode.getChildNodes;
            answers = {};
            images = {};
            questionText = '';
            questionType = '';
            questionId = '';
            childCount = childNodes.getLength;
            for i=0:childCount-1
                child = childNodes.item(i)
                name = child.getNodeName();
                if (strcmp(name,'#text'))
                    %ignore
                elseif (strcmp(name,'answer'))
                    answer = obj.createQAnswer(child)
                    answers = [ answers, answer ]
                elseif (strcmp(name,'text'))
                    questionText = char(child.getTextContent);
                elseif (strcmp(name,'image'))
                    image = obj.createQImage(child)
                    images = [ images, image ]
                else
                    msg = sprintf('Unrecognized element in QuestionNode: %s',char(name));
                    err = MException('QuestionsParse:UnrecognizedElement', msg);
                    throw(err)
                end
            end
            questionType = char(qnode.getAttribute('type'));
            questionId = char(qnode.getAttribute('id'));
            question = QQuestion(questionType, questionId, questionText)
            answerCount = numel(answers);
            for i = 1:answerCount
                answer = answers(i)
                question.addAnswer(answer)
            end
            imageCount = numel(images)
            for i = 1:imageCount
                image = images(i)
                question.addImage(image)
            end
        end
         % <image filename="elephant.jpg" caption="elephants are bigger than a breadbox"/> 
        function image = createQImage(obj,inode)
            filename = char(inode.getAttribute('filename'));
            fprintf('filename : %s\n', filename);
            caption = char(inode.getAttribute('caption'));
            fprintf('caption : %s\n', caption);
            image = QImage(filename, caption)
        end
        
        % <answer value="yes" next="COLOR"/> 
        function answer = createQAnswer(obj, anode)
            answerValue = char(anode.getAttribute('value'));
            fprintf('answerValue : %s\n', answerValue);
            nextQuestion = char(anode.getAttribute('next'));
            fprintf('nextQuestion : %s\n', nextQuestion);
            answer = QAnswer(answerValue, nextQuestion)
        end
        
        function question = findQuestionById(obj, id)
            question = QQuestion('NULL', 'NULL', 'NULL');
            for i=1:length(obj.questions)
                cur_question = obj.questions(i);
                if (strcmp(cur_question.id,id))
                    question = cur_question;
                    break;
                end
            end
        end
    end
end