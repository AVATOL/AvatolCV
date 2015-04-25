package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

public class AnsweredQuestion {
    private String questionID = null;
    private String answer = null;
    public AnsweredQuestion(String questionID, String answer){
        this.questionID = questionID;
        this.answer = answer;
    }
    public String getQuestionID(){
        return this.questionID;
    }
    public String getAnswer(){
        return this.answer;
    }
}
