package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

public class QAnswer {
    private String value = null;
    private String nextQuestion = null;
    
    public QAnswer(String value, String nextQuestion){
        this.value = value;
        this.nextQuestion = nextQuestion;
    }
    public void setNextQuestion(String question){
        this.nextQuestion = question;
    }
    public String getNextQuestion(){
        return this.nextQuestion;
    }
    public String getValue(){
        return this.value;
    }
}
