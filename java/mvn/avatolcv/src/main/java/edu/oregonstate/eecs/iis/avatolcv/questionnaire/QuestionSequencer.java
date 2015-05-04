package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.SystemDirs;
import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class QuestionSequencer {
	private static final String FILESEP = System.getProperty("file.separator");
	private static final String NL = System.getProperty("line.separator");
    private int nextAnswerIndex = 0;
    private List<QQuestion> qquestions;
    private QQuestion currentQuestion;
    private List<AnsweredQuestion> answeredQuestions = new ArrayList<AnsweredQuestion>();
    private QQuestion noMoreQuestionsMarker;
    private String matrixName = "UNDEFINED";
    private String characterName = "UNDEFINED";
    
    public QuestionSequencer(List<QQuestion> qquestions) throws AvatolCVException {
    	this.qquestions = qquestions;
        this.nextAnswerIndex = 1;
        this.currentQuestion = this.qquestions.get(0);
        this.noMoreQuestionsMarker = new QQuestion(QQuestion.NO_MORE_QUESTIONS_TYPE,"NO_MORE_QUESTIONS","NO_MORE_QUESTIONS");
    }
    public List<AnsweredQuestion> getAnsweredQuestions(){
        return this.answeredQuestions;
    }
    public int getNextAnswerIndex(){
        return this.nextAnswerIndex;
    }
    public void persist() throws AvatolCVException {
    	File f = new File("results");
    	if (!f.isDirectory()){
    		f.mkdirs();
    	}
    	String parentPath = SystemDirs.getUserAnswerDir();
    	String filepath = parentPath + FILESEP + this.characterName + ".out";
        
        File parentPathDir = new File(parentPath);
        if (!parentPathDir.exists()){
        	parentPathDir.mkdir();
        }
        try{
        	BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        	for (AnsweredQuestion aq : this.answeredQuestions){
        		writer.write(aq.getQuestionID() + "=" + aq.getAnswer() + NL);
        	}
        	writer.close();
        }
        catch(IOException ioe){
        	throw new AvatolCVException("problem writing answer file");
        }
    }
    
    public QQuestion getCurrentQuestion(){
    	return this.currentQuestion;
    }
    public String answerQuestionAfterBackup(String answer) throws AvatolCVException {
    	String existingAnswerToNextQuestion = "NOT_YET_SPECIFIED";
        // check to see if the incoming answer is the same as the
        // existing answer
        AnsweredQuestion existingAnsweredQuestion = this.answeredQuestions.get(nextAnswerIndex);
        if (existingAnsweredQuestion.getAnswer().equals(answer)){
            // same answer given, any later AnsweredQuestions still
            // valid, and can reuse existing AnsweredQuestion
            this.nextAnswerIndex = this.nextAnswerIndex + 1;
            // check to see if this is the final question
            QAnswer qAnswer = this.currentQuestion.getQAnswerForAnswerValue(answer);
            String nextQuestionId = qAnswer.getNextQuestion();
            if (nextQuestionId.equals("NO_MORE_QUESTIONS")){
                // final question is being reanswered the same
                this.currentQuestion = noMoreQuestionsMarker;
                existingAnswerToNextQuestion = "NOT_YET_SPECIFIED";
            }
            else {
                // non-final question being answered the same; look up answer to reuse 
                int answerQuestionCount = answeredQuestions.size();
                if (this.nextAnswerIndex > answerQuestionCount){
                    // revisiting the last question we already answered
                    existingAnswerToNextQuestion = "NOT_YET_SPECIFIED";
            	}
                else {
                    //% we have later answers we reused
                    AnsweredQuestion existingAnsweredNextQuestion = this.answeredQuestions.get(nextAnswerIndex);
                    existingAnswerToNextQuestion = existingAnsweredNextQuestion.getAnswer();
            	}
                this.currentQuestion = findQuestionById(nextQuestionId);
            }
        }
        else {
            // different answer given, invalidating later answers, flush
            // later answer starting at obj.nextAnswerindex
            int indexOfLastToSave = this.nextAnswerIndex;  // OR  this.nextAnswerIndex - 1?
            this.answeredQuestions = this.answeredQuestions.subList(0,indexOfLastToSave);
            existingAnswerToNextQuestion = answerQuestion(answer);
        }
        return existingAnswerToNextQuestion;
    }
    
    public QQuestion findQuestionById(String id) throws AvatolCVException {
    	 QQuestion question = null;
         for (QQuestion aQuestion : this.qquestions){
             
             if (aQuestion.getId().equals(id)) {
                 question = aQuestion;
                 break;
             }
         }
         if (null == question){
        	 throw new AvatolCVException("no question with id " + id + " could be found");
         }
         return question;
    }
    
    public String answerQuestion(String answer) throws AvatolCVException {
        String existingAnswerToNextQuestion = "NOT_YET_SPECIFIED";
        if (!(this.currentQuestion.getAnswerIntegrity(answer).isValid())){
        	throw new AvatolCVException("invalid answer " + answer + " given to question " + this.currentQuestion.getId());
        } 
        int answerQuestionCount = answeredQuestions.size();
        if (this.nextAnswerIndex <= answerQuestionCount){
            existingAnswerToNextQuestion = this.answerQuestionAfterBackup(answer);
        }
        else {
            //% we ensured answer validity above so assume it here
            AnsweredQuestion aq = new AnsweredQuestion(this.currentQuestion.getId(), answer);
            this.answeredQuestions.add(aq);
            this.nextAnswerIndex = this.nextAnswerIndex + 1;
            QAnswer qAnswer = this.currentQuestion.getQAnswerForAnswerValue(answer);
            String nextQuestionId = qAnswer.getNextQuestion();
            if (nextQuestionId.equals("NO_MORE_QUESTIONS")) {
                this.currentQuestion = this.noMoreQuestionsMarker;
            }
            else {
                QQuestion nextQuestion = findQuestionById(nextQuestionId);
                this.currentQuestion = nextQuestion;
            }
        }
        return existingAnswerToNextQuestion;
    }
    
    public boolean isAllQuestionsAnswered(){
        boolean result = false;
        if (this.currentQuestion.getId().equals("NO_MORE_QUESTIONS")){
                result = true;
        }
    	return result;
	}
    public boolean canBackUp(){
        boolean result = false;
        int answerCount = this.nextAnswerIndex + 1;
        if (answerCount > 1){ // don't use zero due to sspecial boundary action with CharacterQuestion
            result = true;
        }
    	return result;
    }
    public AnsweredQuestion backUp() throws AvatolCVException {
    	this.nextAnswerIndex = this.nextAnswerIndex - 1;
        AnsweredQuestion prevAnswerToQuestion = this.answeredQuestions.get(this.nextAnswerIndex);
        this.currentQuestion = findQuestionById(prevAnswerToQuestion.getQuestionID());
        return prevAnswerToQuestion;
    } 
}

