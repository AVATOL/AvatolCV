package edu.oregonstate.eecs.iis.avatolcv;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Characters {
    private List<Character> characters = new ArrayList<Character>();
    private Hashtable<String, String> characterNameForIdMap = new Hashtable<String, String>();
    private Hashtable<String, String> characterIdForNameMap = new Hashtable<String, String>();
    public Characters(Document doc) throws MorphobankDataException {
    	NodeList nodes = doc.getElementsByTagName("CategoricalCharacter");
    	for (int i = 0; i < nodes.getLength(); i++){
    		Node catCharNode = nodes.item(i);
    		Character character = new Character(catCharNode, doc);
    		String charId = character.getId();
    		String charName = character.getName();
    		//System.out.println("loading character " + charName + " is pa? " + character.isPresentAbsentCharacter());
    		characterNameForIdMap.put(charId, charName);
    		characterIdForNameMap.put(charName,charId);
    		this.characters.add(character);
    	}
    }
    
    public List<Character> getPresenceAbsenceCharacters(){
    	List<Character> paChars = new ArrayList<Character>();
    	for (Character c : this.characters){
    		if (c.isPresentAbsentCharacter()){
    			paChars.add(c);
    		}
    	}
    	return paChars;
    }
    public String getCharacterNameForId(String id){
    	return characterNameForIdMap.get(id);
    }
    public String getCharacterIdForName(String name){
    	return characterIdForNameMap.get(name);
    }
}
