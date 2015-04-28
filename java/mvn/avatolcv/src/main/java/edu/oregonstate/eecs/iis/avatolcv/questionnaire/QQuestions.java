package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class QQuestions {
	List<QQuestion> questions = new ArrayList<QQuestion>();
	
	public QQuestions(Node domNode) throws AvatolCVException {
		parseDomNodeIntoQuestions(domNode);
	}
	
	public List<QQuestion> getQuestions(){
		return questions;
	}
	public void parseDomNodeIntoQuestions(Node domNode) throws AvatolCVException {
		Node questionsNode = domNode.getFirstChild();
		System.out.println("docNode name " + questionsNode.getNodeName());
		NodeList questionNodesAndWhiteSpaceNodes = questionsNode.getChildNodes();
        int count = questionNodesAndWhiteSpaceNodes.getLength(); 
        for (int i = 0; i < count; i++){
            Node questionNode = questionNodesAndWhiteSpaceNodes.item(i);
            String nodeName = questionNode.getNodeName();
            if (nodeName.equals("#text")){
                // skip blank text
            }
            else {
                System.out.println("questionNode name " + questionNode.getNodeName());
                QQuestion question = createQQuestion(questionNode);
                this.questions.add(question);
            }
        }
        QuestionsValidator validator = new  QuestionsValidator();
        validator.validate(questions);
	}
	
	public QQuestion createQQuestion(Node qnode) throws AvatolCVException {
		 NodeList childNodes = qnode.getChildNodes();
         List<QAnswer> answers = new ArrayList<QAnswer>();
         List<QImage> images = new ArrayList<QImage>();
         String questionText = "";
         String questionType = "";
         String questionId = "";
         int childCount = childNodes.getLength();
         for (int i=0; i < childCount; i++){
             Node child = childNodes.item(i);
             String name = child.getNodeName();
             if (name.equals("#text")){
                 //ignore
             }
             else if (name.equals("answer")){
                 QAnswer answer = createQAnswer(child);
                 answers.add(answer);
             }
             else if (name.equals("text")){
                 questionText = child.getTextContent();
             }
             else if (name.equals("image")){
                 QImage image = createQImage(child);
                 images.add(image);
             }
             else {
            	 throw new AvatolCVException("Unrecognized element in QuestionNode " + name);
         	}
         }
         questionType = qnode.getAttributes().getNamedItem("type").getTextContent();
         questionId = qnode.getAttributes().getNamedItem("id").getTextContent();
         QQuestion question = new QQuestion(questionType, questionId, questionText);
         int answerCount = answers.size();
         for (QAnswer answer : answers){
             question.addAnswer(answer);
         }
         
         for (QImage image : images){
             question.addImage(image);
         }
         return question;
	}
	// <image filename="elephant.jpg" caption="elephants are bigger than a breadbox"/> 
	public QImage createQImage(Node inode){
		 String filename = inode.getAttributes().getNamedItem("filename").getTextContent();
         System.out.println("filename : " + filename);
         String caption = inode.getAttributes().getNamedItem("caption").getTextContent();
         System.out.println("caption : " + caption);
         QImage image = new QImage(filename, caption);
         return image;
	}
	// <answer value="yes" next="COLOR"/> 
    public  QAnswer createQAnswer(Node anode){
        String answerValue = anode.getAttributes().getNamedItem("value").getTextContent();
        System.out.println("answerValue :" +  answerValue);
        String nextQuestion = anode.getAttributes().getNamedItem("next").getTextContent();
        System.out.println("nextQuestion :" +  nextQuestion);
        QAnswer answer = new QAnswer(answerValue, nextQuestion);
        return answer;
    }
    
}
