package edu.oregonstate.eecs.iis.avatolcv.generic;

import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.core.Step;
import edu.oregonstate.eecs.iis.avatolcv.core.View;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestion;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QQuestions;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionSequencer;
import edu.oregonstate.eecs.iis.avatolcv.questionnaire.QuestionsXMLFile;

public class CharQuestionsStep implements Step {
    private QuestionSequencer questionSequencer = null;
    private String questionsFilePath = null;
    private View view = null;
    public CharQuestionsStep(View view, String questionsFilePath) {
        this.questionsFilePath = questionsFilePath;
        this.view = view;
    }
    public QuestionSequencer getQuestionSequencer(){
        return this.questionSequencer;
    }
    @Override
    public void init() throws AvatolCVException{
        QuestionsXMLFile xmlFile = new QuestionsXMLFile(this.questionsFilePath);
        QQuestions qquestions = new QQuestions(xmlFile.getDomNode());
        List<QQuestion> qquestionList = qquestions.getQuestions();
        this.questionSequencer = new QuestionSequencer(qquestionList);
        LEFT OFF HERE - we need to pass in a path.  Where do we want the answers?  Needs to be under a dir named for chosen char
    }
    @Override
    public void consumeProvidedData() throws AvatolCVException {
        this.questionSequencer.persist();
    }


    @Override
    public View getView() {
        /*
         * For charQuestions, the view will be a container that fills itself out with subPanel appropriate for current QQuestion
         */
        return this.view;
    }

}
