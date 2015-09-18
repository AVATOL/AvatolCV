package edu.oregonstate.eecs.iis.avatolcv.steps;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.SessionDataInterface;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestions;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionsXMLFile;

public class CharQuestionsStep implements Step {
    private QuestionSequencer questionSequencer = null;
    private SessionDataInterface sessionData = null;
    private String view = null;
    public CharQuestionsStep(String view, SessionDataInterface sessionData) {
        this.sessionData = sessionData;
        this.view = view;
    }
    public QuestionSequencer getQuestionSequencer(){
        return this.questionSequencer;
    }
    @Override
    public void init() throws AvatolCVException{
        QuestionsXMLFile xmlFile = new QuestionsXMLFile(this.sessionData.getCharQuestionsSourcePath());
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        List<QQuestion> qquestionList = qquestions.getQuestions();
        this.questionSequencer = new QuestionSequencer(qquestionList);
        
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.questionSequencer.persist(this.sessionData.getCharQuestionsAnsweredQuestionsPath());
    }
    @Override
    public boolean hasFollowUpDataLoadPhase() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
