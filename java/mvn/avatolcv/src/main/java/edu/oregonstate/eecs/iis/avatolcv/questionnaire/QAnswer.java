package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

public class QAnswer {
    private String value = null;
    private QQuestion nextQuestion = null;
    
    public QAnswer(String value, QQuestion nextQuestion){
        this.value = value;
        this.nextQuestion = nextQuestion;
    }
    public void setNextQuestion(QQuestion question){
        this.nextQuestion = question;
    }
    public QQuestion getNextQuestion(){
        return this.nextQuestion;
    }
    public String getValue(){
        return this.value;
    }
}
