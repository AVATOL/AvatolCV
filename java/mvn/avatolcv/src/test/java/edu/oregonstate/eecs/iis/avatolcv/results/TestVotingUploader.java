package edu.oregonstate.eecs.iis.avatolcv.results;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVConstants;
import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestVotingUploader extends TestCase {
    // A single taxon, one score only, prior answer empty string
    public void testVotingUploaderSimplestSingleItemNewBlankPriorAnswer(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
 // A single taxon, one score only, prior answer ?
    public void testVotingUploaderSimplestSingleItemNewQuestionMarkPriorAnswer(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue(AvatolCVConstants.UNDETERMINED)));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
 // A single taxon, one score only, prior answer null
    public void testVotingUploaderSimplestSingleItemNewNullPriorAnswer(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), null));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
 // A single taxon, one score only, prior answer exists
    public void testVotingUploaderSimplestSingleItemRevise(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
    
    
    // B single taxon, two scores, conflicting prior value should throw exception
    public void testVotingUploaderConflictingPriorValues(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal2")));
            vu.vote();
            Assert.fail("should have thrown exception on inconsistent ttConcern prior values");
            
        }
        catch(AvatolCVException e){
            Assert.assertTrue(true);
        }
    }
    
    
    
    // C single taxon, two scores, different, cause tie, should use consistent prior value
    public void testVotingUploaderTwoCauseTie(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(ScoreItem.ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
    
    
    // D single taxon, two scores, both new values same, should use that new value
    public void testVotingUploaderTwoCauseWinnerPriorValueExists(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
 // D single taxon, two scores, both new values same, should use that new value
    public void testVotingUploaderTwoCauseWinnerPriorValueNull(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), null));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), null));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // D single taxon, two scores, both new values same, should use that new value
    public void testVotingUploaderTwoCauseWinnerPriorValueEmptyString(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
 // D single taxon, two scores, both new values same, should use that new value
    public void testVotingUploaderTwoCauseWinnerPriorValueQuestionMark(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue(AvatolCVConstants.UNDETERMINED)));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue(AvatolCVConstants.UNDETERMINED)));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
    
    
    
    // E single taxon, three scores, two same, should use new value for winner
    public void testVotingUploaderThreeCauseWinnerYesPriorValue(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // E single taxon, three scores, two same, should use new value for winner
    public void testVotingUploaderThreeCauseWinnerNoPriorValue(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
    // F single taxon, four scores, two and two -> tie, should use consistent prior value
    public void testVotingUploaderFourMakesTie(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"),  new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im4", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(ScoreItem.ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // G single taxon, four scores, three to one, should use vote winner
    public void testVotingUploaderFourWinnerPriorValueExists(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im4", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal2"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    public void testVotingUploaderFourWinnerNoPriorValue(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im4", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("")));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal2"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // seven taxa, one of each cases A, C, D, E, F, G, each should behave as before - make sure to randomize adds
    public void testVotingUploaderAllTogether(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            //
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonB"), new NormalizedValue("taxonBNewVal1"), new NormalizedValue("taxonBPriorVal1")));
            vu.addScore(new ScoreItem("im3", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonB"), new NormalizedValue("taxonBNewVal2"), new NormalizedValue("taxonBPriorVal1")));
            //
            vu.addScore(new ScoreItem("im4", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonC"), new NormalizedValue("taxonCNewVal1"), new NormalizedValue("taxonCPriorVal1")));
            vu.addScore(new ScoreItem("im5", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonC"), new NormalizedValue("taxonCNewVal1"), new NormalizedValue("taxonCPriorVal1")));
            //
            vu.addScore(new ScoreItem("im6", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im7", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal1"), new NormalizedValue("")));
            vu.addScore(new ScoreItem("im8", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal2"), new NormalizedValue("")));
            //
            vu.addScore(new ScoreItem("im9", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal1"), new NormalizedValue("taxonEPriorVal1")));
            vu.addScore(new ScoreItem("im10", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal1"), new NormalizedValue("taxonEPriorVal1")));
            vu.addScore(new ScoreItem("im11", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal2"), new NormalizedValue("taxonEPriorVal1")));
            vu.addScore(new ScoreItem("im12", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal2"), new NormalizedValue("taxonEPriorVal1")));
            //
            vu.addScore(new ScoreItem("im13", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal1"), new NormalizedValue("taxonFPriorVal1")));
            vu.addScore(new ScoreItem("im14", new NormalizedKey("char1"),  new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1")));
            vu.addScore(new ScoreItem("im15", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1")));
            vu.addScore(new ScoreItem("im16", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1")));
            //
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonA")).getScoringFate());
            
            Assert.assertEquals(ScoreItem.ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE, vu.getVoteWinner(new NormalizedValue("taxonB")).getScoringFate());
            
            Assert.assertEquals(new NormalizedValue("taxonCNewVal1"), vu.getVoteWinner(new NormalizedValue("taxonC")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonC")).getScoringFate());
            
            Assert.assertEquals(new NormalizedValue("taxonDNewVal1"), vu.getVoteWinner(new NormalizedValue("taxonD")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.SET_NEW_VALUE, vu.getVoteWinner(new NormalizedValue("taxonD")).getScoringFate());
            
            Assert.assertEquals(ScoreItem.ScoringFate.ABSTAIN_FROM_CHANGING_SCORE_VOTE_TIE, vu.getVoteWinner(new NormalizedValue("taxonE")).getScoringFate());
            
            Assert.assertEquals(new NormalizedValue("taxonFNewVal2"), vu.getVoteWinner(new NormalizedValue("taxonF")).getNewValue());
            Assert.assertEquals(ScoreItem.ScoringFate.REVISE_VALUE, vu.getVoteWinner(new NormalizedValue("taxonF")).getScoringFate());
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // H mixed trainTest concerns passed to VotingUploader - should throw exception 
    public void testVotingUploaderConflictingTTConcerns(){
        try {
            UploadVoter vu = new UploadVoter(null, null, null);
            vu.addScore(new ScoreItem("im1", new NormalizedKey("char1"), new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1")));
            vu.addScore(new ScoreItem("im2", new NormalizedKey("char1"), new NormalizedKey("klaxon"), new NormalizedValue("klaxonA"), new NormalizedValue("klaxonANewVal1"), new NormalizedValue("klaxonAPriorVal1")));
            Assert.fail("should have thrown exception on inconsistent ttConcern");
            
        }
        catch(AvatolCVException e){
            Assert.assertTrue(true);
        }
    }
}
