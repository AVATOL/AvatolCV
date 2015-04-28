package edu.oregonstate.eecs.iis.avatolcv.questionnaire;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.iis.avatolcv.core.AvatolCVException;

public class QuestionsValidator {
    private static final String FILESEP = System.getProperty("file.separator");
	public boolean validate(List<QQuestion> questions) throws AvatolCVException {
        boolean questionCountValid = validateQuestionCount(questions);
        if (!questionCountValid){
        	throw new AvatolCVException("bad question count found during questions validation");
        }
        
        List<String> replicatedIds = getDuplicateIds(questions);
        if (replicatedIds.size() != 0){
        	throw new AvatolCVException("Duplicate key in questions file found during questions validation: " + replicatedIds.get(0));
        }
           
        List<String> unusedIds = getUnusedQuestions(questions);
        if (unusedIds.size() != 0){
        	throw new AvatolCVException("unused question found during questions validation: " + unusedIds.get(0));
        }
        
        List<String> questionMalformations = getQuestionsMalformations(questions);
        if (questionMalformations.size() != 0){
            String msg = "malformed questions : ";
            int questionMalformationCount = questionMalformations.size();
            for (String malformation : questionMalformations){
                msg = msg + " " + malformation;
            }
            throw new AvatolCVException("malformed questions " + msg);
        }
        //noLoopsDetected = obj.validateNoLoops(questions);
        //noBadNextPointers = obj.validateNoBadNextPointers(questions);
        //result = questionCountValid & idsUnique & questionsAllUsed & allQuestionsWellFormed & noLoopsDetected & noBadNextPointers;
        return true;
	}
	public List<String> getQuestionsMalformations(List<QQuestion> questions){
		List<String> malformations = new ArrayList<String>();
		for (QQuestion question : questions){
            List<String> curQuestionMalformations = getQuestionMalformations(question);
            if (curQuestionMalformations.size() != 0){
                int questionMalformationCount = curQuestionMalformations.size();
                for (String malformation : curQuestionMalformations){
                	malformations.add(malformation);
                }
            }
		}
		return malformations;
	}
	public List<String> getQuestionMalformations(QQuestion question){
		List<String> malformations = new ArrayList<String>();
		// id is not ''
        if (question.getId().equals("")){
            malformations.add("question id empty");
        }
        // text is not ''
        if (question.getText().equals("")){
            malformations.add("question text empty for " + question.getId());
        }
        // type is either choice or integer_input, not ''
        
        if (question.getType().equals("choice")){
            List<String> choiceQuestionMalformations = getChoiceQuestionMalformations(question);
            for (String mf : choiceQuestionMalformations){
            	malformations.add(mf);
            }
        }
        else if (question.getType().equals("input_integer")){
            List<String> integerQuestionMalformations = getInputIntegerQuestionMalformations(question);
            for (String mf : integerQuestionMalformations) {
                malformations.add(mf);
            }
        }
        else if (question.getType().equals("input_string")){
            List<String> stringQuestionMalformations = getInputStringQuestionMalformations(question);
            for (String mf : stringQuestionMalformations){
            	malformations.add(mf);
            }
        }
        else{
            malformations.add("question type must be either choice, input_integer, or input_string");
        }
        return malformations;
	}
	            
	public  List<String> getChoiceQuestionMalformations(QQuestion question){
        List<String >malformations = new ArrayList<String>();
        // should be more than one answer
        if (question.getAnswers().size() < 2){
            malformations.add("at least two answers required of a choice question " + question.getId());
        }
        // % answer valid
        List<String> answerMalformations = getAnswerMalformationsForChoiceQuestion(question);
        for (String mf : answerMalformations){
        	malformations.add(mf);
        }
        
        //if images, images valid
        List<String> imageMalformations = getImageMalformationsForQuestion(question);
        
        for (String mf : imageMalformations){
           malformations.add(mf);
        }
        return malformations;
	}  
	
	public List<String> getInputIntegerQuestionMalformations(QQuestion question){
	   // single answer element
	   List<String> malformations = new ArrayList<String>();
	   if (question.getAnswers().size()  != 1){
	        malformations.add("input_integer questions should have one answer " + question.getId());
	   }
	   // answer valid
	   List<String> answerMalformations = getAnswerMalformationsForInputIntegerQuestion(question);
	   for (String mf : answerMalformations){
		   malformations.add(mf);
	   }
	   //if images, images valid
	   List<String> imageMalformations = getImageMalformationsForQuestion(question);
	   for (String mf : imageMalformations){
		   malformations.add(mf);
	   }
	   return malformations;
	}

    public List<String> getInputStringQuestionMalformations( QQuestion question){
		// single answer element
    	List<String> malformations = new ArrayList<String>();
    	if (question.getAnswers().size()  != 1){
	        malformations.add("input_string questions should have one answer " + question.getId());
	   }
    	// answer valid
 	   List<String> answerMalformations = getAnswerMalformationsForInputStringQuestion(question);
 	   for (String mf : answerMalformations){
 		   malformations.add(mf);
 	   }
 	   //if images, images valid
 	   List<String> imageMalformations = getImageMalformationsForQuestion(question);
 	   for (String mf : imageMalformations){
 		   malformations.add(mf);
 	   }
 	   return malformations;
 	}
    public List<String> getAnswerMalformationsForChoiceQuestion(QQuestion question){
    	List<String> malformations = new ArrayList<String>();
    	List<QAnswer> answers = question.getAnswers();
        for (QAnswer answer : answers){
        	List<String> answerMalformations = getChoiceAnswerMalformations(answer);
        	for (String mf : answerMalformations){
        		malformations.add(mf);
        	}
        }
        return malformations;
    }
    public List<String> getChoiceAnswerMalformations(QAnswer qanswer){
    	List<String> malformations = new ArrayList<String>();
        //value not ''
        if (qanswer.getValue().equals("")){
        	malformations.add("answer value empty");
        }
        //next not ''
        if (qanswer.getNextQuestion().equals("")){
          malformations.add("answer's nextQuestion reference is empty");
        }
        return malformations;
    }
    public List<String>  getAnswerMalformationsForInputIntegerQuestion(QQuestion question){
    	List<String> malformations = new ArrayList<String>();
        QAnswer answer = question.getAnswers().get(0);
        List<String> answerMalformations = getInputIntegerAnswerMalformations(answer);
        for (String mf : answerMalformations){
        	malformations.add(mf);
        }
        return malformations;
    }
    public  List<String> getInputIntegerAnswerMalformations(QAnswer qanswer){
    	List<String> malformations = new ArrayList<String>();
        // value not ''
        if (qanswer.getValue().equals("")){
            malformations.add("answer value empty");
        }
        // next not ''
        if (qanswer.getNextQuestion().equals("")){
           malformations.add("answer nextQuestion empty");
        }
        return malformations;
    }
    
    public List<String> getAnswerMalformationsForInputStringQuestion(QQuestion question){
    	List<String> malformations = new ArrayList<String>();
        QAnswer answer = question.getAnswers().get(0);
        List<String> answerMalformations = getInputStringAnswerMalformations(answer);
        for (String mf: answerMalformations){
            malformations.add(mf);
        }
        return malformations;
    }
    
    public List<String> getInputStringAnswerMalformations(QAnswer qanswer){
    	List<String> malformations = new ArrayList<String>();
        // next not ''
        if (qanswer.getNextQuestion().equals("")){
            malformations.add("answer nextQuestion empty");
        }
        return malformations;
    }
    public List<String> getImageMalformationsForQuestion(QQuestion question){
    	List<String> malformations = new ArrayList<String>();
        List<QImage> images = question.getImages();
        for(QImage image : images){
        	List<String> imageMalformations = getImageMalformations(image);
        	for (String mf : imageMalformations){
        		malformations.add(mf);
        	}
        }
        return malformations;
    }
    
    public List<String> getImageMalformations(QImage qimage){
    	List<String> malformations = new ArrayList<String>();
        // filename not ''
    	SystemDependent sd = new SystemDependent
        String currentDir = System.getProperty("user.dir");
        String relPath = qimage.getPath();
        String imagePath = currentDir + FILESEP + relPath;
        File imageFile = new File(imagePath);
        if (qimage.getPath().equals("")){
            malformations.add("image filename empty");
        }
        else if (!imageFile.exists()){
            malformations.add("'image filename does not exist: " + imagePath);
        }
                
        // caption not ''
        if (qimage.getCaption().equals("")){
            malformations.add("image caption empty");
        }
        return malformations;
    }
    public boolean validateQuestionCount(List<QQuestion> questions){
        if (questions.size() == 0){
           return false;
    	}
    	return true;
    }
    public List<String> getDuplicateIds(List<QQuestion> questions){
        List<String> replicatedIds = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();
        for (QQuestion q: questions){
        	String newId = q.getId();
        	if (ids.contains(newId)){
        		replicatedIds.add(newId);
        	}
        	else {
        		ids.add(newId);
        	}
        }
        return replicatedIds;
    }
    public List<String> getUnusedQuestions(List<QQuestion> questions){
        List<String> futureIds = new ArrayList<String>();
        int questionsLength = questions.size();
        for (int i=1; i < questionsLength; i++){
            QQuestion question = questions.get(i);
            futureIds.add(question.getId());
        }
        for (QQuestion q : questions){
        	List<QAnswer> answers = q.getAnswers();
        	for (QAnswer a : answers){
        		String nextQuestionId = a.getNextQuestion();
        		futureIds.remove(nextQuestionId);
        	}
        }
        // remaining ones are unused
        return futureIds;
    }
}

