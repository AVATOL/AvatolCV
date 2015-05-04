package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class QQuestion {
    public static final String INPUT_TYPE_INTEGER = "input_integer";
    public static final String INPUT_TYPE_STRING = "input_string";
    public static final String INPUT_TYPE_CHOICE = "choice";
    public static final String NO_MORE_QUESTIONS_TYPE = "NO_MORE_QUESTIONS";
    private String type = null;
    private String id = null;
    private String text = null;
    private List<QAnswer> answers = new ArrayList<QAnswer>();
    private List<QImage> images = new ArrayList<QImage>();
    public QQuestion(String type, String id, String text) throws AvatolCVException {
    	if (!(   type.equals(NO_MORE_QUESTIONS_TYPE) || 
    			 type.equals(INPUT_TYPE_INTEGER) || 
    			 type.equals(INPUT_TYPE_STRING) || 
    			 type.equals(INPUT_TYPE_CHOICE)  )){
    		throw new AvatolCVException("bad type given for QQuestion " + type + " + must be " + INPUT_TYPE_INTEGER + " or " + INPUT_TYPE_STRING + " or " + INPUT_TYPE_CHOICE);
    	}
        this.type = type;
        this.id = id;
        this.text = text;
    }
    public String getText(){
    	return text;
    }
    public String getId(){
    	return id;
    }
    public String getType(){
    	return this.type;
    }
    public List<QAnswer> getAnswers(){
    	return this.answers;
    }
    public List<QImage> getImages(){
    	return this.images;
    }
    public void addAnswer(QAnswer answer){
        answers.add(answer);
    }
    public void addImage(QImage image){
        images.add(image);
    }
    
    public QAnswerIntegrity getAnswerIntegrity(String givenAnswer) throws AvatolCVException {
        if (this.type.equals(INPUT_TYPE_INTEGER)){
            try {
                Integer integer = new Integer(givenAnswer);
                int value = integer.intValue();
                return new QAnswerIntegrity(true,"ok");
            }
            catch(Exception e){
                return new QAnswerIntegrity(false, "Answer must be valid integer.");
            }
        }
        else if (this.type.equals(INPUT_TYPE_STRING)){
            if (isStringValid(givenAnswer)){
                return new QAnswerIntegrity(true,"ok");
            }
            else {
                return new QAnswerIntegrity(false,"Answer must be non-empty character string.");
            }
        }
        else { // (this.type.equals(INPUT_TYPE_CHOICE))
            for (QAnswer answer : this.answers){
                if (givenAnswer.equals(answer.getValue())){
                    return new QAnswerIntegrity(true,"ok");
                }
            }
            return new QAnswerIntegrity(false,"Given choice not valid answer for question: " + givenAnswer);
        }
    }
    
    public boolean isStringValid(String s){
        String result = s.replaceAll("\n","");
        result = result.replaceAll("\t", "");
        result = result.replaceAll(" ", "");
        if (result.equals("")){
            return false;
        }
        return true;
    }
    public QAnswer getQAnswerForAnswerValue(String value) throws AvatolCVException{
        if (this.type.equals(INPUT_TYPE_INTEGER)){
            return this.answers.get(0);
        }
        else if (this.type.equals(INPUT_TYPE_STRING)){
            return this.answers.get(0);
        }
        else if (this.type.equals(INPUT_TYPE_CHOICE)){
            return getQAnswerForChoiceAnswerValue(value);
        }
        else {
            throw new AvatolCVException("unknown question type " + this.type + " for question " + this.id);
        }
    }
    public QAnswer getQAnswerForChoiceAnswerValue(String value){
        QAnswer result = null;
        for (QAnswer answer : this.answers){
            if (answer.getValue().equals(value)){
                result = answer;
                break;
            }
        }
        return result;
    }
}
