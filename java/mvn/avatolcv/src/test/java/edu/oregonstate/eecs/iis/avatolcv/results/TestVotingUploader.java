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
    
    // E single taxon, three scores, two same, should use new value for winner
    
    // F single taxon, four scores, two and two -> tie, shoulr use consistent prior value
    
    // G single taxon, four scores, three to one, should use vote winner
    
    // seven taxa, one of each cases A, B, C, D, E, F, G, each should behave as before - make sure to randomize adds
    
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
