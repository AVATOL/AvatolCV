classdef QuestionSequencer < handle
    properties
        nextAnswerIndex;
        qquestions;
        currentQuestion;
        answeredQuestions = {};
        noMoreQuestionsMarker;
        characterName = 'UNDEFINED';
    end
    methods
        
        function obj = QuestionSequencer(qquestions)
            obj.qquestions = qquestions;
            obj.nextAnswerIndex = 1;
            obj.currentQuestion = obj.qquestions.questions(1);
            obj.noMoreQuestionsMarker = QQuestion('NO_MORE_QUESTIONS','NO_MORE_QUESTIONS','NO_MORE_QUESTIONS');
        end
        
        function persist(obj)
            resultsDirExists = exist('results', 'dir');
            if (not(resultsDirExists))
                mkdir('results');
            end
            curDir = pwd();
            filepath = sprintf('%s/results/%s.out',curDir,obj.characterName);
            disp(filepath);
            fileID = fopen(filepath,'w');
            for i=1:length(obj.answeredQuestions)
                aq = obj.answeredQuestions(i);
                fprintf(fileID,'%s=%s\n',aq.questionID, aq.answer);
            end
            fprintf(fileID,'\n'); % not sure why I needed to add an extra linefeed, but if didn't, final linefeed doesn't express
            fclose(fileID);
        end
        
        function currentQuestion = getCurrentQuestion(obj)
            currentQuestion = obj.currentQuestion;
        end
        
        function existingAnswerToNextQuestion = answerQuestionAfterBackup(obj, answer)
            existingAnswerToNextQuestion = 'NOT_YET_SPECIFIED';
            % check to see if the incoming answer is the same as the
            % existing answer
            existingAnsweredQuestion = obj.answeredQuestions(obj.nextAnswerIndex);
            if strcmp(existingAnsweredQuestion.answer,answer)
                % same answer given, any later AnsweredQuestions still
                % valid, and can reuse existing AnsweredQuestion
                obj.nextAnswerIndex = obj.nextAnswerIndex + 1;
                % check to see if this is the final question
                qAnswer = obj.currentQuestion.getQAnswerForAnswerValue(answer);
                nextQuestionId = qAnswer.nextQuestion;
                if (strcmp(nextQuestionId, 'NO_MORE_QUESTIONS'))
                    %final question is being reanswered the same
                    obj.currentQuestion = obj.noMoreQuestionsMarker;
                    existingAnswerToNextQuestion = 'NOT_YET_SPECIFIED';
                else 
                    % non-final question being answered the same; look up answer to reuse 
                    answerQuestionCount = length(obj.answeredQuestions);
                    if (obj.nextAnswerIndex > answerQuestionCount)
                        %revisiting the last question we already answered
                        existingAnswerToNextQuestion = 'NOT_YET_SPECIFIED';
                    else
                        % we have later answers we reused
                        existingAnsweredNextQuestion = obj.answeredQuestions(obj.nextAnswerIndex);
                        existingAnswerToNextQuestion = existingAnsweredNextQuestion.answer;
                    end
                    obj.currentQuestion = obj.findQuestionById(nextQuestionId);
                end
            else
                % different answer given, invalidating later answers, flush
                % later answer starting at obj.nextAnswerindex
                indexOfLastToSave = obj.nextAnswerIndex - 1;
                obj.answeredQuestions = obj.answeredQuestions(1:indexOfLastToSave);
                existingAnswerToNextQuestion = obj.answerQuestion(answer);
            end
        end
        
        function existingAnswerToNextQuestion = answerQuestion(obj, answer)
            existingAnswerToNextQuestion = 'NOT_YET_SPECIFIED';
            if (not(obj.currentQuestion.isValidAnswer(answer)))
                message = sprintf('invalid answer %s given to question %s', answer, obj.currentQuestion.id);
                exception = MException('QuestionSequencer:IllegalAnswer', message);
                throw(exception);
            end 
            answerQuestionCount = length(obj.answeredQuestions);
            if (obj.nextAnswerIndex <= answerQuestionCount)
                existingAnswerToNextQuestion = obj.answerQuestionAfterBackup(answer);
            else 
                % we ensured answer validity above so assume it here
                aq = AnsweredQuestion(obj.currentQuestion.id, answer);
                obj.answeredQuestions = [ obj.answeredQuestions, aq ];
                obj.nextAnswerIndex = obj.nextAnswerIndex + 1;
                qAnswer = obj.currentQuestion.getQAnswerForAnswerValue(answer);
                nextQuestionId = qAnswer.nextQuestion;
                if (strcmp(nextQuestionId,'NO_MORE_QUESTIONS'))
                    obj.currentQuestion = obj.noMoreQuestionsMarker;
                else
                    nextQuestion = obj.findQuestionById(nextQuestionId);
                    if (strcmp(nextQuestion.id,'NULL'))
                        message = sprintf('could not find question %s in qquestions', nextQuestionId);
                        exception = MException('QuestionSequencer:UnknownQuestionId', message);
                        throw(exception);
                    else
                        obj.currentQuestion = nextQuestion;
                    end
                end
            end
        end
        
        function result = isAllQuestionsAnswered(obj)
            result = false;
            if (strcmp(obj.currentQuestion.id,'NO_MORE_QUESTIONS'))
                result = true;
            end
        end
        
        function question = findQuestionById(obj, id)
            question = obj.qquestions.findQuestionById(id);
        end
        
        function result = canBackUp(obj)
            result = false;
            answerCount = obj.nextAnswerIndex;
            if (answerCount > 1) % don't use zero due to sspecial boundary action with CharacterQuestion
                result = true;
            end
        end
        
        function prevAnswerToQuestion = backUp(obj)
            obj.nextAnswerIndex = obj.nextAnswerIndex - 1;
            prevAnswerToQuestion = obj.answeredQuestions(obj.nextAnswerIndex);
            %obj.answeredQuestions = obj.answeredQuestions(1:length(obj.answeredQuestions) - 1);
            obj.currentQuestion = obj.findQuestionById(prevAnswerToQuestion.questionID);
        end
    end
    
end