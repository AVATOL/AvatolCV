classdef QuestionsValidator < handle
    properties
        
    end
    methods
        function obj = QuestionsValidator()
        end

        function result = validate(obj, questions)
            questionCountValid = obj.validateQuestionCount(questions);
            if (not(questionCountValid))
                msg = sprintf('Invalid number of questions.');
                err = MException('Validate:BadQuestionsCount', msg);
                throw(err)
            end
            replicatedIds = obj.getDuplicateIds(questions);
            if (not(isempty(replicatedIds)))
                msg = sprintf('Duplicate key in questions file: %s', replicatedId);
                err = MException('Validate:DupicateId', msg);
                throw(err)
            end
            unusedIds = obj.getUnusedQuestions(questions);
            if (not(isempty(unusedIds)))
                msg = sprintf('unused questions : %s', unusedIds);
                err = MException('Validate:UnusedIds', msg);
                throw(err)
            end
            questionMalformations = obj.getQuestionsMalformations(questions);
            if (not(isempty(questionMalformations)))
                msg = 'malformed questions : ';
                for i=1:length(questionMalformations)
                    msg = sprintf('%s%s ', msg, questionMalformations{i});
                end
                err = MException('Validate:QuestionMalformed', msg);
                throw(err)
            end
            %noLoopsDetected = obj.validateNoLoops(questions);
            %noBadNextPointers = obj.validateNoBadNextPointers(questions);
            %result = questionCountValid & idsUnique & questionsAllUsed & allQuestionsWellFormed & noLoopsDetected & noBadNextPointers;
        end
        
        function malformations = getQuestionsMalformations(obj,questions)
            malformations = {};
            for i=1:length(questions)
                question = questions(i);
                curQuestionMalformations = obj.getQuestionMalformations(question)
                if (not(isempty(curQuestionMalformations)))
                    for j=1,length(curQuestionMalformations)
                        malformations = [ malformations, curQuestionMalformations(j) ];
                    end
                end
                
             end
        end
        
        function malformations = getQuestionMalformations(obj, question)
            malformations = {}
            % id is not ''
            if (strcmp(question.id,''))
                malformations = [ malformations, 'question id empty' ];
            end
            % text is not ''
            if (strcmp(question.text,''))
                textError = sprintf('question text empty for %s',question.id);
                malformations = [ malformations, textError ];
            end
            % type is either choice or integer_input, not ''
            
            if (strcmp(question.type,'choice'))
                choiceQuestionMalformations = obj.getChoiceQuestionMalformations(question)
                if (not(isempty(choiceQuestionMalformations)))
                    for i=1,length(choiceQuestionMalformations)
                        malformations =  [ malformations, choiceQuestionMalformations(i) ];
                    end
                end
            elseif (strcmp(question.type,'input_integer'))
                integerQuestionMalformations = obj.getInputIntegerQuestionMalformations(question);
                if (not(isempty(integerQuestionMalformations)))
                    for i=1,length(integerQuestionMalformations)
                        malformations =  [ malformations, integerQuestionMalformations(i) ];
                    end
                end
            elseif (strcmp(question.type,'input_string'))
                stringQuestionMalformations = obj.getInputStringQuestionMalformations(question);
                if (not(isempty(stringQuestionMalformations)))
                    for i=1,length(stringQuestionMalformations)
                        malformations =  [ malformations, stringQuestionMalformations(i) ];
                    end
                end
            else
                typeError = 'question type must be either choice, input_integer, or input_string';
                malformations = [ malformations, typeError ];
            end
        end
      
        function malformations = getChoiceQuestionMalformations(obj, question)
            malformations = {}
            % more than one answer
            answerCount = length(question.answers);
            if (answerCount < 2)
                tooFewAnswerError = sprintf('at least two answers required of a choice question, not %s',int2str(answerCount));
                malformations = [ malformations,  tooFewAnswerError ];
            end
             % answer valid
            answerMalformations = obj.getAnswerMalformationsForChoiceQuestion(question);
            if (not(isempty(answerMalformations)))
                for i=1,length(answerMalformations)
                    malformations =  [ malformations, answerMalformations(i) ];
                end
            end
            
            % if images, images valid
            imageMalformations = obj.getImageMalformationsForQuestion(question);
            if (not(isempty(imageMalformations)))
                for i=1,length(imageMalformations)
                    malformations =  [ malformations, imageMalformations(i) ];
                end
            end
        end
        
        function malformations = getInputIntegerQuestionMalformations(obj, question)
            % single answer element
            malformations = {}
            answerCount = length(question.answers);
            if (answerCount ~= 1)
                singleAnwserError = sprintf('input_integer questions should have one answer: %s', question.id);
                malformations = [ malformations,  singleAnwserError ];
            end
            % answer valid
            answerMalformations = obj.getAnswerMalformationsForInputIntegerQuestion(question);
            if (not(isempty(answerMalformations)))
                for i=1,length(answerMalformations)
                    malformations =  [ malformations, answerMalformations(i) ];
                end
            end
            
            % if images, images valid
            imageMalformations = obj.getImageMalformationsForQuestion(question);
            if (not(isempty(imageMalformations)))
                for i=1,length(imageMalformations)
                    malformations =  [ malformations, imageMalformations(i) ];
                end
            end
        end
        
        
        function malformations = getInputStringQuestionMalformations(obj, question)
            % single answer element
            malformations = {}
            answerCount = length(question.answers);
            if (answerCount ~= 1)
                singleAnwserError = sprintf('input_string questions should have one answer: %s', question.id);
                malformations = [ malformations,  singleAnwserError ];
            end
            % answer valid
            answerMalformations = obj.getAnswerMalformationsForInputStringQuestion(question);
            if (not(isempty(answerMalformations)))
                for i=1,length(answerMalformations)
                    malformations =  [ malformations, answerMalformations(i) ];
                end
            end
            
            % if images, images valid
            imageMalformations = obj.getImageMalformationsForQuestion(question);
            if (not(isempty(imageMalformations)))
                for i=1,length(imageMalformations)
                    malformations =  [ malformations, imageMalformations(i) ];
                end
            end
        end
        
        
        
        function malformations = getAnswerMalformationsForChoiceQuestion(obj, question)
            malformations = {};
            answerCount = length(question.answers);
            for i=1:answerCount
                answer = question.answers(i);
                answerMalformations = obj.getChoiceAnswerMalformations(answer);
                if (not(isempty(answerMalformations)))
                    for j=1,length(answerMalformations)
                        malformations = [ malformations, answerMalformations(j) ];
                    end
                end
            end
        end
         function malformations = getChoiceAnswerMalformations(obj, qanswer)
            malformations = {}
            % value not ''
            if (strcmp(qanswer.value,''))
                malformations = [ malformations, 'answer value empty' ];
            end
            % next not ''
            if (strcmp(qanswer.nextQuestion,''))
                malformations = [ malformations, 'answer nextQuestion empty' ];
            end
        end
        
        
        
        
        function malformations = getAnswerMalformationsForInputIntegerQuestion(obj, question)
            malformations = {};
            answer = question.answers(1);
            answerMalformations = obj.getInputIntegerAnswerMalformations(answer);
            if (not(isempty(answerMalformations)))
                for j=1,length(answerMalformations)
                    malformations = [ malformations, answerMalformations(j) ];
                end
            end
        end
        
        function malformations = getInputIntegerAnswerMalformations(obj, qanswer)
            malformations = {}
            % value not ''
            if (strcmp(qanswer.value,''))
                malformations = [ malformations, 'answer value empty' ];
            end
            % next not ''
            if (strcmp(qanswer.nextQuestion,''))
                malformations = [ malformations, 'answer nextQuestion empty' ];
            end
        end
        
        
        
        
        function malformations = getAnswerMalformationsForInputStringQuestion(obj, question)
            malformations = {};
            answer = question.answers(1);
            answerMalformations = obj.getInputStringAnswerMalformations(answer);
            if (not(isempty(answerMalformations)))
                for j=1,length(answerMalformations)
                    malformations = [ malformations, answerMalformations(j) ];
                end
            end
        end
        
        function malformations = getInputStringAnswerMalformations(obj, qanswer)
            malformations = {}
            % next not ''
            if (strcmp(qanswer.nextQuestion,''))
                malformations = [ malformations, 'answer nextQuestion empty' ];
            end
        end
        
        
        
        
        function malformations = getImageMalformationsForQuestion(obj, question)
            malformations = {};
            imageCount = length(question.images);
            if (imageCount > 0)
                for i=1:imageCount
                    image = question.images(i);
                    imageMalformations = obj.getImageMalformations(image);
                    if (not(isempty(imageMalformations)))
                        for j=1,length(imageMalformations)
                            malformations = [ malformations, imageMalformations(j) ];
                        end
                    end
                end
            end
        end
        
        function malformations = getImageMalformations(obj, qimage)
            malformations = {}
            % filename not ''
            currentDir = pwd();
            relPath = qimage.imageFilePath;
            imagePath = sprintf('%s/%s',currentDir, relPath);
            if (strcmp(qimage.imageFilePath,''))
                malformations = [ malformations,  'image filename empty'];
            elseif exist(imagePath, 'file') ~= 2
                error = sprintf('image filename does not exist: %s',relPath);
                malformations = [ malformations, error ] ;
            end
                
            % caption not ''
            if (strcmp(qimage.imageCaption,''))
                malformations = [ malformations, 'image caption empty' ];
            end
        end
        
       
        
        
        
        function result = validateQuestionCount(obj, questions)
            result = true;
            if (length(questions) == 0)
                result = false;
            end
        end
        
        function replicatedIds = getDuplicateIds(obj, questions)
            replicatedIds = {};
            ids = {}
            for i=1:length(questions)
                question = questions(i);
                newId = question.id
                if (ismember( newId, ids))
                    replicatedIds = [ replicatedIds, newId ];
                else
                    ids = [ ids, newId ]
                end
            end
        end
        
        function unusedIds = getUnusedQuestions(obj, questions)
           ids = {};
           for i=2:length(questions)
               question = questions(i);
               ids = [ ids, question.id ]
           end
           for i=1:length(questions)
               question = questions(i);
               answersLength = length(question.answers);
               for j=1:answersLength
                   qanswer = question.answers(j);
                   nextQuestionId = qanswer.nextQuestion;
                   ids = obj.removeMatchStringFromList(nextQuestionId, ids);
               end
           end
           unusedIds = ids;
        end
        
        function newList = removeMatchStringFromList(obj, s, oldList)
            newList = {};
            for i=1:length(oldList)
                member = oldList(i);
                if (not(strcmp(member,s)))
                    newList = [ newList, member ];
                end
            end
        end
    end
end
