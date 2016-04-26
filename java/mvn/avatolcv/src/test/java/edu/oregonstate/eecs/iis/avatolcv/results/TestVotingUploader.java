package edu.oregonstate.eecs.iis.avatolcv.results;

import edu.oregonstate.eecs.iis.avatolcv.AvatolCVException;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedKey;
import edu.oregonstate.eecs.iis.avatolcv.normalized.NormalizedValue;
import junit.framework.Assert;
import junit.framework.TestCase;

public class TestVotingUploader extends TestCase {
    // A single taxon, one score only
    public void testVotingUploaderSimplestSingleItem(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // B single taxon, two scores, conflicting prior value should throw exception
    public void testVotingUploaderConflictingPriorValues(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal2"));
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
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonAPriorVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // D single taxon, two scores, both new values same, should use that new value
    public void testVotingUploaderTwoCauseWinner(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    
    // E single taxon, three scores, two same, should use new value for winner
    public void testVotingUploaderThreeCauseWinner(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im3", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // F single taxon, four scores, two and two -> tie, should use consistent prior value
    public void testVotingUploaderFourMakesTie(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im3", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im4", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonAPriorVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // G single taxon, four scores, three to one, should use vote winner
    public void testVotingUploaderFourWinner(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im3", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im4", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal2"), new NormalizedValue("taxonAPriorVal1"));
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal2"), vu.getVoteWinner(new NormalizedValue("taxonA")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // seven taxa, one of each cases A, C, D, E, F, G, each should behave as before - make sure to randomize adds
    public void testVotingUploaderAllTogether(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            //
            vu.addScore("im2", new NormalizedKey("taxon"), new NormalizedValue("taxonB"), new NormalizedValue("taxonBNewVal1"), new NormalizedValue("taxonBPriorVal1"));
            vu.addScore("im3", new NormalizedKey("taxon"), new NormalizedValue("taxonB"), new NormalizedValue("taxonBNewVal2"), new NormalizedValue("taxonBPriorVal1"));
            //
            vu.addScore("im4", new NormalizedKey("taxon"), new NormalizedValue("taxonC"), new NormalizedValue("taxonCNewVal1"), new NormalizedValue("taxonCPriorVal1"));
            vu.addScore("im5", new NormalizedKey("taxon"), new NormalizedValue("taxonC"), new NormalizedValue("taxonCNewVal1"), new NormalizedValue("taxonCPriorVal1"));
            //
            vu.addScore("im6", new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal1"), new NormalizedValue("taxonDPriorVal1"));
            vu.addScore("im7", new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal1"), new NormalizedValue("taxonDPriorVal1"));
            vu.addScore("im8", new NormalizedKey("taxon"), new NormalizedValue("taxonD"), new NormalizedValue("taxonDNewVal2"), new NormalizedValue("taxonDPriorVal1"));
            //
            vu.addScore("im9", new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal1"), new NormalizedValue("taxonEPriorVal1"));
            vu.addScore("im10", new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal1"), new NormalizedValue("taxonEPriorVal1"));
            vu.addScore("im11", new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal2"), new NormalizedValue("taxonEPriorVal1"));
            vu.addScore("im12", new NormalizedKey("taxon"), new NormalizedValue("taxonE"), new NormalizedValue("taxonENewVal2"), new NormalizedValue("taxonEPriorVal1"));
            //
            vu.addScore("im13", new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal1"), new NormalizedValue("taxonFPriorVal1"));
            vu.addScore("im14", new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1"));
            vu.addScore("im15", new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1"));
            vu.addScore("im16", new NormalizedKey("taxon"), new NormalizedValue("taxonF"), new NormalizedValue("taxonFNewVal2"), new NormalizedValue("taxonFPriorVal1"));
            //
            vu.vote();
            Assert.assertEquals(new NormalizedValue("taxonANewVal1"), vu.getVoteWinner(new NormalizedValue("taxonA")));
            Assert.assertEquals(new NormalizedValue("taxonBPriorVal1"), vu.getVoteWinner(new NormalizedValue("taxonB")));
            Assert.assertEquals(new NormalizedValue("taxonCNewVal1"), vu.getVoteWinner(new NormalizedValue("taxonC")));
            Assert.assertEquals(new NormalizedValue("taxonDNewVal1"), vu.getVoteWinner(new NormalizedValue("taxonD")));
            Assert.assertEquals(new NormalizedValue("taxonEPriorVal1"), vu.getVoteWinner(new NormalizedValue("taxonE")));
            Assert.assertEquals(new NormalizedValue("taxonFNewVal2"), vu.getVoteWinner(new NormalizedValue("taxonF")));
        }
        catch(AvatolCVException e){
            Assert.fail(e.getMessage());
        }
    }
    // H mixed trainTest concerns passed to VotingUploader - should throw exception 
    public void testVotingUploaderConflictingTTConcerns(){
        try {
            VotingUploader vu = new VotingUploader(null, null, null);
            vu.addScore("im1", new NormalizedKey("taxon"), new NormalizedValue("taxonA"), new NormalizedValue("taxonANewVal1"), new NormalizedValue("taxonAPriorVal1"));
            vu.addScore("im2", new NormalizedKey("klaxon"), new NormalizedValue("klaxonA"), new NormalizedValue("klaxonANewVal1"), new NormalizedValue("klaxonAPriorVal1"));
            Assert.fail("should have thrown exception on inconsistent ttConcern");
            
        }
        catch(AvatolCVException e){
            Assert.assertTrue(true);
        }
    }
}
